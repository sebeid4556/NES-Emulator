package nes;

//the view
public class DebugPool {
	
	//artificial data		
	public static int PRG_UNIT_SIZE = 0x4000;
	public static int CODE_START = 0x8000;
	
	public static final int FLAG_C = 0;	//carry
	public static final int FLAG_Z = 1;	//zero
	public static final int FLAG_I = 2;	//interrupt disable
	public static final int FLAG_D = 3;	//decimal
	public static final int FLAG_B = 4;	//B flag
	public static final int FLAG_1 = 5;	//always pushed as 1
	public static final int FLAG_V = 6;	//overflow
	public static final int FLAG_N = 7;	//negative
	
	public static final int CTRL_VRAM_ADDR_INC 		= 2;
	public static final int CTRL_SP_ADDR 			= 3;
	public static final int CTRL_BG_ADDR 			= 4;
	public static final int CTRL_SP_SIZE 			= 5;
	public static final int CTRL_MASTER_SLAVE 		= 6;
	public static final int CTRL_NMI 				= 7;
	
	public static final int MASK_GRAY_SCALE 		= 0;
	public static final int MASK_SHOW_BG_LEFT 		= 1;
	public static final int MASK_SHOW_SP_LEFT 		= 2;
	public static final int MASK_SHOW_BG 			= 3;
	public static final int MASK_SHOW_SP 			= 4;
	public static final int MASK_TINT_RED 			= 5;
	public static final int MASK_TINT_GREEN			= 6;
	public static final int MASK_TINT_BLUE 			= 7;
	
	public static final int STATUS_SP_OVERFLOW 		= 5;
	public static final int STATUS_SP_ZERO_HIT 		= 6;
	public static final int STATUS_VBLANK 			= 7;
	
	public static int PRG_size;
	
	public static int[] frameBuffer;
	
	public static boolean updateMemorySpace = false;
	public static byte[] CPU_MEMORY_SPACE = new byte[0x10000];
	
	//data snapshots
	public static byte[] RAM;
	public static byte[] ROM;
	
	public static byte[] patternTable = new byte[0x2000];
	
	public static byte[] nametable = new byte[0x1000];
	public static int nametableMirror;
	
	public static int ir;
	public static int cycle;
	public static int cycleTotal;
	public static int operand;
	public static boolean NMI = false;
	public static boolean IRQ = false;
	public static byte A;
	public static byte X;
	public static byte Y;
	public static byte P;
	public static byte SP;
	public static short PC;
	
	public static int dot;
	public static int scanline;
	
	public static byte PPUCTRL;	//W, $2000	NOTE: After power/reset, writes to this register are ignored for about 30,000 cycles. 
	public static byte PPUMASK;	//R, $2001
	public static byte PPUSTATUS;	//R, $2002
	public static byte OAMADDR;	//W, $2003
	public static byte OAMDATA;	//RW,$2004
	public static byte PPUSCROLL;	//W(x2), $2005
	public static byte PPUADDR;	//W(x2), $2006
	public static byte PPUDATA;	//RW,$2007
	public static byte OAMDMA;		//W, $4014
	
	public static short v;
	public static short t;
	public static byte fineX;
	/*public static int coarseX;
	public static int coarseY;
	public static int ntSelect;
	public static byte fineX;
	public static byte fineY;*/
	
	//=================================================================================================
	//STATIC METHODS
	//=================================================================================================
	
	public static int coarseX(short s)
	{
		return (Util.UINT16(s) & 0b000000000011111);
	}
	
	public static int coarseY(short s)
	{
		return (Util.UINT16(s) & 0b000001111100000) >>> 5;
	}
	
	public static int NtSelect(short s)
	{
		return (Util.UINT16(s) & 0b000110000000000) >>> 10;
	}
	
	public static int fineY(short s)
	{
		return (Util.UINT16(s) & 0b111000000000000) >>> 12;
	}
	
	public static void requestCPUMemorySpaceSnapshot()
	{
		updateMemorySpace = true;
	}
		
}
