package nes;

public class Ppu
{
	public final int VRAM_SIZE = 0x4000;	//16KB of INTERNAL VRAM
	public final int PATTERN_TABLE_ENTRY_SIZE = 0x1000;
	public final int PATTERN_TABLE_SIZE = 0x2000;
	public final int NAMETABLE_SIZE = 0x1000;
	public final int PALETTE_SIZE = 0x20;
	public final int OAM_SIZE = 64 * 4;
	public final int SECONDARY_OAM_SIZE = 8 * 4;
	
	private final int PATTERN_TABLE_START = 0x0000;
	private final int NAMETABLE_START = 0x2000;
	private final int PALETTE_START = 0x3F00;
	
	public final int NAMETABLE_MIRROR_HORIZONTAL = 0;
	public final int NAMETABLE_MIRROR_VERTICAL = 1;
	
	/*VRAM*/
	//public byte[] VRAM = new byte[VRAM_SIZE];
	private byte[] PATTERN_TABLE = new byte[PATTERN_TABLE_SIZE];
	private byte[] NAMETABLE = new byte[NAMETABLE_SIZE];
	private byte[] PALETTE = new byte[PALETTE_SIZE];	
	public byte[] OAM = new byte[OAM_SIZE];
	private byte[] SECONDARY_OAM = new byte[SECONDARY_OAM_SIZE];
	
	/*MAPPED PPU REGISTERS*/
	public byte PPUCTRL;	//W, $2000	NOTE: After power/reset, writes to this register are ignored for about 30,000 cycles. 
	public byte PPUMASK;	//R, $2001
	public byte PPUSTATUS;	//R, $2002
	public byte OAMADDR;	//W, $2003
	public byte OAMDATA;	//RW,$2004
	public byte PPUSCROLL;	//W(x2), $2005
	public byte PPUADDR;	//W(x2), $2006
	public byte PPUDATA;	//RW,$2007
	public byte OAMDMA;		//W, $4014
	
	public final int CTRL_VRAM_ADDR_INC 		= 2;
	public final int CTRL_SP_ADDR 				= 3;
	public final int CTRL_BG_ADDR 				= 4;
	public final int CTRL_SP_SIZE 				= 5;
	public final int CTRL_MASTER_SLAVE 			= 6;
	public final int CTRL_NMI 					= 7;
	
	public final int MASK_GRAY_SCALE 			= 0;
	public final int MASK_SHOW_BG_LEFT 			= 1;
	public final int MASK_SHOW_SP_LEFT 			= 2;
	public final int MASK_SHOW_BG 				= 3;
	public final int MASK_SHOW_SP 				= 4;
	public final int MASK_TINT_RED 				= 5;
	public final int MASK_TINT_GREEN 			= 6;
	public final int MASK_TINT_BLUE 			= 7;
	
	public final int STATUS_SP_OVERFLOW 		= 5;
	public final int STATUS_SP_ZERO_HIT 		= 6;
	public final int STATUS_VBLANK 				= 7;
	
	private byte ioBus;	//Bus used to communicate with CPU
	private byte vramBus;	//Internal bus used for VRAM		
	
	/*PPU INTERNAL REGISTERS*/
	public short v;	//Current VRAM address (15-bits)
	public short t;	//Temporary VRAM address (15-bits)
	public byte x;	//Fine X scroll (3-bits)
	public byte w;	//First or second write toggle (1-bit)	
	
	private int n;	//which 4-byte OAM sprite entry
	private int m;	//which byte of the sprite entry
	private boolean found = false;	//was a sprite been found in range?
	private boolean full = false;	//is Secondary OAM
	private boolean done = false;
	private int oamCounter = 0;
	private int oamNumFound = 0;
	
	private byte NT;	//nametable id of next tile
	private byte AT;	//attribute of next tile
	private byte BG_Lsb;	//BG lsb bitplane of next tile
	private byte BG_Msb;	//BG msb bitplane of next tile
	
	private byte atrLatchLo;	//hold 1-bit attribute lsb
	private byte atrLatchHi;	//same but msb
	private byte atrNextQuad;	//figure out which quadrant of the attribute entry the next tile belongs to
	
	private short lsbShifter;	//shift register for BG lsb
	private short msbShifter;	//same for BG msb
	private byte atrShifterLo;	//8-bit shifter for attribute lsb
	private byte atrShifterHi;	//smae for msb
	
	public short vramAddr;	//internal vram address (UNUSED)
	private int vramCtrl;	//read or write
	
	private byte dataBuffer;	//for the delayed read from $2007
	
	private final int VRAM_CTRL_WRITE = 0;
	private final int VRAM_CTRL_READ = 1;
	
	private int nametableMirror;	//mirroring mode specified in rom
	private boolean chrRAM;
	
	public final int PRE_RENDER_SCANLINE = 261;
	public final int VISIBLE_SCANLINE_START = 0;
	public final int VISIBLE_SCANLINE_END = 239;
	public final int POST_RENDER_SCANLINE = 240;
	public final int VBLANK_SCANLINE_START = 241;
	public final int VBLANK_SCANLINE_END = 260;
	
	public int scanline;
	public int dot;
	
	private boolean odd = false;
	
	public boolean NMI = false;
	public boolean suppressNMI = false;	//suppress the next NMI?
	public boolean suppressVBL = false;	//suppress setting the vblank flag on the next iteration?
	
	private int frame = 0;
	
	private boolean debugUpdatePool = false;
	
	private Rom ROM;
	
	Ppu(Rom rObj)
	{
		ROM = rObj;
		nametableMirror = ROM.mirror;
		chrRAM = (ROM.CHR_units == ROM.CHR_RAM);
		
		scanline = PRE_RENDER_SCANLINE;
		dot = 0;
		
		if(ROM.CHR_units > 0) loadPatternTable();	
		if(debugUpdatePool) updateDebugPool();
	}
	
	public void setUpdateDebugPool(boolean b)
	{
		debugUpdatePool = b;
	}
	
	private void loadPatternTable()
	{
		for(int i = 0; i < PATTERN_TABLE_SIZE; i++)
			PATTERN_TABLE[i] = ROM.BUFFER[ROM.PRG_size + i];		
	}
	
	//updates the data in the debug pool
	public void updateDebugPool()
	{
		DebugPool.patternTable = PATTERN_TABLE;
		DebugPool.nametable = NAMETABLE;
		DebugPool.nametableMirror = nametableMirror;
		DebugPool.dot = dot;
		DebugPool.scanline = scanline;
		DebugPool.PPUCTRL = PPUCTRL;
		DebugPool.v = v;
		DebugPool.t = t;
		DebugPool.fineX = x;
		
		DebugPool.frameBuffer = Screen.frameBuffer;
	}
	
	public boolean shouldDrawFrame()
	{
		return ((scanline == POST_RENDER_SCANLINE) && (dot == 1));
	}
	
	public boolean isVBL()
	{
		if(scanline >= VBLANK_SCANLINE_START && scanline <= PRE_RENDER_SCANLINE)
		{
			if(scanline == VBLANK_SCANLINE_START && dot < 1)
			{
				return false;
			}
			else if(scanline == PRE_RENDER_SCANLINE && dot >= 1)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}
	
	private void internalBusOp()
	{
		int addr = UINT16(vramAddr) & 0x3FFF;
		if(addr >= 0x0000)
		{
			if(addr < 0x2000)	//pattern table
			{
				if(vramCtrl == VRAM_CTRL_READ) vramBus = PATTERN_TABLE[(addr % PATTERN_TABLE_SIZE)];
				if(vramCtrl == VRAM_CTRL_WRITE)
				{
					if(chrRAM)
					{
						PATTERN_TABLE[(addr % PATTERN_TABLE_SIZE)] = vramBus;
					}
				}
				return;
			}
			else if(addr < 0x3F00)	//nametable
			{
				addr &= 0x2FFF;
				short mirroredAddr = 0x0000;
				
				if(nametableMirror == NAMETABLE_MIRROR_HORIZONTAL)				
				{					
					//2000 => 00 / 800 = 0, 0 * 0x800 = 0, 0 + 0 = 0
					//2050 => 50 / 800 = 0 => *0x800 == 0 + 0x50 == 2050
					//2150 => 0x150 / 0x800 = 0, 0 * 0x800 = 0, 0 + 0x150 = 0x150
					//2450 => 450 / 800 = 0, 0 * 0x800 = 0, 0 + 50 = 2050
					//2850 => 0x850 / 0x800 = 1, 1 * 0x800 = 0x800, 0x800 + 0x50 = 0x850
					//2a50 => 0xa50 / 0x800 = 1, 1 * 0x800 = 0x800, 0x800 + 0x50 = 0x850					
					//mirroredAddr += (((addr - 0x2000) / 0x800) * 0x800) + (addr & 0x3FF);
					mirroredAddr = (short) (((addr >> 1) & 0x400) | (addr & 0x3FF));
					//System.out.println(String.format("\t\tinternalBusOp(): addr = $%X, mirroredAddr = $%X", addr&0x3FF, UINT16(mirroredAddr)));
				}
				else if(nametableMirror == NAMETABLE_MIRROR_VERTICAL)				
				{											
					//0x2050 => 0x2000 + (0x2050 % 0x800)
					//mirroredAddr += (addr % 0x800);
					mirroredAddr = (short)(addr & 0x7FF);
				}
				else
				{
					System.out.println("internalBusOp(): FATAL ERROR: unimplemented mirroring configuration.");
					System.exit(0);
				}
				
				if(vramCtrl == VRAM_CTRL_READ) vramBus = NAMETABLE[UINT16(mirroredAddr)];
				else if(vramCtrl == VRAM_CTRL_WRITE) NAMETABLE[UINT16(mirroredAddr)] = vramBus;				
				return;
			}			
			else if(addr >= 0x3F00 && addr < 0x4000)	//palette
			{
				int paddr = addr & 0x1F;	//mirror
				//writing to $3F10 is the same as writing to $3F00 and so on
				if(addr % 0x4 == 0) paddr = (paddr % 0x10);
				if(vramCtrl == VRAM_CTRL_READ) vramBus = (byte)(UINT8(PALETTE[(paddr % PALETTE_SIZE)]) & 0x63);
				else if(vramCtrl == VRAM_CTRL_WRITE) PALETTE[(paddr % PALETTE_SIZE)] = vramBus;
				return;
			}
		}
		else
		{
			System.out.println(String.format("internalBusOp(): FATAL ERROR: attempting to access VRAM at $%X", addr));
			System.exit(0);
		}
		return;
	}
	
	public int BIT(byte b, int t)
	{
		int bit = t % 8;
		return (((UINT8(b) & (1 << bit)) >>> bit) & 1);
	}
	
	public byte BIT(byte b, int t, int n)
	{
		b = (byte) (0x00 | (UINT8(b) & (0xFF ^ (1 << t))));	//zero the bit
		b = (byte) (UINT8(b) | ((n & 1) << t));	//set to either 0 or 1
		return b;
	}
	
	/*Read byte from VRAM*/
	public byte BYTE(short addr)
	{	
		/*Set bus values*/
		vramAddr = addr;
		vramCtrl = VRAM_CTRL_READ;		
		internalBusOp();	//set DATA		
		return vramBus;
	}	
	
	/*Write byte to VRAM*/
	public void BYTE(short addr, byte data)
	{		
		/*Set bus values*/
		vramAddr = addr;
		vramCtrl = VRAM_CTRL_WRITE;
		vramBus = data;			
		internalBusOp();
	}
	
	public static byte HIGH(short s)
	{
		return (byte)(UINT16(s) >>> 8);
	}
	
	public static byte LOW(short s)
	{
		return (byte)(UINT16(s) & 0xFF);
	}		
	
	public static short COMBINE16(byte lo, byte hi)
	{
		return (short)((UINT8(hi) << 8) | UINT8(lo));
	}

	public static int UINT8(byte b)
	{
		return (b & 0xFF);
	}
	
	public static int UINT16(short s)
	{
		return (s & 0xFFFF);
	}
	
	//==============================================================================
	public int STATUS(int bit)
	{
		return ((UINT8(PPUSTATUS) >>> bit) & 0x1);
	}
	
	public void STATUS(int bit, int data)
	{
		PPUSTATUS = (byte) (0x00 | (UINT8(PPUSTATUS) & (0xFF ^ (1 << bit))));	//zero the bit
		PPUSTATUS = (byte) (UINT8(PPUSTATUS) | ((data & 1) << bit));	//set to either 0 or 1
	}
	
	public int CTRL(int bit)
	{
		return BIT(PPUCTRL, bit);		
	}
	
	public void CTRL(int bit, int data)
	{
		PPUCTRL = (byte) (0x00 | (UINT8(PPUCTRL) & (0xFF ^ (1 << bit))));	//zero the bit
		PPUCTRL = (byte) (UINT8(PPUCTRL) | ((data & 1) << bit));	//set to either 0 or 1				
	}
	
	public int MASK(int bit)
	{
		return BIT(PPUMASK, bit);
	}
	
	public void MASK(int bit, int data)
	{
		PPUMASK = (byte) (0x00 | (UINT8(PPUMASK) & (0xFF ^ (1 << bit))));	//zero the bit
		PPUMASK = (byte) (UINT8(PPUMASK) | ((data & 1) << bit));	//set to either 0 or 1				
	}
	
	public byte OAMDATA()
	{		
		return OAM[UINT8(OAMADDR)];
	}
	
	public void OAMDATA(byte b)
	{
		OAM[UINT8(OAMADDR)] = b;
		OAMADDR++;
	}
	
	//UNUSED
	public void PPUADDR(byte b)
	{
		PPUADDR = b;
		if(UINT8(w) == 0)	//first
		{
			//v = (short)((UINT16(v) & 0x00FF) | (UINT8(b) << 8));	//set upper byte
			v = (short)((UINT16(v) & 0x00FF) | (UINT8(b) << 8));	//set upper byte
			w = 1;
		}
		else if(UINT8(w) == 1)	//second
		{
			//v = (short)((UINT16(v) & 0xFF00) | (UINT8(b)));		//set lower byte
			v = (short)((UINT16(v) & 0xFF00) | (UINT8(b)));		//set lower byte
			w = 0;
		}
	}
	
	public byte PPUDATA()	//read from PPUDATA
	{		
		
		//PPUDATA reads are delayed by one read
		byte b = dataBuffer;	//previous content of the data buffer
		dataBuffer = BYTE(v);	//set data buffer to the newly read data
		
		//update v
		if(CTRL(CTRL_VRAM_ADDR_INC) == 0) v++;
		else v += 32;
		return b;
	}
	
	public void PPUDATA(byte b)	//write to PPUDATA
	{
		PPUDATA = b;
		BYTE(v, b);
		//update v
		if(CTRL(CTRL_VRAM_ADDR_INC) == 0) v++;
		else v += 32;
	}
	//==============================================================================
	
	private boolean rendering()
	{
		return (MASK(MASK_SHOW_BG) == 1 || MASK(MASK_SHOW_SP) == 1);
	}

	//==============================================================================
	
	private int coarseX()
	{
		return (UINT16(v) & 0b000000000011111);
	}

	private void coarseX(int x)
	{
		v = (short)((UINT16(v) & 0b111111111100000) | (x & 0b00011111));
	}
	
	private int coarseY()
	{
		return (UINT16(v) & 0b000001111100000) >>> 5;
	}
	
	private void coarseY(int y)
	{
		v = (short)((UINT16(v) & 0b111110000011111) | ((y & 0b00011111) << 5));
	}
	
	private int NtSelect()
	{
		return (UINT16(v) & 0b000110000000000) >>> 10;
	}
	
	private void NtSelect(int n)
	{
		v = (short)((UINT16(v) & 0b111001111111111) | ((n & 0b00000011) << 10));
	}
	
	private int fineY()
	{
		return (UINT16(v) & 0b111000000000000) >>> 12;
	}
	
	private void fineY(int f)
	{
		v = (short)((UINT16(v) & 0b000111111111111) | ((f & 0b00000111) << 12));
	}
	
	private void inc_hori()
	{
		if(!rendering()) return;
		if(coarseX() < 31)
		{
			coarseX(coarseX() + 1);
		}
		else
		{
			coarseX(0);
			//NtSelect(~(NtSelect() & 0b01));
			NtSelect((NtSelect() & 0b01) ^ 0b01);
		}
	}
	
	private void inc_vert()
	{
		if(!rendering()) return;
		if(fineY() < 7)
		{
			fineY(fineY() + 1);
		}
		else	//next tile
		{
			fineY(0);
			if(coarseY() == 29)
			{
				coarseY(0);
				NtSelect((NtSelect() & 0b10) ^ 0b10);
			}
			else if(coarseY() == 31)
			{
				coarseY(0);
			}
			else
			{
				coarseY(coarseY() + 1);
			}
			/*if(coarseY() < 29)
			{
				coarseY(coarseY() + 1);
			}
			else if(coarseY() == 31)
			{				
				coarseY(0);
			}
			else
			{
				coarseY(0);
				//NtSelect(~(NtSelect() & 0b10));				
				NtSelect((NtSelect() & 0b10) ^ 0b10);				
			}*/
		}
	}
	
	private void transfer_hori()
	{
		if(rendering())
		{
			coarseX(UINT16(t) & 0b11111);
			NtSelect((UINT16(t) & 0b010000000000) >>> 10);
		}
	}
	
	private void transfer_vert()
	{
		if(rendering()) 
		{
			coarseY((UINT16(t) & 0b1111100000) >>> 5);
			NtSelect((UINT16(t) & 0b100000000000) >>> 10);
			fineY((UINT16(t) & 0b111000000000000) >>> 12);
		}
	}
	
	//==============================================================================
	
	private int spriteY(int s)
	{
		return UINT8(OAM[(s & 0x1f) * 0x4]);
	}
	
	private int spriteTileID(int s)
	{
		return UINT8(OAM[((s & 0x1f) * 0x4) + 1]);
	}
	
	private int spriteAtr(int s)
	{
		return UINT8(OAM[((s & 0x1f) * 0x4) + 2]);
	}
	
	private int spriteX(int s)
	{
		return UINT8(OAM[((s & 0x1f) * 0x4) + 3]);
	}
	
	//==============================================================================
	
	private byte getTileMsb(byte nt)
	{
		int tile = UINT8(nt);
		return BYTE((short)((PATTERN_TABLE_ENTRY_SIZE * CTRL(CTRL_BG_ADDR)) + (tile * 16) + fineY() + 8));
	}
	
	private byte getTileLsb(byte nt)
	{
		int tile = UINT8(nt);
		return BYTE((short)((PATTERN_TABLE_ENTRY_SIZE * CTRL(CTRL_BG_ADDR)) + (tile * 16) + fineY()));
	}
	
	private int getPixel()
	{
		int lsb = (UINT16(lsbShifter) & (0x8000 >>> x)) >>> (15 - x) & 1;
		int msb = (UINT16(msbShifter) & (0x8000 >>> x)) >>> (15 - x) & 1;
		int paletteLo = (UINT8(atrShifterLo) & (0x80 >>> x)) >>> (7 - x) & 1;
		int paletteHi = (UINT8(atrShifterHi) & (0x80 >>> x)) >>> (7 - x) & 1;
		int palette = ((paletteHi << 1) | paletteLo) & 0b11;
		int color = ((msb << 1) | lsb) & 0b11;		
		
		int paletteIndex = ((palette << 2) | color);
		if((paletteIndex % 0x04) == 0) paletteIndex = 0;
		return UINT8(PALETTE[paletteIndex]) & 0b111111;
	}
	
	//==============================================================================
	
	private void updateShifters()
	{
		if(!rendering()) return;
		lsbShifter <<= 1;
		msbShifter <<= 1;
		atrShifterLo = (byte) ((UINT8(atrShifterLo) << 1) | UINT8(atrLatchLo));
		atrShifterHi = (byte) ((UINT8(atrShifterHi) << 1) | UINT8(atrLatchHi));
	}
	
	private void reloadShifters()
	{
		if(!rendering()) return;
		lsbShifter = (short)((UINT16(lsbShifter) & 0xFF00) | UINT8(BG_Lsb));
		msbShifter = (short)((UINT16(msbShifter) & 0xFF00) | UINT8(BG_Msb));
		atrLatchLo = (byte)(UINT8(atrNextQuad) & 0b01);
		atrLatchHi = (byte)((UINT8(atrNextQuad) & 0b10) >> 1);
	}
	
	//==============================================================================
	
	private void clearSecondaryOAM()
	{
		if(dot % 2 == 0)	//clear(0xFF) every other cycle
		{
			SECONDARY_OAM[(dot / 2) - 1] = (byte) 0xFF;
		}
		else
		{
			;
		}
	}
	
	private void spriteEvaluation()
	{
		if(dot >= 1 && dot <= 64)
		{
			clearSecondaryOAM();
		}
		else if(dot >= 65 && dot <= 256)
		{
			if(!done)
			{
				if(odd)	//READ CYCLE
				{
					int y = spriteY(n);				
					if((scanline >= y) && (scanline < (y + 8)))	//Sprite within range
					{
						found = true;
						oamCounter = 1;
						oamNumFound++;
						if(oamNumFound == 9)	//If more than 8 sprites have been found
						{
							full = true;	//Disable writes to SECONDARY OAM
						}
					}
					else
					{
						n++;	//If sprite is not in range, increment n
						if(n == 0)
						{
							done = true;
						}
					}
				}
				else	//WRITE CYCLE
				{				
					//Copy first byte into SECONDARY OAM even if sprite not within range
					if(!full)	//but only if SECONDARY OAM isn't full yet
					{
						SECONDARY_OAM[((oamNumFound - 1) * 4)] = OAM[n * 4 + 0];
						if(found)
						{
							/*Copy bytes 1 thru 3 into SECONDARY OAM*/
							int index = n * 4 + oamCounter;					
							SECONDARY_OAM[((oamNumFound - 1) * 4) + oamCounter] = OAM[index];
							oamCounter++;
							if(oamCounter == 4)	//Finished OAM entry
							{
								found = false;
								n++;	//Next OAM entry
							}
						}
					}
				}
			}
			else	//All 64 sprites have been evaluated
			{
				;
			}
		}
		else if(dot >= 257 && dot <= 320)
		{
			switch(dot % 8)
			{
				case 0:
					;
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
					
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
			}
		}
	}
	
	//==============================================================================
	
	private void updateFrameBuffer()
	{
		if(dot >= 1 && dot <= 256)	//picture region
		{					
			Screen.frameBuffer[(scanline * 256) + (dot-1)] = getPixel();					
		}
	}
	
	//==============================================================================
	
	public void CYCLE()
	{		
		if(GuiController.pause) return;
		if(debugUpdatePool) updateDebugPool();
		
		//System.out.println(String.format("\tPPU.CYCLE(%d, %d):", scanline, dot));
		//System.out.println(String.format("PPUMASK: BG: %d, SP: %d", MASK(MASK_SHOW_BG), MASK(MASK_SHOW_SP)));
		
		
		if((scanline >= VISIBLE_SCANLINE_START && scanline <= VISIBLE_SCANLINE_END) || scanline == PRE_RENDER_SCANLINE)	//visible lines
		{
			if(scanline == PRE_RENDER_SCANLINE)	//pre-render line
			{
				if(dot == 1)
				{
					//System.out.println(String.format("\tPPU.CYCLE(%d, %d): VBLANK OVER! ============================", scanline, dot));
					if(CTRL(CTRL_NMI) == 1)	//NOT SURE IF WILL CHECK NMI ENABLE BEFORE CLEARING VBLANK FLAG!!
					{
						NMI = false;					
					}				
					STATUS(STATUS_VBLANK, 0);
				}
				else if(dot >= 280 && dot <= 304)
				{
					transfer_vert();
				}
				else if(dot == 340)
				{
					if(odd)
					{
						if(rendering())					
						{
							//System.out.println(String.format("SKIPPING LAST DOT: frame %d", frame));
							dot = 0;
							scanline = 0;
							odd = false;
							frame++;
							return;
						}										
					}										
				}
			}
			if(scanline >= VISIBLE_SCANLINE_START && scanline <= VISIBLE_SCANLINE_END)
			{
				spriteEvaluation();
				
				updateFrameBuffer();
			}
			if((dot >= 1 && dot <= 256) || (dot >= 321 && dot <= 336))
			{			
				updateShifters();
				switch(dot % 8)
				{
					case 0:	//BG MSB						
						BG_Msb = getTileMsb(NT);
						reloadShifters();
						inc_hori();						
						break;
					case 2:	//NT   						
						NT = BYTE((short)((0x2000 | (UINT16(v) & 0xFFF))));						
						break;
					case 4:	//AT
						AT = BYTE((short)(0x23C0 | (NtSelect() << 10) | ((coarseY() >>> 2) << 3) | (coarseX() >> 2)));						
						int quad = ((coarseY() & 0x2) << 1) | ((coarseX() & 0x2));
						atrNextQuad = (byte) ((UINT8(AT) >> quad) & 0b11);
						break;
					case 6:	//BG LSB
						BG_Lsb = getTileLsb(NT);
						break;					
				}
				if(dot == 256)
				{
					inc_vert();
				}
			}
			else if(dot == 257)
			{
				reloadShifters();
				transfer_hori();
			}
		}
		else if(scanline == POST_RENDER_SCANLINE)	//post render line
		{
			;
		}
		else if(scanline >= VBLANK_SCANLINE_START && scanline <= VBLANK_SCANLINE_END)	//vertical blank
		{
			if(scanline == VBLANK_SCANLINE_START && dot == 1)
			{
				//System.out.println(String.format("\tPPU.CYCLE(%d, %d): VBLANK START! ============================", scanline, dot));
				if(suppressNMI)
				{
					//System.out.println("NMI SUPPRESSED");
					suppressNMI = false;
				}
				else
				{
					if(CTRL(CTRL_NMI) == 1)
					{					
						NMI = true;
					}
				}
				if(suppressVBL)
				{
					//System.out.println(String.format("VBLANK FLAG SET SUPPRESSED (%d, %d, frame %d)", scanline, dot, frame));
					suppressVBL = false;
				}
				else
				{
					STATUS(STATUS_VBLANK, 1);
				}
				
			}			
						
		}	
				
		dot++;
		dot %= (340 + 1);
		if(dot % (340 + 1) == 0)
		{
			//System.out.println(String.format("\tPPU.CYCLE(): scanline = %d", scanline));
			scanline++;
			if(scanline >= (PRE_RENDER_SCANLINE + 1))	//new frame
			{
				odd = !odd;
				frame++;
			}
			scanline %= (PRE_RENDER_SCANLINE + 1);
		}
	}
	
}
