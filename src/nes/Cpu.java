package nes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Cpu 
{	
	public static final short PRG_START = (short)0x8000;
	public static final short STACK_START = (short)0x100;
	
	private final int RAM_SIZE = 0x800;	//2KB of INTERNAL RAM
	
	private final int TOTAL_LEGAL_INSTRUCTIONS = 151;
	private final int TOTAL_ALL_INSTRUCTIONS = 256;
	
	/*OPCODES*/
	private final int BRK_IMP 		= 0x00;
	private final int ORA_IND_X 	= 0x01;
	private final int ORA_Z 		= 0X05;
	private final int ASL_Z			= 0x06;
	private final int PHP_IMP		= 0x08;
	private final int ORA_IMM 		= 0x09;
	private final int ASL_A			= 0x0A;
	private final int ORA_ABS 		= 0x0D;
	private final int ASL_ABS		= 0x0E;
	
	private final int BPL_REL 		= 0x10;
	private final int ORA_IND_Y		= 0x11;
	private final int ORA_Z_X		= 0x15;
	private final int ASL_Z_X		= 0x16;
	private final int CLC_IMP		= 0x18;
	private final int ORA_ABS_Y		= 0x19;
	private final int ORA_ABS_X		= 0x1D;
	private final int ASL_ABS_X		= 0x1E;
	
	private final int JSR_ABS		= 0x20;
	private final int AND_IND_X		= 0x21;
	private final int BIT_Z			= 0x24;
	private final int AND_Z			= 0x25;
	private final int ROL_Z			= 0x26;
	private final int PLP_IMP		= 0x28;
	private final int AND_IMM		= 0x29;
	private final int ROL_A			= 0x2A;
	private final int BIT_ABS		= 0x2C;
	private final int AND_ABS		= 0x2D;
	private final int ROL_ABS		= 0x2E;
	
	private final int BMI_REL		= 0x30;
	private final int AND_IND_Y		= 0x31;
	private final int AND_Z_X		= 0x35;
	private final int ROL_Z_X		= 0x36;
	private final int SEC_IMP		= 0x38;
	private final int AND_ABS_Y		= 0x39;
	private final int AND_ABS_X		= 0x3D;
	private final int ROL_ABS_X		= 0x3E;
	
	private final int RTI_IMP		= 0x40;
	private final int EOR_IND_X		= 0x41;
	private final int EOR_Z			= 0x45;
	private final int LSR_Z			= 0x46;
	private final int PHA_IMP		= 0x48;
	private final int EOR_IMM		= 0x49;
	private final int LSR_A			= 0x4A;
	private final int JMP_ABS		= 0x4C;
	private final int EOR_ABS 		= 0x4D;
	private final int LSR_ABS		= 0x4E;
	
	private final int BVC_REL		= 0x50;
	private final int EOR_IND_Y		= 0x51;
	private final int EOR_Z_X		= 0x55;
	private final int LSR_Z_X		= 0x56;
	private final int CLI_IMP		= 0x58;
	private final int EOR_ABS_Y		= 0x59;
	private final int EOR_ABS_X		= 0x5D;
	private final int LSR_ABS_X		= 0x5E;
	
	private final int RTS_IMP		= 0x60;
	private final int ADC_IND_X		= 0x61;
	private final int ADC_Z			= 0x65;
	private final int ROR_Z			= 0x66;
	private final int PLA_IMP		= 0x68;
	private final int ADC_IMM		= 0X69;
	private final int ROR_A			= 0x6A;
	private final int JMP_IND		= 0x6C;
	private final int ADC_ABS		= 0x6D;
	private final int ROR_ABS		= 0x6E;
	
	private final int BVS_REL		= 0x70;
	private final int ADC_IND_Y		= 0x71;
	private final int ADC_Z_X		= 0x75;
	private final int ROR_Z_X		= 0x76;
	private final int SEI_IMP		= 0x78;
	private final int ADC_ABS_Y		= 0x79;
	private final int ADC_ABS_X		= 0x7D;
	private final int ROR_ABS_X		= 0x7E;
	
	private final int STA_IND_X		= 0x81;
	private final int STY_Z			= 0x84;
	private final int STA_Z			= 0x85;
	private final int STX_Z			= 0x86;
	private final int DEY_IMP		= 0x88;
	private final int TXA_IMP		= 0x8A;
	private final int STY_ABS		= 0x8C;
	private final int STA_ABS	 	= 0x8D;
	private final int STX_ABS		= 0x8E;
	
	private final int BCC_REL		= 0x90;
	private final int STA_IND_Y		= 0x91;
	private final int STY_Z_X		= 0x94;
	private final int STA_Z_X		= 0x95;
	private final int STX_Z_Y		= 0x96;
	private final int TYA_IMP		= 0x98;
	private final int STA_ABS_Y		= 0x99;
	private final int TXS_IMP		= 0x9A;
	private final int STA_ABS_X		= 0x9D;
	
	private final int LDY_IMM		= 0xA0;
	private final int LDA_IND_X		= 0xA1;
	private final int LDX_IMM		= 0xA2;
	private final int LDY_Z	 		= 0xA4;
	private final int LDA_Z			= 0xA5;
	private final int LDX_Z			= 0xA6;
	private final int TAY_IMP		= 0xA8;
	private final int LDA_IMM		= 0xA9;
	private final int TAX_IMP		= 0xAA;
	private final int LDY_ABS		= 0xAC;
	private final int LDA_ABS		= 0xAD;
	private final int LDX_ABS		= 0xAE;
	
	private final int BCS_REL		= 0xB0;
	private final int LDA_IND_Y		= 0xB1;
	private final int LDY_Z_X		= 0xB4;
	private final int LDA_Z_X		= 0xB5;
	private final int LDX_Z_Y		= 0xB6;
	private final int CLV_IMP		= 0xB8;
	private final int LDA_ABS_Y		= 0xB9;
	private final int TSX_IMP		= 0xBA;
	private final int LDY_ABS_X		= 0xBC;
	private final int LDA_ABS_X		= 0xBD;
	private final int LDX_ABS_Y		= 0xBE;

	private final int CPY_IMM		= 0xC0;
	private final int CMP_IND_X		= 0xC1;
	private final int CPY_Z			= 0xC4;
	private final int CMP_Z			= 0xC5;
	private final int DEC_Z			= 0xC6;
	private final int INY_IMP		= 0xC8;
	private final int CMP_IMM		= 0xC9;
	private final int DEX_IMP		= 0xCA;
	private final int CPY_ABS		= 0xCC;
	private final int CMP_ABS		= 0xCD;
	private final int DEC_ABS		= 0xCE;

	private final int BNE_REL		= 0xD0;
	private final int CMP_IND_Y		= 0xD1;
	private final int CMP_Z_X		= 0xD5;
	private final int DEC_Z_X		= 0xD6;
	private final int CLD_IMP		= 0xD8;
	private final int CMP_ABS_Y		= 0xD9;
	private final int CMP_ABS_X		= 0xDD;
	private final int DEC_ABS_X		= 0xDE;
	
	private final int CPX_IMM		= 0xE0;
	private final int SBC_IND_X		= 0xE1;
	private final int CPX_Z			= 0xE4;
	private final int SBC_Z			= 0xE5;
	private final int INC_Z			= 0xE6;
	private final int INX_IMP		= 0xE8;
	private final int SBC_IMM		= 0xE9;
	private final int NOP_IMP		= 0xEA;
	private final int CPX_ABS		= 0xEC;
	private final int SBC_ABS		= 0xED;
	private final int INC_ABS		= 0xEE;
	
	private final int BEQ_REL	 	= 0xF0;
	private final int SBC_IND_Y		= 0xF1;
	private final int SBC_Z_X		= 0xF5;
	private final int INC_Z_X		= 0xF6;
	private final int SED_IMP		= 0xF8;
	private final int SBC_ABS_Y		= 0xF9;
	private final int SBC_ABS_X		= 0xFD;
	private final int INC_ABS_X		= 0xFE;
	
	/*illegal instructions*/
	
	private final int ILL_DCP_Z			= 0xC7;
	private final int ILL_DCP_Z_X		= 0xD7;
	private final int ILL_DCP_ABS		= 0xCF;
	private final int ILL_DCP_ABS_X		= 0xDF;
	private final int ILL_DCP_ABS_Y		= 0xDB;
	private final int ILL_DCP_IND_X		= 0xC3;
	private final int ILL_DCP_IND_Y		= 0xD3;
	
	private final int ILL_ISB_Z			= 0xE7;
	private final int ILL_ISB_Z_X		= 0xF7;
	private final int ILL_ISB_ABS		= 0xEF;
	private final int ILL_ISB_ABS_X		= 0xFF;
	private final int ILL_ISB_ABS_Y		= 0xFB;
	private final int ILL_ISB_IND_X		= 0xE3;
	private final int ILL_ISB_IND_Y		= 0xF3;
	
	private final int ILL_LAX_Z 		= 0xA7;
	private final int ILL_LAX_Z_Y 		= 0xB7;
	private final int ILL_LAX_ABS 		= 0xAF;
	private final int ILL_LAX_ABS_Y 	= 0xBF;
	private final int ILL_LAX_IND_X 	= 0xA3;
	private final int ILL_LAX_IND_Y 	= 0xB3;
	
	//IMP
	private final int ILL_NOP_IMP_0 	= 0x1A;
	private final int ILL_NOP_IMP_1 	= 0x3A;
	private final int ILL_NOP_IMP_2 	= 0x5A;
	private final int ILL_NOP_IMP_3 	= 0x7A;
	private final int ILL_NOP_IMP_4 	= 0xDA;
	private final int ILL_NOP_IMP_5 	= 0xFA;
	//IMM
	private final int ILL_NOP_IMM_0 	= 0x80; 
	private final int ILL_NOP_IMM_1 	= 0x82;
	private final int ILL_NOP_IMM_2 	= 0x89;
	private final int ILL_NOP_IMM_3 	= 0xC2;  
	private final int ILL_NOP_IMM_4 	= 0xE2; 
	//Z
	private final int ILL_NOP_Z_0 		= 0x04;
	private final int ILL_NOP_Z_1 		= 0x44;
	private final int ILL_NOP_Z_2		= 0x64;
	//Z_X
	private final int ILL_NOP_Z_X_0 	= 0x14;
	private final int ILL_NOP_Z_X_1 	= 0x34;
	private final int ILL_NOP_Z_X_2 	= 0x54;
	private final int ILL_NOP_Z_X_3 	= 0x74;
	private final int ILL_NOP_Z_X_4 	= 0xD4;
	private final int ILL_NOP_Z_X_5 	= 0xF4;  
	//ABS
	private final int ILL_NOP_ABS_0 	= 0x0C;
	//ABS_X
	private final int ILL_NOP_ABS_X_0 	= 0x1C;
	private final int ILL_NOP_ABS_X_1 	= 0x3C;
	private final int ILL_NOP_ABS_X_2 	= 0x5C;
	private final int ILL_NOP_ABS_X_3 	= 0x7C;
	private final int ILL_NOP_ABS_X_4 	= 0xDC;
	private final int ILL_NOP_ABS_X_5 	= 0xFC;		
	
	private final int ILL_RLA_Z			= 0x27;
	private final int ILL_RLA_Z_X		= 0x37;
	private final int ILL_RLA_ABS		= 0x2F;
	private final int ILL_RLA_ABS_X		= 0x3F;
	private final int ILL_RLA_ABS_Y		= 0x3B;
	private final int ILL_RLA_IND_X		= 0x23;
	private final int ILL_RLA_IND_Y		= 0x33;
	
	private final int ILL_RRA_Z			= 0x67;
	private final int ILL_RRA_Z_X		= 0x77;
	private final int ILL_RRA_ABS		= 0x6F;
	private final int ILL_RRA_ABS_X		= 0x7F;
	private final int ILL_RRA_ABS_Y		= 0x7B;
	private final int ILL_RRA_IND_X		= 0x63;
	private final int ILL_RRA_IND_Y		= 0x73;
	
	private final int ILL_SAX_Z 		= 0x87;
	private final int ILL_SAX_Z_Y 		= 0x97;
	private final int ILL_SAX_ABS 		= 0x8F;	
	private final int ILL_SAX_IND_X 	= 0x83;	
	
	private final int ILL_SBC_IMM 		= 0xEB;	
	
	private final int ILL_SLO_Z			= 0x07;
	private final int ILL_SLO_Z_X		= 0x17;
	private final int ILL_SLO_ABS		= 0x0F;
	private final int ILL_SLO_ABS_X		= 0x1F;
	private final int ILL_SLO_ABS_Y		= 0x1B;
	private final int ILL_SLO_IND_X		= 0x03;
	private final int ILL_SLO_IND_Y		= 0x13;
	
	private final int ILL_SRE_Z			= 0x47;
	private final int ILL_SRE_Z_X		= 0x57;
	private final int ILL_SRE_ABS		= 0x4F;
	private final int ILL_SRE_ABS_X		= 0x5F;
	private final int ILL_SRE_ABS_Y		= 0x5B;
	private final int ILL_SRE_IND_X		= 0x43;
	private final int ILL_SRE_IND_Y		= 0x53;
		
	
	/*address of each interrupt vector*/
	private final short NMI_VECTOR = (short) 0xFFFA;	
	private final short RESET_VECTOR = (short) 0xFFFC;
	private final short IRQ_VECTOR = (short) 0xFFFE;
	
	/*INTERNAL WORK RAM*/
	private byte RAM[] = new byte[RAM_SIZE];
	
	private final byte RAM_POWER_UP_VALUE = (byte) 0xFF;

	/*INTERNAL REGISTERS*/
	private byte A;	//Accumulator
	private byte X;	//Index
	private byte Y;	//Index
	private byte P;	//Status Register
	private byte SP;//Stack Pointer
	public short PC;//Program Counter
	
	private final int FLAG_C = 0;	//carry
	private final int FLAG_Z = 1;	//zero
	private final int FLAG_I = 2;	//interrupt disable
	private final int FLAG_D = 3;	//decimal
	private final int FLAG_B = 4;	//B flag
	private final int FLAG_1 = 5;	//always pushed as 1
	private final int FLAG_V = 6;	//overflow
	private final int FLAG_N = 7;	//negative
	
	private boolean NMI = false;	//pending non-maskable interrupt
	private boolean IRQ = false;	//pending interrupt request	
	private boolean RESET = false;	//pending reset
	
	private int IR;	//Instruction Register (IR)
	private byte operand;	//hold operand byte
	private short address;	//hold temporary address
	private short effective;	//effective address
	private short pointer;	//pointer for indirect addressing
	private byte PCH;	//used for JMP
	private byte PCL;	//
	private byte high;	//used for absolute addressing
	private byte low;	//
	
	/*how many cycles into the instruction*/
	private int cycle;	//gets reset after an instruction
	private int cycleTotal;
	
	/*OAM DMA*/
	private boolean OAM_DMA = false;
	private boolean attemptHalt = false;
	private boolean alignDMA = false;
	
	private static final boolean MEM_READ = false;
	private static final boolean MEM_WRITE = true;
	private boolean lastMemoryAccess = MEM_READ;
	
	private static final boolean DMA_GET = false;
	private static final boolean DMA_PUT = true;
	private boolean getOrPut = DMA_GET;
	
	private byte byteDMA;
	private byte cDMA = 0;
	
	/*ARTIFICIAL INSTRUCTION CYCLE EXECUTION STATE FLAGS*/
	private boolean SKIP	= false;	//skip the next cycle?
	private boolean FINISH 	= false;	//has the instruction finished executing?
	
	/*NESTEST*/
	private boolean nestest = false;	//when true, code execution starts from $C000
	
	/*PERIPHERALS*/
	private Ppu PPU;
	private Controller CONTROLLER;
	/*temporary way of accessing ROM because mappers are not yet implemented*/
	private Rom ROM;	
	/*bus*/
	private Bus BUS;
	private Bus IO_BUS;
	
	//used for nestest automation
	private BufferedReader br;
	private String line;
	private int lineNumber = 0;		
	
	private boolean pause = false;
	private boolean stepping = false;
	
	private boolean debug = false;
	private boolean debugUpdatePool = false;	//must be enabled for CPU Viewer to work
	private boolean debugMemorySpace = false;	//HUGE slow down
	private boolean oamDebug = false;
	
	private boolean TOGGLE_BREAKPOINT = false;
	private int BREAKPOINT = 0x0000;
	
	Cpu(Bus bObj, Bus ioObj, Ppu pObj, Rom rObj, Controller cObj)
	{
		BUS = bObj;	//cpu bus
		IO_BUS = ioObj;	//io_bus used by cpu and ppu to communicate between each other
		
		/*Create object for each peripheral class*/
		PPU = pObj;
		ROM = rObj;
		CONTROLLER = cObj;
		
		try {
			br = new BufferedReader(new FileReader("rom/test/nestest.log.log"));
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}		
	}
	
	/*Power on*/
	public void POWER()
	{
		/*POWER-ON*/
		
		/*TODO: do all power-on initialization*/
		
		/*
		    P = $34[1] (IRQ disabled)[2]
		    A, X, Y = 0
		    S = $FD[3]
		    $4017 = $00 (frame irq enabled)
		    $4015 = $00 (all channels disabled)
		    $4000-$400F = $00
		    $4010-$4013 = $00 [4]
		*/	
		
		cycle = 1;
		cycleTotal = 1;
		
		IRQ = false;
		NMI = false;
		
		P = 0x24;
		A = 0;
		X = 0;
		Y = 0;
		
		SP = (byte)0xFD;
		
		PC = COMBINE16(BYTESAFE(RESET_VECTOR), BYTESAFE((short)(UINT16(RESET_VECTOR) + 1)));
		System.out.println(String.format("STARTING EXECUTION FROM $%04X", UINT16(PC)));
		if(nestest) PC = (short) 0xC000;	//nestest code start
		
		/*Fill RAM with 0x00*/
		for(int i = 0; i < RAM_SIZE; i++) RAM[i] = RAM_POWER_UP_VALUE;
		
		
		//IR = UINT8(_read8rom(PC));
		IR = UINT8(BYTESAFE(PC));
		printNestestLog();
		DebugPool.ir = UINT8(BYTESAFE(PC));
		updateDebugPool();
	}
	
	/*Reset*/
	public void RESET()
	{
		;
	}
	
	private void IRQ()
	{
		if(STATUS(FLAG_I) == 1) return;	//don't handle interrupt if disable flag is set
		switch(cycle)
		{			
			case 2:				
				BYTE(PC);
				//INCREMENT_PC();	//PC increment is suppressed
				break;
			case 3: 				
				PCH = PCH();
				PUSH(PCH);
				SP--;
				break;
			case 4:
				PCL = PCL();
				PUSH(PCL);
				SP--;
				break;
			case 5: 	
				byte b = P;
				b = BIT(b, FLAG_B, 0);	//clear B flag
				b = BIT(b, FLAG_1, 1);
				PUSH(b);
				SP--;
				break;
			case 6: 
				PCL = BYTE(IRQ_VECTOR);
				STATUS(FLAG_I, 1);				
				break;
			case 7: 
				PCH = BYTE((short)(UINT16(IRQ_VECTOR) + 1));
				PC = COMBINE16(PCL, PCH);
				FINISH();
				break;
		}
	}	
	
	private void NMI()
	{
		//System.out.println(String.format("NMI(%d)", cycle));
		switch(cycle)
		{			
			case 2:				
				BYTE(PC);
				//INCREMENT_PC();	//PC increment is suppressed
				break;
			case 3: 				
				PCH = PCH();
				PUSH(PCH);
				SP--;
				break;
			case 4:
				PCL = PCL();
				PUSH(PCL);
				SP--;
				break;
			case 5: 	
				byte b = P;
				b = BIT(b, FLAG_B, 0);	//clear B flag
				b = BIT(b, FLAG_1, 1);
				PUSH(b);
				SP--;
				break;
			case 6: 
				PCL = BYTE(NMI_VECTOR);
				STATUS(FLAG_I, 1);				
				break;
			case 7: 
				PCH = BYTE((short)(UINT16(NMI_VECTOR) + 1));
				PC = COMBINE16(PCL, PCH);
				//System.out.println("JUMPING TO NMI VECTOR");
				FINISH();
				break;
		}
	}
	
	private void OAM_DMA()
	{
		if(getOrPut == DMA_GET)
		{
			byteDMA = BYTE((short)((UINT8(PPU.OAMDMA) << 8) | UINT8(cDMA)));
		}
		else if(getOrPut == DMA_PUT)
		{
			PPU.OAM[UINT8(cDMA)] = byteDMA;
			cDMA++;
		}
				
		if(UINT8(cDMA) == 0x00)	//OAM-DMA finished
		{
			if(oamDebug) System.out.println(String.format("[*]DMA complete: (cycle %d, [%d])", cycle, cycleTotal));
			OAM_DMA = false;
		}
	}
	
	private void SKIP()
	{
		cycle++;
		SKIP = true;
		cycleTotal--;
	}
	
	private void FINISH()
	{		
		cycleTotal += cycle;
		cycle = 1;
		FINISH = true;
		//if(NMI) NMI = false;
		//if(IRQ) IRQ = false;
	}
	
	public void PAUSE()
	{
		pause = true;
	}
	
	public void RESUME()
	{
		pause = false;
	}
	
	public void setUpdateDebugPool(boolean flag)
	{
		debug = flag;
	}
	
	private void updateDebugPool()
	{		
		DebugPool.ir = IR;
		DebugPool.cycle = cycle;
		DebugPool.cycleTotal = cycleTotal;
		DebugPool.operand = operand;
		DebugPool.NMI = NMI;
		DebugPool.IRQ = IRQ;
		DebugPool.A = A;
		DebugPool.X = X;
		DebugPool.Y = Y;
		DebugPool.P = P;
		DebugPool.SP = SP;
		DebugPool.PC = PC;
		DebugPool.RAM = RAM;
		
		//updateDebugPoolMemorySpaceSnapshot();
	}
	
	public void updateDebugPoolMemorySpaceSnapshot()
	{
		if(!debug) return;
		for(int i = 0; i <= 0xFFFF; i++)
		{			
			if((i >= 0) && (i <= 0x1FFF))
			{
				if(DebugPool.CPU_MEMORY_SPACE[i] != RAM[i & 0x7FF]) DebugPool.CPU_MEMORY_SPACE[i] = RAM[i & 0x7FF];
			}
			else if((i >= 0x2000) && (i <= 0x3FFF))
			{
				switch(i % 8)
				{
					case 0x0:
						DebugPool.CPU_MEMORY_SPACE[i] = PPU.PPUCTRL;
						continue;
					case 0x1:
						DebugPool.CPU_MEMORY_SPACE[i] = PPU.PPUMASK;
						continue;
					case 0x2:
						DebugPool.CPU_MEMORY_SPACE[i] = PPU.PPUSTATUS;
						continue;
					case 0x3:
						//DebugPool.CPU_MEMORY_SPACE[i] = 0x00;
						continue;						
					case 0x4:
						//DebugPool.CPU_MEMORY_SPACE[i] = 0x00;
						continue;						
					case 0x5:
						DebugPool.CPU_MEMORY_SPACE[i] = PPU.PPUSCROLL;
						continue;
					case 0x6:
						DebugPool.CPU_MEMORY_SPACE[i] = PPU.PPUADDR;
						continue;
					case 0x7:
						DebugPool.CPU_MEMORY_SPACE[i] = PPU.PPUDATA;
						continue;
				}
			}
			else if((i >= 0x8000) && (i <= 0xFFFF))
			{
				DebugPool.CPU_MEMORY_SPACE[i] = BYTESAFE((short)i);
			}
			else
			{
				DebugPool.CPU_MEMORY_SPACE[i] = 0x00;
			}
		}
	}
	
	public boolean isBreakpointEnabled()
	{
		return TOGGLE_BREAKPOINT;
	}
	
	public void toggleBreakpoint(boolean toggle)
	{
		TOGGLE_BREAKPOINT = toggle;
	}
	
	public void setBreakpointAt(int addr)
	{
		BREAKPOINT = addr;
	}
	
	public void toggleBreakpointAt(boolean toggle, int addr)
	{
		TOGGLE_BREAKPOINT = toggle;
		BREAKPOINT = addr;
	}
	
	private byte _read8ram(short addr)
	{		
		return RAM[UINT16(addr) & 0x7FF];
	}
	
	
	private byte _read8rom(short offset)
	{		
		//if(debug) System.out.println(String.format("CPU._read8rom(): reading from $%04X", (UINT16(addr) % 0x4000)));
		//if(debug) System.out.println(String.format("CPU._read8rom(): reading from $%04X", ((UINT16(addr) - UINT16(PRG_START)) % 0x8000)));
		//System.out.println(String.format("CPU._read8rom(): reading from $%04X", UINT16(addr)));
		/*if(UINT16(offset) < PRG_START)
		{
			System.out.println("CPU._read8rom(): FATAL ERROR: attempting to read from negative offset.");
			System.exit(0);
		}*/
		return ROM.PRG_ROM[UINT16(offset) % (ROM.PRG_UNIT_SIZE * 2)];	//hack for now (idk if the rom is mirrored when prg rom is smaller than 32 kb like in the case of nestest)
		//return ROM.BUFFER[((UINT16(addr) - UINT16(PRG_START)) % (ROM.KB * 32))];
		//return ROM.BUFFER[((UINT16(addr) - UINT16(PRG_START)) & 0x7FFF)];
	}	
	
	//BUS OPERATION
	//called inside BYTE() to read/write
	//
	//do read/write operation at ADDR with DATA
	//Address, Data, and Control bus lines must be set beforehand
	private void busOp()
	{		
		if(UINT16(BUS.ADDR) >= 0x0000)
		{
			if(UINT16(BUS.ADDR) < 0x2000)	//RAM
			{
				//if(IR == STA_IND_Y && UINT16(BUS.ADDR) == 0x400) System.out.println(String.format("\tbusOp(): GOOD, ctrl = %d", UINT8(BUS.CTRL)));
				if(BUS.CTRL == BUS.CTRL_READ) BUS.DATA = RAM[UINT16(BUS.ADDR) & 0x7FF];
				else if(BUS.CTRL == BUS.CTRL_WRITE) RAM[UINT16(BUS.ADDR) & 0x7FF] = BUS.DATA;
				return;
			}
			else if(UINT16(BUS.ADDR) < 0x4000)	//PPU Registers
			{
				//THIS SEGMENT SHOULD NEVER EXECUTE!!!
				System.out.println("CPU.busOp(): FATAL ERROR: busOp() should not be handling memory access from 0x2000-0x3FFF!!");
				
				//System.out.println(String.format("\tbusOp(): BUS.ADDR = $%X, BUS.CTRL = %d", Util.UINT16(BUS.ADDR), BUS.CTRL));
				switch(UINT16(BUS.ADDR) % 0x8)	//mirror
				{
					//System.out.println(String.format("accessing $%04X", UINT16(BUS.ADDR) % 0x8));
					/*potentially buggy code... keep an eye out...*/
					case 0x00:						
						//if(BUS.CTRL == BUS.CTRL_WRITE) BUS.DATA = PPU.PPUCTRL; break;
						if(BUS.CTRL == BUS.CTRL_WRITE)
						{
							//if(BIT(BUS.DATA, 7) == 1 && PPU.CTRL(PPU.CTRL_NMI) == 0 && PPU.STATUS(PPU.STATUS_VBLANK) == 1) PPU.NMI = true;
							//if(BIT(BUS.DATA, 7) == 1 && PPU.STATUS(PPU.STATUS_VBLANK) == 1) PPU.NMI = true;
							if((BIT(BUS.DATA, 7) == 1) || (PPU.STATUS(PPU.STATUS_VBLANK) == 1))
							{
								//System.out.println("ENABLING VBL WHILE FLAG IS SET");
								PPU.NMI = true;
							}
							PPU.PPUCTRL = BUS.DATA;
						}
						break;
					case 0x01:				
						if(BUS.CTRL == BUS.CTRL_WRITE) PPU.PPUMASK = BUS.DATA; break;
					case 0x02:	
						if(BUS.CTRL == BUS.CTRL_READ)
						{							
							if(PPU.scanline == PPU.VBLANK_SCANLINE_START)
							{
								switch(PPU.dot)
								{
									case 0:	//1 cycle before V-BLANK
										PPU.suppressNMI = true;
										PPU.suppressVBL = true;
										break;
									case 1:	//on the exact cycle that VBLANK starts
										//System.out.println("READING PPUSTATUS ON VBLANK SET!");
										PPU.suppressNMI = true;										
										break;
									case 2:	//1 cycle after V-BLANK
										PPU.suppressNMI = true;
										break;
								}
							}
							BUS.DATA = (byte) (UINT8(PPU.PPUSTATUS) & 0xE0);
							//System.out.println(String.format("\tbusOp(): PPU.PPUSTATUS VBLANK = %d", Util.BIT(PPU.PPUSTATUS, PPU.STATUS_VBLANK)));							
							PPU.STATUS(PPU.STATUS_VBLANK, 0);
							break;
						}							
						break;
					case 0x03:
						if(BUS.CTRL == BUS.CTRL_WRITE) PPU.OAMADDR = BUS.DATA;
						break;
					case 0x04:
						if(BUS.CTRL == BUS.CTRL_READ)
						{
							BUS.DATA = PPU.OAMDATA();							
						}
						else if(BUS.CTRL == BUS.CTRL_WRITE) 
						{							
							PPU.OAMDATA = BUS.DATA;
							PPU.OAMDATA(BUS.DATA);							
						}
						break;
					case 0x05:
						if(BUS.CTRL == BUS.CTRL_WRITE) PPU.PPUSCROLL = BUS.DATA; 
						break;
					case 0x06:
						//if(BUS.CTRL == BUS.CTRL_WRITE) PPU.PPUADDR = BUS.DATA; break;
						if(BUS.CTRL == BUS.CTRL_WRITE) PPU.PPUADDR(BUS.DATA); 
						break;
					case 0x07:
						//if(BUS.CTRL == BUS.CTRL_READ) {BUS.DATA = PPU.PPUDATA; break;}
						//if(BUS.CTRL == BUS.CTRL_WRITE) {PPU.PPUDATA = BUS.DATA; break;}
						if(BUS.CTRL == BUS.CTRL_READ) BUS.DATA = PPU.PPUDATA();
						else if(BUS.CTRL == BUS.CTRL_WRITE) PPU.PPUDATA(BUS.DATA);
						break;
				}
				return;
			}
			else if(UINT16(BUS.ADDR) < 0x4020)
			{
				switch(BUS.ADDR)
				{
					case 0x4014: 
						if(BUS.CTRL == BUS.CTRL_WRITE)
						{
							if(oamDebug) System.out.println(String.format("[*]Write to OAMDMA ($4014): (cycle %d, [%d])", cycle, cycleTotal));
							PPU.OAMDMA = BUS.DATA;
							OAM_DMA = true;
							attemptHalt = true;
						}
						break;
					case 0x4016:	//controller 1
						if(BUS.CTRL == BUS.CTRL_READ)
						{														
							BUS.DATA = CONTROLLER.getNextButton();
							//System.out.println("\tgetNextButton() = " + UINT8(BUS.DATA));
							/*if(UINT8(X) == 0x5)
							{
								//System.out.println("FORCED START");
								BUS.DATA = 1;
							}
							BUS.DATA = 0x41;*/
						}
						else if(BUS.CTRL == BUS.CTRL_WRITE)
						{							
							CONTROLLER.writeStrobe(BUS.DATA);
							if(CONTROLLER.getStrobe() == 0)
							{
								;
							}
							else if(CONTROLLER.getStrobe() == 1)
							{
								CONTROLLER.captureInput();
							}
							//System.out.println(String.format("writeStrobe(%d)", UINT8(BUS.DATA)));
						}
						break;					
				}
			}
			else if(UINT16(BUS.ADDR) < 0x6000)	//Expansion ROM
			{
				//System.out.println("EXP ROM");
			}
			else if(UINT16(BUS.ADDR) < 0x8000)	//SRAM
			{
				//System.out.println("SRAM");
			}
			else if(UINT16(BUS.ADDR) < 0x10000)	//ROM
			{						
				/*NOT SURE IF THE INDEXING IS ACCURATE/CORRECT, namely the "% 0x4000" part*/
				//if(BUS.CTRL == BUS.CTRL_READ) BUS.DATA = ROM.BUFFER[((UINT16(BUS.ADDR) - UINT16(PRG_START)) % 0x4000)];				
				//if(BUS.CTRL == BUS.CTRL_READ) BUS.DATA = ROM.PRG_ROM[((UINT16(BUS.ADDR) - UINT16(PRG_START)) % 0x4000)];
				if(BUS.CTRL == BUS.CTRL_READ) BUS.DATA = _read8rom((short)(UINT16(BUS.ADDR) - PRG_START));
			}
		}		
	}
	
	private void ioBusOp(short addr, int read)
	{			
		//Assume IO_BUS.DATA is already set
		
		switch(UINT16(addr) % 0x8)	//mirror
		{
			case 0x00:
				//if(read == IO_BUS.CTRL_WRITE) PPU.PPUCTRL = IO_BUS.DATA;
				if(read == IO_BUS.CTRL_WRITE)
				{
					//if(BIT(IO_BUS.DATA, 7) == 1 && PPU.CTRL(PPU.CTRL_NMI) == 0 && PPU.STATUS(PPU.STATUS_VBLANK) == 1) PPU.NMI = true;
					if((BIT(IO_BUS.DATA, 7) == 1) && (PPU.STATUS(PPU.STATUS_VBLANK) == 1))
					{
						//System.out.println("ENABLING VBL WHILE FLAG IS SET");
						if(PPU.CTRL(PPU.CTRL_NMI) == 0) PPU.NMI = true;
					}
					PPU.PPUCTRL = (byte) (UINT8(IO_BUS.DATA) & 0b11111100);
										
					PPU.t = (short)((UINT16(PPU.t) & 0b111001111111111) | ((UINT8(IO_BUS.DATA) & 0b00000011) << 10));
				}
				break;
			case 0x01:
				if(read == IO_BUS.CTRL_WRITE)
				{
					PPU.PPUMASK = IO_BUS.DATA;
				}
				break;
			case 0x02:						
				if(read == IO_BUS.CTRL_READ)
				{
					if(PPU.scanline == PPU.VBLANK_SCANLINE_START)
					{
						switch(PPU.dot)
						{
							case 1:	//1 cycle before V-BLANK
								PPU.suppressNMI = true;
								PPU.suppressVBL = true;
								break;
							case 2:	//on the exact cycle that VBLANK starts
								//System.out.println("READING PPUSTATUS ON VBLANK SET!");
								PPU.suppressNMI = true;										
								break;
							case 3:	//1 cycle after V-BLANK
								PPU.suppressNMI = true;
								break;
						}
					}
					IO_BUS.DATA = (byte) (UINT8(PPU.PPUSTATUS) & 0xE0);
					PPU.STATUS(PPU.STATUS_VBLANK, 0);	
					
					PPU.w = 0;
				}							
				break;
			case 0x03:
				if(read == IO_BUS.CTRL_WRITE)
				{
					PPU.OAMADDR = IO_BUS.DATA;
				}
				break;
			case 0x04:
				if(read == IO_BUS.CTRL_READ) 
				{
					IO_BUS.DATA = PPU.OAMDATA(); 
					break;
				}
				if(read == IO_BUS.CTRL_WRITE) 
				{
					PPU.OAMDATA(IO_BUS.DATA); 
					break;
				}
			case 0x05:
				if(read == IO_BUS.CTRL_WRITE)
				{
					PPU.PPUSCROLL = IO_BUS.DATA; 
					if(UINT8(PPU.w) == 0)
					{																	
						PPU.t = (short)((UINT16(PPU.t) & 0b111111111100000) | ((UINT8(IO_BUS.DATA) & 0b11111000) >>> 3));
						PPU.x = (byte)(UINT8(IO_BUS.DATA) & 0b00000111);
						PPU.w = 1;
					}
					else if(UINT8(PPU.w) == 1)
					{						
						//PPU.t = (short)((UINT16(PPU.t) & 0b000110000011111) | ((UINT8(IO_BUS.DATA) & 0b11111000) << 2));
						PPU.t = (short)((UINT16(PPU.t) & 0b000110000011111) | (((UINT8(IO_BUS.DATA) & 0b11111000) >>> 3) << 5));
						PPU.t = (short)((UINT16(PPU.t) & 0b000111111111111) | ((UINT8(IO_BUS.DATA) & 0b00000111) << 12));						
						PPU.w = 0;
					}
				}					
				break;
			case 0x06:
				if(read == IO_BUS.CTRL_WRITE)
				{
					if(UINT8(PPU.w) == 0)
					{															
						PPU.t = (short)((UINT16(PPU.t) & 0b000000011111111) | ((UINT8(IO_BUS.DATA) & 0b00111111) << 8));
						PPU.w = 1;
					}
					else if(UINT8(PPU.w) == 1)
					{						
						PPU.t = (short)((UINT16(PPU.t) & 0b111111100000000) | UINT8(IO_BUS.DATA));
						PPU.v = PPU.t;						
						PPU.w = 0;
					}
				}
				//PPU.PPUADDR(IO_BUS.DATA); 
				break;
			case 0x07:
				if(read == IO_BUS.CTRL_READ) 
				{
					IO_BUS.DATA = PPU.PPUDATA(); 
					break;
				}
				if(read == IO_BUS.CTRL_WRITE) 
				{
					PPU.PPUDATA(IO_BUS.DATA); 
					break;
				}
			default:
				System.out.println("\tioBusOp(): code is broken.");
				break;
		}
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
	
	private void INCREMENT_PC()
	{
		//suppress PC increments if OAM-DMA is pending and this is a GET(read) cycle
		//so that once the DMA transfer is complete, execution can resume from where
		//it left off
		if(OAM_DMA && lastMemoryAccess == MEM_READ)
		{
			if(oamDebug) System.out.println(String.format("[*]PC increment suppressed: (cycle %d, [%d])", cycle, cycleTotal));
		}
		
		PC++;
	}
	
	/*Read byte from memory*/
	public byte BYTE(short addr)
	{		
		//handle io bus
		if((UINT16(addr) >= 0x2000) && (UINT16(addr) <= 0x3FFF))
		{	
			ioBusOp(addr, IO_BUS.CTRL_READ);
			return IO_BUS.DATA;
		}
		
		//handle cpu internal bus
		/*set bus values*/
		BUS.ADDR = addr;
		BUS.CTRL = BUS.CTRL_READ;		
		busOp();	//set DATA

		//used for OAM DMA
		lastMemoryAccess = MEM_READ;
		if(OAM_DMA && attemptHalt)
		{
			if(oamDebug) System.out.println(String.format("[*]Halted CPU: (cycle %d, [%d])", cycle, cycleTotal));
			attemptHalt = false;	//halt CPU
			alignDMA = true;	//now align get/put cycle
			;
		}
		
		return BUS.DATA;
	}	
	
	/*Write byte to memory*/
	public void BYTE(short addr, byte data)
	{		
		//handle io bus
		if((UINT16(addr) >= 0x2000) && (UINT16(addr) <= 0x3FFF))	
		{			
			IO_BUS.DATA = data;
			ioBusOp(addr, IO_BUS.CTRL_WRITE);			
			return;
		}
		
		//handle cpu internal bus
		/*Set bus values*/
		BUS.ADDR = addr;
		BUS.CTRL = BUS.CTRL_WRITE;
		BUS.DATA = data;
		busOp();
		
		//used for OAM DMA
		lastMemoryAccess = MEM_WRITE;
	}
	
	/*Read byte from memory SAFELY meaning without altering any CPU states*/
	public byte BYTESAFE(short addrSigned)
	{		
		int addr = UINT16(addrSigned);
		byte data = 0x00;
		if(addr >= 0 && addr < 0x2000)
		{
			data = _read8ram(addrSigned);
		}
		else if(addr >= 0x8000 && addr <= 0xFFFF)
		{
			data = _read8rom((short)(addr - PRG_START));
		}
		else	//currently only supports RAM and ROM
		{
			System.out.println("ERROR: BYTESAFE() can currently only read from RAM/ROM.\nExiting...");
			System.exit(0);
		}
		return data;
	}	
	
	public static byte HIGH(short s)
	{
		return (byte)(UINT16(s) >>> 8);
	}
	
	public static byte LOW(short s)
	{
		return (byte)(UINT16(s) & 0xFF);
	}
	
	public int STATUS(int bit)
	{
		return ((UINT8(P) >>> bit) & 0x1);
	}
	
	/*requires testing to ensure output is correct*/
	public void STATUS(int bit, int data)
	{
		P = (byte) (0x00 | (UINT8(P) & (0xFF ^ (1 << bit))));	//zero the bit
		P = (byte) (UINT8(P) | ((data & 1) << bit));	//set to either 0 or 1
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
	
	//===============================================================================
	private byte PCL()
	{
		return (byte)(UINT16(PC) & 0xFF);
	}
	
	private byte PCH()
	{
		return (byte)((UINT16(PC) & 0xFF00) >>> 8);
	}
	//===============================================================================
	
	private void PUSH(byte data)
	{
		BYTE((short)(STACK_START + UINT8(SP)), data);
	}
	
	private byte POP()
	{
		return BYTE((short)(STACK_START + UINT8(SP)));
	}
	
	//===============================================================================
	
	//===============================================================================
	//INSTRUCTIONS
	//===============================================================================
	
	private void _ADC()
	{
		int r = UINT8(A) + UINT8(operand) + STATUS(FLAG_C);
		if(r > 0xFF) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);
		if((r&0xFF) == 0) STATUS(FLAG_Z, 1);	//zero
		else STATUS(FLAG_Z, 0);
		if(BIT(A, 7) == BIT(operand, 7) && ((r & 0x80) >>> 7) != BIT(A, 7)) STATUS(FLAG_V, 1);	//overflow
		else STATUS(FLAG_V, 0);
		if(((r & 0x80) >>> 7) == 1) STATUS(FLAG_N, 1);	//negative
		else STATUS(FLAG_N, 0);
		A = (byte)(r & 0xFF);
	}
	
	private void _AND()
	{
		A = (byte) (UINT8(operand) & UINT8(A));
		STATUS(FLAG_N, BIT(A, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _ASL_ACC()
	{
		if(BIT(A, 7) == 1) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);
		A = (byte) (UINT8(A) << 1);
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, BIT(A, 7));
	}
	
	private void _ASL_MEM()
	{
		if(BIT(operand, 7) == 1) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);
		operand = (byte) (UINT8(operand) << 1);
		if(UINT8(operand) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, BIT(operand, 7));
	}
	
	private boolean _BCC()
	{
		return (STATUS(FLAG_C) == 0);
	}
	private boolean _BCS()
	{
		return (STATUS(FLAG_C) == 1);
	}
	
	private boolean _BEQ()
	{
		return (STATUS(FLAG_Z) == 1);
	}
	
	private void _BIT()
	{
		STATUS(FLAG_N, BIT(operand, 7));
		STATUS(FLAG_V, BIT(operand, 6));
		if((UINT8(A) & UINT8(operand)) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private boolean _BMI()
	{
		return (STATUS(FLAG_N) == 1);
	}
	
	private boolean _BNE()
	{
		return (STATUS(FLAG_Z) == 0);
	}
	
	private boolean _BPL()
	{
		return (STATUS(FLAG_N) == 0);
	}
	
	private boolean _BVC()
	{
		return (STATUS(FLAG_V) == 0);
	}
	
	private boolean _BVS()
	{
		return (STATUS(FLAG_V) == 1);
	}
	
	private void _CLC()
	{
		STATUS(FLAG_C, 0);
	}
	
	private void _CLD()
	{
		STATUS(FLAG_D, 0);
	}
	
	private void _CLI()
	{
		STATUS(FLAG_I, 0);
	}
	
	private void _CLV()
	{
		STATUS(FLAG_V, 0);
	}
	
	private void _CMP()
	{
		int r = UINT8(A) - UINT8(operand);
		if(((r & 0x80) >>> 7) == 1) STATUS(FLAG_N, 1);
		else STATUS(FLAG_N, 0);
		if((r & 0xFF) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		if(UINT8(A) >= UINT8(operand)) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);
	}
	
	private void _CPX()
	{
		int r = UINT8(X) - UINT8(operand);
		if(((r & 0x80) >>> 7) == 1) STATUS(FLAG_N, 1);
		else STATUS(FLAG_N, 0);
		if((r & 0xFF) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		if(UINT8(X) >= UINT8(operand)) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);
	}
	
	private void _CPY()
	{
		int r = UINT8(Y) - UINT8(operand);
		if(((r & 0x80) >>> 7) == 1) STATUS(FLAG_N, 1);
		else STATUS(FLAG_N, 0);
		if((r & 0xFF) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		if(UINT8(Y) >= UINT8(operand)) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);
	}
	
	private void _DEC()
	{
		operand--;
		STATUS(FLAG_N, BIT(operand, 7));
		if(UINT8(operand) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _DEX()
	{
		X--;
		STATUS(FLAG_N, BIT(X, 7));
		if(UINT8(X) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _DEY()
	{
		Y--;
		STATUS(FLAG_N, BIT(Y, 7));
		if(UINT8(Y) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _EOR()
	{
		A = (byte)((UINT8(A) ^ UINT8(operand)) & 0xFF);
		STATUS(FLAG_N, BIT(A, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _INC()
	{
		operand++;
		STATUS(FLAG_N, BIT(operand, 7));
		if(UINT8(operand) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _INX()
	{
		X++;
		STATUS(FLAG_N, BIT(X, 7));
		if(UINT8(X) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _INY()
	{
		Y++;
		STATUS(FLAG_N, BIT(Y, 7));
		if(UINT8(Y) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _JMP()
	{
		PC = COMBINE16(PCL, PCH);
	}
	
	private void _LDA()
	{
		A = operand;		
		STATUS(FLAG_N, BIT(A, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _LDX()
	{
		X = operand;
		STATUS(FLAG_N, BIT(X, 7));
		if(UINT8(X) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _LDY()
	{
		Y = operand;
		STATUS(FLAG_N, BIT(Y, 7));
		if(UINT8(Y) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _LSR_ACC()
	{
		STATUS(FLAG_C, BIT(A, 0));		
		A = (byte) (UINT8(A) >>> 1);
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, 0);
	}
	
	private void _LSR_MEM()
	{
		STATUS(FLAG_C, BIT(operand, 0));
		operand = (byte) (UINT8(operand) >>> 1);
		if(UINT8(operand) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, 0);
	}
	
	private void _ORA()
	{
		A = (byte)((UINT8(A) | UINT8(operand)) & 0xFF);
		STATUS(FLAG_N, BIT(A, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _PHA()
	{
		PUSH(A);		
	}
	
	private void _PHP()
	{
		byte b = P;
		b = BIT(b, FLAG_B, 1);	
		b = BIT(b, FLAG_1, 1);
		PUSH(b);
		//STATUS(FLAG_B, 1);
		//PUSH(P);
	}
	
	private void _PLA()
	{
		A = POP();		
		STATUS(FLAG_N, BIT(A, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _PLP()
	{
		P = POP();
		STATUS(FLAG_B, 0);
		STATUS(FLAG_1, 1);
	}
	
	private void _ROL_ACC()
	{
		int c = STATUS(FLAG_C);
		int bit = BIT(A, 7);
		STATUS(FLAG_C, bit);
		A = (byte) (UINT8(A) << 1);
		A = BIT(A, 0, c);
		STATUS(FLAG_Z, UINT8(A) == 0 ? 1 : 0);
		STATUS(FLAG_N, BIT(A, 7));
	}
	
	private void _ROL_MEM()
	{
		int c = STATUS(FLAG_C);
		int bit = BIT(operand, 7);
		STATUS(FLAG_C, bit);
		operand = (byte) (UINT8(operand) << 1);
		operand = BIT(operand, 0, c);
		STATUS(FLAG_Z, UINT8(operand) == 0 ? 1 : 0);
		STATUS(FLAG_N, BIT(operand, 7));
		
		/*int c = STATUS(FLAG_C);
		int bit = BIT(operand, 7);
		STATUS(FLAG_C, bit);				
		operand = (byte) (UINT8(operand) << 1);
		operand = BIT(operand, 0, c);
		if(UINT8(operand) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, BIT(operand, 7));*/
	}
	
	private void _ROR_ACC()
	{
		int c = STATUS(FLAG_C);			
		int bit = BIT(A, 0);
		STATUS(FLAG_C, bit);
		A = (byte) (UINT8(A) >>> 1);
		A = BIT(A, 7, c);
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, BIT(A, 7));			
	}
	
	private void _ROR_MEM()
	{		
		int c = STATUS(FLAG_C);			
		int bit = BIT(operand, 0);
		STATUS(FLAG_C, bit);
		operand = (byte) (UINT8(operand) >>> 1);
		operand = BIT(operand, 7, c);
		if(UINT8(operand) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
		STATUS(FLAG_N, BIT(operand, 7));
	}
	
	//may not produce right result
	private void _SBC()
	{	
		int r = UINT8(A) - UINT8(operand) - ((~STATUS(FLAG_C)) & 1);
				
		if(((r & 0x80) >>> 7) == 1) STATUS(FLAG_N, 1);
		else STATUS(FLAG_N, 0);
		if(BIT(A, 7) != BIT(operand, 7) && ((r & 0x80) >>> 7) != BIT(A, 7)) STATUS(FLAG_V, 1);	//overflow
		else STATUS(FLAG_V, 0);
		if((r & 0xFF) == 0) STATUS(FLAG_Z, 1);		
		else STATUS(FLAG_Z, 0);
		if(UINT8(A) >= UINT8(operand)) STATUS(FLAG_C, 1);
		else STATUS(FLAG_C, 0);		
		
		A = (byte)(r & 0xFF);
	}
	
	private void _SEC()
	{
		STATUS(FLAG_C, 1);
	}
	
	private void _SED()
	{
		STATUS(FLAG_D, 1);
	}
	
	private void _SEI()
	{
		STATUS(FLAG_I, 1);
	}
	
	private void _TAX()
	{
		X = A;
		STATUS(FLAG_N, BIT(X, 7));
		if(UINT8(X) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}

	private void _TAY()
	{		
		Y = A;
		STATUS(FLAG_N, BIT(Y, 7));		
		if(UINT8(Y) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _TSX()
	{
		X = SP;
		STATUS(FLAG_N, BIT(X, 7));
		if(UINT8(X) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _TXA()
	{
		A = X;
		STATUS(FLAG_N, BIT(A, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	private void _TXS()
	{
		SP = X;
	}
	
	private void _TYA()
	{
		A = Y;
		STATUS(FLAG_N, BIT(Y, 7));
		if(UINT8(A) == 0) STATUS(FLAG_Z, 1);
		else STATUS(FLAG_Z, 0);
	}
	
	//===============================================================================

	private void ADC_IMM()
	{
		switch(cycle)
		{			
			case 2: 
				operand = BYTE(PC); 
				INCREMENT_PC();
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				BYTE(address);
				break;
			case 4:				
				effective = (short) (UINT16(address) + UINT8(X));
				operand = BYTE(effective);
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void ADC_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)
				{
					_ADC();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);				
				operand = BYTE(effective);
				_ADC();
				FINISH();
				break;
		}
	}
	
	private void AND_IMM()
	{
		switch(cycle)
		{			
			case 2: 
				operand = BYTE(PC); 
				INCREMENT_PC();
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				BYTE(address);
				break;
			case 4:				
				effective = (short) (UINT16(address) + UINT8(X));
				operand = BYTE(effective);
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_AND();
				FINISH();
				break;
		}
	}
	
	private void AND_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)
				{
					_AND();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);				
				operand = BYTE(effective);
				_AND();
				FINISH();
				break;
		}
	}
	
	private void ASL_A()
	{
		switch(cycle)
		{			
			case 2: 
				BYTE(PC); 				
				_ASL_ACC();
				FINISH();
				break;
		}
	}
	
	private void ASL_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ASL_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ASL_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_ASL_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ASL_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 				
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void BCC()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BCC() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BCS()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BCS() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BEQ()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BEQ() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BIT_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);
				_BIT();
				FINISH();
				break;
		}
	}
	
	private void BIT_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_BIT();
				FINISH();
				break;
		}
	}
	
	private void BMI()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BMI() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BNE()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BNE() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BPL()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BPL() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BVC()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BVC() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BVS()
	{
		switch(cycle)
		{			
			case 2: 	//R  fetch operand, increment PC
				operand = BYTE(PC); 
				INCREMENT_PC();		
				if(_BVS() == false) 
				{
					//According to Nintendulator logs and other sources, PC should not be incremented here.
					//if incremented, then it would skip over the opcode of the next instruction
					//INCREMENT_PC(); 
					FINISH();					
				}						
				break;
			case 3:	
				PCL = PCL();	//retrieve low byte of PC
				int r = UINT16(PC) + operand;
				PCL = (byte)(r & 0xFF);
				PC = (short)((UINT16(PC) & 0xFF00) | UINT8(PCL));	//set PC low to PCL	
				PCH = (byte)((r & 0xFF00) >>> 8);
				BYTE(PC);
				if(PCH() == PCH) FINISH();
				break;
			case 4:					
				PC = COMBINE16(PCL, PCH);
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void BRK_IMP()	//0x00, push PC, jump to IRQ vector
	{
		switch(cycle)
		{			
			case 2:				
				BYTE(PC);
				INCREMENT_PC();
				break;
			case 3: 				
				PCH = PCH();
				PUSH(PCH);
				SP--;
				break;
			case 4:
				PCL = PCL();
				PUSH(PCL);
				SP--;
				break;
			case 5: 	
				byte b = P;
				b = BIT(b, FLAG_B, 1);
				b = BIT(b, FLAG_1, 1);
				PUSH(b);
				SP--;
				break;
			case 6: 
				PCL = BYTE(IRQ_VECTOR);
				STATUS(FLAG_I, 1);
				NMI = true;
				break;
			case 7: 
				PCH = BYTE((short)(UINT16(IRQ_VECTOR) + 1));
				PC = COMBINE16(PCL, PCH);
				FINISH();
				break;
		}
	}
	
	private void CLC_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_CLC();
				FINISH();
				break;
		}
	}
	
	private void CLD_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_CLD();
				FINISH();
				break;
		}
	}
	
	private void CLI_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_CLI();
				FINISH();
				break;
		}
	}
	
	private void CLV_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_CLV();
				FINISH();
				break;
		}
	}
	
	private void CMP_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CMP_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)
				{
					_CMP();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);				
				operand = BYTE(effective);
				_CMP();
				FINISH();
				break;
		}
	}
	
	private void CPX_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_CPX();
				FINISH();
				break;
		}
	}
	
	private void CPX_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_CPX();
				FINISH();
				break;
		}
	}
	
	private void CPX_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_CPX();
				FINISH();
				break;
		}
	}
	
	private void CPY_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_CPY();
				FINISH();
				break;
		}
	}
	
	private void CPY_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_CPY();
				FINISH();
				break;
		}
	}
	
	private void CPY_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_CPY();
				FINISH();
				break;
		}
	}
	
	private void DEC_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_DEC();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void DEC_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_DEC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void DEC_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_DEC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void DEC_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_DEC();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void DEX_IMP()
	{
		switch(cycle)
		{
			case 2:
				_DEX();
				FINISH();
				break;
		}
	}
	
	private void DEY_IMP()
	{
		switch(cycle)
		{
			case 2:
				_DEY();
				FINISH();
				break;
		}
	}
	
	private void EOR_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void EOR_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)
				{
					_EOR();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);				
				operand = BYTE(effective);
				_EOR();
				FINISH();
				break;
		}
	}
	
	private void INC_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_INC();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void INC_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_INC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void INC_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_INC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void INC_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);			
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_INC();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void INX_IMP()
	{
		switch(cycle)
		{
			case 2:
				_INX();
				FINISH();
				break;
		}
	}
	
	private void INY_IMP()
	{
		switch(cycle)
		{
			case 2:
				_INY();
				FINISH();
				break;
		}
	}
	
	private void JMP_ABS()
	{
		switch(cycle)
		{
			case 2: 				
				PCL = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				PCH = BYTE(PC);
				_JMP();
				FINISH();				
				break;			
		}
	}
	
	private void JMP_IND()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				pointer = COMBINE16(low, high);
				INCREMENT_PC();
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				//no page crossing
				//(potentially buggy)
				high = BYTE((short)((UINT16(pointer) & 0xFF00) | ((UINT16(pointer) + 1) & 0xFF)));
				PC = COMBINE16(low, high);
				FINISH();
				break;
		}
	}
	
	private void JSR_ABS()
	{
		switch(cycle)
		{
			case 2:
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				BYTE((short)(STACK_START + UINT8(SP)));	//dummy read?	
				break;			
			case 4:
				PCH = PCH();
				PUSH(PCH);
				SP--;
				break;
			case 5:
				PCL = PCL();
				PUSH(PCL);
				SP--;
				break;
			case 6:
				high = BYTE(PC);
				PC = COMBINE16(low, high);
				FINISH();
				break;
		}
	}
	
	private void LDA_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				if(lineNumber == 3624) System.out.println(String.format("\t\tLDA_ABS(%d): $%02X", cycle, UINT16(PC)));
				low = BYTE(PC);				
				INCREMENT_PC();
				break;
			case 3:
				if(lineNumber == 3624) System.out.println(String.format("\t\tLDA_ABS(%d): $%02X", cycle, UINT16(PC)));
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				if(lineNumber == 3624) System.out.println(String.format("\t\tLDA_ABS(%d): $%02X", cycle, UINT16(effective)));
				if(lineNumber == 3624) System.out.println(String.format("\t\tLDA_ABS(%d): RAM[0x400] = $%01X", cycle, UINT8(RAM[0x400])));
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_ABS_Y()
	{
		switch(cycle)
		{			
			case 1:				
				break;
			case 2: 
				low = BYTE(PC); 				
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDA_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				/*if(UINT16(DebugPool.PC) == 0xC2BE && UINT8(DebugPool.Y) == 0x7D && UINT8(DebugPool.X) == 0x0E)
					System.out.println(String.format("\tLDA_IND_Y(%d): $%04X", cycle, UINT16(PC)));*/
				address = (short)UINT8(BYTE(PC));		
				//if(lineNumber == 3379) System.out.println(String.format("\t\tLDA_IND_Y(2): $%02X", UINT16(PC)));
				INCREMENT_PC();
				break;
			case 3:
				/*if(UINT16(DebugPool.PC) == 0xC2BE && UINT8(DebugPool.Y) == 0x7D && UINT8(DebugPool.X) == 0x0E)
					System.out.println(String.format("\tLDA_IND_Y(%d): $%04X", cycle, UINT16(address)));*/
				low = BYTE(address);
				//if(lineNumber == 3379) System.out.println(String.format("\t\tLDA_IND_Y(2): $%02X", UINT16(address)));
				break;
			case 4:
				/*if(UINT16(DebugPool.PC) == 0xC2BE && UINT8(DebugPool.Y) == 0x7D && UINT8(DebugPool.X) == 0x0E)
					System.out.println(String.format("\tLDA_IND_Y(%d): $%04X", cycle, UINT16((short)((UINT16(address) + 1) & 0xFF))));*/
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				//high = BYTE((short)((UINT16(address) + 1)));
				//if(lineNumber == 3379) System.out.println(String.format("\t\tLDA_IND_Y(2): $%02X", (UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				//if(lineNumber == 3379) System.out.println(String.format("\t\tLDA_IND_Y(2): $%02X", UINT16(effective)));
				
				/*if(UINT16(DebugPool.PC) == 0xC2BE && UINT8(DebugPool.Y) == 0x7D && UINT8(DebugPool.X) == 0x0E)
				{
					System.out.println(String.format("\tLDA_IND_Y(%d): r = $%X, low = $%02X", cycle, r, UINT8(low)));
					System.out.println(String.format("\tLDA_IND_Y(%d): $%04X", cycle, UINT16(effective)));
					System.out.println(String.format("\tLDA_IND_Y(%d): ((r & 0xFF) + UINT8(Y)) < 0x100 = %b", cycle, (((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100) ));
					System.out.println(String.format("\tLDA_IND_Y(%d): r < 0x10000 = %b", cycle, (r < 0x10000) ));
				}*/
				
				operand = BYTE(effective);				
				//if((UINT8(low) + UINT8(Y)) < 0x100 && r < 0x10000)
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)				
				{					
					_LDA();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);		
				//if(lineNumber == 3379) System.out.println(String.format("\t\tLDA_IND_Y(2): $%02X", UINT16(effective)));
				
				/*if(UINT16(DebugPool.PC) == 0xC2BE && UINT8(DebugPool.Y) == 0x7D && UINT8(DebugPool.X) == 0x0E)
					System.out.println(String.format("\tLDA_IND_Y(%d): $%04X", cycle, UINT16(effective)));*/
				
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}
	
	private void LDX_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void LDX_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_LDX();
				FINISH();
				break;
		}
	}	
	
	private void LDX_Z_Y()
	{
		switch(cycle)	//correct implementation of Z_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(Y)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void LDX_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void LDX_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void LDY_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_LDY();
				FINISH();
				break;
		}
	}
	
	private void LDY_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_LDY();
				FINISH();
				break;
		}
	}
	
	private void LDY_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_LDY();
				FINISH();
				break;
		}
	}
	
	private void LDY_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDY();
				FINISH();
				break;
		}
	}
	
	private void LDY_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDY();
				FINISH();
				break;
		}
	}
	
	private void LSR_A()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_LSR_ACC();
				FINISH();
				break;				
		}
	}
	
	private void LSR_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);										
				break;
			case 4:
				BYTE(effective, operand);	//dummy write				
				_LSR_MEM();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void LSR_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_LSR_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void LSR_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_LSR_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void LSR_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 				
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_LSR_MEM();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void NOP_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				FINISH();
				break;
		}
	}
	
	private void ORA_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void ORA_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)
				{
					_ORA();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);				
				operand = BYTE(effective);
				_ORA();
				FINISH();
				break;
		}
	}
	
	private void PHA_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC); //dummy read
				break;
			case 3:
				_PHA();
				SP--;
				FINISH();
				break;
		}
	}
	
	private void PHP_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC); //dummy read
				break;
			case 3:
				_PHP();
				SP--;
				FINISH();
				break;
		}
	}
	
	private void PLA_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC); //dummy read
				break;
			case 3:
				BYTE((short)(STACK_START + UINT8(SP)));	//dummy read
				SP++;
				break;
			case 4:				
				_PLA();				
				FINISH();
				break;
		}
	}
	
	private void PLP_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC); //dummy read
				break;
			case 3:
				BYTE((short)(STACK_START + UINT8(SP)));	//dummy read
				SP++;
				break;
			case 4:				
				_PLP();				
				FINISH();
				break;
		}
	}
	
	private void ROL_A()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_ROL_ACC();
				FINISH();
				break;				
		}
	}
	
	private void ROL_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROL_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROL_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_ROL_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROL_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROR_A()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_ROR_ACC();
				FINISH();
				break;				
		}
	}
	
	private void ROR_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROR_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROR_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_ROR_MEM();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ROR_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);	
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void RTI_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);							
				break;	
			case 3:
				BYTE((short)(STACK_START + UINT8(SP)));
				SP++;
				break;
			case 4:
				P = (byte)((UINT8(P) & 0b00110000) | (UINT8(POP()) & 0b11001111));	//ignore bits 5 & 4				
				SP++;
				break;
			case 5:
				PCL = POP();
				SP++;
				break;
			case 6:
				PCH = POP();
				PC = COMBINE16(PCL, PCH);
				FINISH();
				break;
		}
	}
		
	private void RTS_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);							
				break;	
			case 3:
				BYTE((short)(STACK_START + UINT8(SP)));
				SP++;
				break;
			case 4:
				PCL = POP();
				SP++;
				break;
			case 5:
				PCH = POP();
				PC = COMBINE16(PCL, PCH);
				break;
			case 6:
				BYTE(PC);
				INCREMENT_PC();
				FINISH();
				break;
		}
	}
	
	private void SBC_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SBC_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if(((UINT16(pointer) & 0xFF) + UINT8(Y)) < 0x100 && r < 0x10000)
				{
					_SBC();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);				
				operand = BYTE(effective);
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void SEC_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_SEC();
				FINISH();
				break;
		}
	}

	private void SED_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_SED();
				FINISH();
				break;
		}
	}
	
	private void SEI_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_SEI();
				FINISH();
				break;
		}
	}
		
	private void STA_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:				
				BYTE(effective, A);
				FINISH();
				break;
		}
	}
	
	private void STA_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				BYTE(effective, A);
				FINISH();
				break;
		}
	}
	
	private void STA_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				BYTE(effective, A);
				FINISH();
				break;
		}
	}
	
	private void STA_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 				
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				BYTE(effective, A);				
				FINISH();
				break;
		}
	}
	
	private void STA_ABS_Y()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 				
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				BYTE(effective, A);				
				FINISH();
				break;
		}
	}
	
	private void STA_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X write
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:				
				//int r = UINT8(BYTE(address)) + UINT8(X);
				//pointer = (short)(r & 0xFF);	
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				//high = BYTE((short)(UINT16(pointer) + 1));
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				BYTE(effective, A);
				FINISH();
				break;
		}
	}
	
	private void STA_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y write?
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));									
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF)); 
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				if((UINT8(low) + UINT8(Y)) > 0xFF && r > 0xFFFF)
				//if(((UINT16(pointer) & 0xFF) + UINT8(Y)) > 0xFF && r > 0xFFFF)
				{
					high++;
				}	
				break;
			case 6:
				effective = COMBINE16(low, high);					
				BYTE(effective, A);
				FINISH();
				break;
		}
	}
	
	private void STX_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:							
				BYTE(effective, X);
				FINISH();
				break;
		}
	}
	
	private void STX_Z_Y()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(Y)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				BYTE(effective, X);
				FINISH();
				break;
		}
	}
	
	private void STX_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				BYTE(effective, X);
				FINISH();
				break;
		}
	}
	
	private void STY_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:							
				BYTE(effective, Y);
				FINISH();
				break;
		}
	}
	
	private void STY_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				BYTE(effective, Y);
				FINISH();
				break;
		}
	}
	
	private void STY_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				BYTE(effective, Y);
				FINISH();
				break;
		}
	}
	
	private void TAX_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_TAX();
				FINISH();
				break;
		}
	}
	
	private void TAY_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_TAY();
				FINISH();
				break;
		}
	}
	
	private void TSX_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_TSX();
				FINISH();
				break;
		}
	}
	
	private void TXA_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_TXA();
				FINISH();
				break;
		}
	}
	
	private void TXS_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_TXS();
				FINISH();
				break;
		}
	}
	
	private void TYA_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				_TYA();
				FINISH();
				break;
		}
	}
	
	//===============================================================================
	//ILLEGAL INSTRUCTIONS
	//===============================================================================
	
	private void ILL_DCP_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_DEC();
				_CMP();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_DCP_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_DEC();
				_CMP();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_DCP_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_DEC();
				_CMP();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_DCP_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_DEC();
				_CMP();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_DCP_ABS_Y()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_DEC();
				_CMP();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_DCP_IND_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);								
				break;
			case 7:
				BYTE(effective, operand);
				_DEC();
				_CMP();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_DCP_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);		
				if(((r&0xFF00)>>>8) != UINT8(high)) high += 0x01;	//increment page if high byte of r is not equal to high byte of pointer
				break;
			case 6:
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);				
				break;
			case 7:
				BYTE(effective, operand);
				_DEC();
				_CMP();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_INC();
				_SBC();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_INC();
				_SBC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_INC();
				_SBC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_INC();
				_SBC();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_ABS_Y()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_INC();
				_SBC();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_IND_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);								
				break;
			case 7:
				BYTE(effective, operand);
				_INC();
				_SBC();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_ISB_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);		
				if(((r&0xFF00)>>>8) != UINT8(high)) high += 0x01;	//increment page if high byte of r is not equal to high byte of pointer
				break;
			case 6:
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);				
				break;
			case 7:
				BYTE(effective, operand);
				_INC();
				_SBC();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_NOP_IMP()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);				
				FINISH();
				break;
		}
	}
	
	private void ILL_NOP_IMM()
	{
		switch(cycle)
		{
			case 2:
				BYTE(PC);
				INCREMENT_PC();
				FINISH();
				break;
		}
	}
	
	private void ILL_NOP_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:							
				BYTE(effective);
				FINISH();
				break;
		}
	}
	
	private void ILL_NOP_Z_X()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				BYTE(effective);
				FINISH();
				break;
		}
	}
	
	private void ILL_NOP_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				BYTE(effective);
				FINISH();
				break;
		}
	}
	
	private void ILL_NOP_ABS_X()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);				
				FINISH();
				break;
		}
	}
	
	private void ILL_LAX_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);				
				_LDA();
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void ILL_LAX_Z_Y()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(Y)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				operand = BYTE(effective);
				_LDA();
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void ILL_LAX_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);				
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDA();
				_LDX();
				FINISH();
				break;
		}
	}	
	
	private void ILL_LAX_ABS_Y()
	{
		switch(cycle)
		{			
			case 1:				
				break;
			case 2: 
				low = BYTE(PC); 				
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				if(r < 0x100) SKIP();	//skip next cycle if page boundary is not crossed
				INCREMENT_PC();
				break;
			case 4:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDA();
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void ILL_LAX_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X read
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				_LDA();
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void ILL_LAX_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);				
				if((UINT8(low) + UINT8(Y)) < 0x100 && r < 0x10000)
				{					
					_LDA();
					_LDX();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);
				_LDA();
				_LDX();
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				_AND();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				_AND();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_ROL_MEM();
				_AND();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				_AND();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_ABS_Y()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ROL_MEM();
				_AND();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_IND_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));							
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	
				break;
			case 7:
				BYTE(effective, operand);
				_ROL_MEM();
				_AND();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RLA_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);		
				if(((r&0xFF00)>>>8) != UINT8(high)) high += 0x01;	//increment page if high byte of r is not equal to high byte of pointer
				break;
			case 6:
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);				
				break;
			case 7:
				BYTE(effective, operand);
				_ROL_MEM();
				_AND();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				_ADC();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				_ADC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_ROR_MEM();
				_ADC();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				_ADC();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_ABS_Y()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ROR_MEM();
				_ADC();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_IND_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);								
				break;
			case 7:
				BYTE(effective, operand);
				_ROR_MEM();
				_ADC();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_RRA_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);		
				if(((r&0xFF00)>>>8) != UINT8(high)) high += 0x01;	//increment page if high byte of r is not equal to high byte of pointer
				break;
			case 6:
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);				
				break;
			case 7:
				BYTE(effective, operand);
				_ROR_MEM();
				_ADC();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SAX_Z()
	{
		switch(cycle)
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				byte r = (byte) (UINT8(A) & UINT8(X));
				BYTE(effective, r);
				FINISH();
				break;
		}
	}
	
	private void ILL_SAX_Z_Y()
	{
		switch(cycle)
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short) ((UINT16(address) + UINT8(Y)) & 0xFF);	//effective address wraps around
				break;
			case 4:
				byte r = (byte) (UINT8(A) & UINT8(X));
				BYTE(effective, r);
				FINISH();
				break;
		}
	}
	
	private void ILL_SAX_ABS()
	{
		switch(cycle)
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				byte r = (byte) (UINT8(A) & UINT8(X));
				BYTE(effective, r);
				FINISH();
				break;
		}
	}	
	
	private void ILL_SAX_IND_X()
	{
		switch(cycle)	//correct implementation of IND_X write
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:				
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				byte r = (byte) (UINT8(A) & UINT8(X));
				BYTE(effective, r);
				FINISH();
				break;
		}
	}
	
	private void ILL_SBC_IMM()
	{
		switch(cycle)
		{			
			case 2:
				operand = BYTE(PC);
				INCREMENT_PC();
				_SBC();
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				_ORA();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				_ORA();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_ASL_MEM();
				_ORA();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				_ORA();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_ABS_Y()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_ASL_MEM();
				_ORA();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_IND_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);								
				break;
			case 7:
				BYTE(effective, operand);
				_ASL_MEM();
				_ORA();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SLO_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);		
				if(((r&0xFF00)>>>8) != UINT8(high)) high += 0x01;	//increment page if high byte of r is not equal to high byte of pointer
				break;
			case 6:
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);				
				break;
			case 7:
				BYTE(effective, operand);
				_ASL_MEM();
				_ORA();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_Z()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				effective = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				operand = BYTE(effective);								
				break;
			case 4:
				BYTE(effective, operand);	//dummy write
				_LSR_MEM();
				_EOR();
				break;
			case 5:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_Z_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);	//dummy read
				effective = (short)((UINT16(address) + UINT8(X)) & 0xFF);	//effective address wraps around
				break;
			case 4:				
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);	//dummy write
				_LSR_MEM();
				_EOR();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_ABS()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC);
				INCREMENT_PC();
				break;
			case 3:
				high = BYTE(PC);
				INCREMENT_PC();
				break;
			case 4: 
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 5:
				BYTE(effective, operand);
				_LSR_MEM();
				_EOR();
				break;
			case 6:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_ABS_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(X);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_LSR_MEM();
				_EOR();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_ABS_Y()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				low = BYTE(PC); 
				INCREMENT_PC();				
				break;
			case 3:
				high = BYTE(PC);								
				INCREMENT_PC();
				break;
			case 4:
				int r = UINT8(low) + UINT8(Y);
				low = (byte)(r&0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);	//dummy read (a page less)
				if(r > 0xFF) high += 0x01;	//fix high byte of address (page)
				break;
			case 5:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);
				break;
			case 6:
				BYTE(effective, operand);	//dummy write
				_LSR_MEM();
				_EOR();
				break;
			case 7:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_IND_X()
	{
		switch(cycle)	//RMW
		{			
			case 2: 
				address = (short)UINT8(BYTE(PC));				
				INCREMENT_PC();
				break;
			case 3:
				BYTE(address);
				pointer = (short) ((UINT16(address) + UINT8(X))&0xFF);				
				break;
			case 4:
				low = BYTE(pointer);
				break;
			case 5:				
				high = BYTE( (short) ((UINT16(pointer) + 1) & 0xFF) );
				break;
			case 6:
				effective = COMBINE16(low, high);
				operand = BYTE(effective);								
				break;
			case 7:
				BYTE(effective, operand);
				_LSR_MEM();
				_EOR();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	private void ILL_SRE_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				INCREMENT_PC();
				break;
			case 3:
				low = BYTE(address);
				break;
			case 4:
				high = BYTE((short)((UINT16(address) + 1) & 0xFF));
				break;
			case 5:				
				pointer = COMBINE16(low, high);
				int r = UINT16(pointer) + UINT8(Y);
				low = (byte)(r & 0xFF);
				effective = COMBINE16(low, high);
				operand = BYTE(effective);		
				if(((r&0xFF00)>>>8) != UINT8(high)) high += 0x01;	//increment page if high byte of r is not equal to high byte of pointer
				break;
			case 6:
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);				
				break;
			case 7:
				BYTE(effective, operand);
				_LSR_MEM();
				_EOR();
				break;
			case 8:
				BYTE(effective, operand);
				FINISH();
				break;
		}
	}
	
	
	//===============================================================================

	private void FETCH()
	{
		IR = UINT8(BYTE(PC));	//fetch opcode	
		
		if(nestest) autoNestest();
		//printNestestLog();
		if(debug)
		{
			//printNestestLog();
		}
		if(isBreakpointEnabled())
		{
			if(UINT16(PC) == BREAKPOINT)			
			{
				GuiController.pause = true;
			}
		}
		if(debug) updateDebugPool();				
		
		if(!NMI && !IRQ) INCREMENT_PC();		//PC increment is supressed during NMI or IRQ
	}
	
	private void DECODE_AND_EXECUTE()
	{
		try
		{			
			switch(IR)
			{				
				case ADC_IMM: 	ADC_IMM(); break;
				case ADC_Z: 	ADC_Z(); break;
				case ADC_Z_X: 	ADC_Z_X(); break;				
				case ADC_ABS: 	ADC_ABS(); break;
				case ADC_ABS_X: ADC_ABS_X(); break;
				case ADC_ABS_Y: ADC_ABS_Y(); break;
				case ADC_IND_X: ADC_IND_X(); break;
				case ADC_IND_Y: ADC_IND_Y(); break;
				
				case AND_IMM: 	AND_IMM(); break;
				case AND_Z: 	AND_Z(); break;
				case AND_Z_X: 	AND_Z_X(); break;				
				case AND_ABS: 	AND_ABS(); break;
				case AND_ABS_X: AND_ABS_X(); break;
				case AND_ABS_Y: AND_ABS_Y(); break;
				case AND_IND_X: AND_IND_X(); break;
				case AND_IND_Y: AND_IND_Y(); break;
				
				case ASL_A: 	ASL_A(); break;
				case ASL_Z: 	ASL_Z(); break;
				case ASL_Z_X: 	ASL_Z_X(); break;				
				case ASL_ABS: 	ASL_ABS(); break;
				case ASL_ABS_X: ASL_ABS_X(); break;
				
				case BCC_REL: 	BCC(); break;
				
				case BCS_REL: 	BCS(); break;
				
				case BEQ_REL: 	BEQ(); break;
				
				case BIT_Z:   	BIT_Z(); break;
				
				case BIT_ABS: 	BIT_ABS(); break;
				
				case BMI_REL: 	BMI(); break;
				
				case BNE_REL: 	BNE(); break;
				
				case BPL_REL: 	BPL(); break;
				
				case BVC_REL:   BVC(); break;
				
				case BVS_REL:   BVS(); break;
				
				case BRK_IMP: 	BRK_IMP(); break;
				
				case CLC_IMP:   CLC_IMP(); break;
				
				case CLD_IMP:   CLD_IMP(); break;
				
				case CLI_IMP:   CLI_IMP(); break;
				
				case CLV_IMP:   CLV_IMP(); break;
				
				case CMP_IMM:	CMP_IMM(); break;
				case CMP_Z:		CMP_Z(); break;
				case CMP_Z_X:	CMP_Z_X(); break;
				case CMP_ABS:	CMP_ABS(); break;
				case CMP_ABS_X:	CMP_ABS_X(); break;
				case CMP_ABS_Y:	CMP_ABS_Y(); break;
				case CMP_IND_X:	CMP_IND_X(); break;
				case CMP_IND_Y:	CMP_IND_Y(); break;
				
				case CPX_IMM:	CPX_IMM(); break;
				case CPX_Z:		CPX_Z(); break;
				case CPX_ABS:	CPX_ABS(); break;
				
				case CPY_IMM:	CPY_IMM(); break;
				case CPY_Z:		CPY_Z(); break;
				case CPY_ABS:	CPY_ABS(); break;
				
				case DEC_Z:		DEC_Z(); break;
				case DEC_Z_X:	DEC_Z_X(); break;
				case DEC_ABS:	DEC_ABS(); break;
				case DEC_ABS_X:	DEC_ABS_X(); break;
				
				case DEX_IMP: 	DEX_IMP(); break;
				
				case DEY_IMP: 	DEY_IMP(); break;
				
				case EOR_IMM:	EOR_IMM(); break;
				case EOR_Z:		EOR_Z(); break;
				case EOR_Z_X:	EOR_Z_X(); break;
				case EOR_ABS:	EOR_ABS(); break;
				case EOR_ABS_X:	EOR_ABS_X(); break;
				case EOR_ABS_Y:	EOR_ABS_Y(); break;
				case EOR_IND_X:	EOR_IND_X(); break;
				case EOR_IND_Y:	EOR_IND_Y(); break;
				
				case INC_Z:		INC_Z(); break;
				case INC_Z_X:	INC_Z_X(); break;
				case INC_ABS:	INC_ABS(); break;
				case INC_ABS_X:	INC_ABS_X(); break;
				
				case INX_IMP: 	INX_IMP(); break;
				
				case INY_IMP: 	INY_IMP(); break;
				
				case JMP_ABS:	JMP_ABS(); break;
				case JMP_IND:	JMP_IND(); break;
				
				case JSR_ABS:	JSR_ABS(); break;
				
				case LDA_IMM:	LDA_IMM(); break;
				case LDA_Z:		LDA_Z(); break;
				case LDA_Z_X:	LDA_Z_X(); break;
				case LDA_ABS:	LDA_ABS(); break;
				case LDA_ABS_X:	LDA_ABS_X(); break;
				case LDA_ABS_Y:	LDA_ABS_Y(); break;
				case LDA_IND_X:	LDA_IND_X(); break;
				case LDA_IND_Y:	LDA_IND_Y(); break;
				
				case LDX_IMM:	LDX_IMM(); break;
				case LDX_Z:		LDX_Z(); break;
				case LDX_Z_Y:	LDX_Z_Y(); break;
				case LDX_ABS:	LDX_ABS(); break;
				case LDX_ABS_Y:	LDX_ABS_Y(); break;
				
				case LDY_IMM:	LDY_IMM(); break;
				case LDY_Z:		LDY_Z(); break;
				case LDY_Z_X:	LDY_Z_X(); break;
				case LDY_ABS:	LDY_ABS(); break;
				case LDY_ABS_X:	LDY_ABS_X(); break;
				
				case LSR_A: 	LSR_A(); break;
				case LSR_Z: 	LSR_Z(); break;
				case LSR_Z_X: 	LSR_Z_X(); break;				
				case LSR_ABS: 	LSR_ABS(); break;
				case LSR_ABS_X: LSR_ABS_X(); break;
				
				case NOP_IMP:	NOP_IMP(); break;
				
				case ORA_IMM:	ORA_IMM(); break;
				case ORA_Z:		ORA_Z(); break;
				case ORA_Z_X:	ORA_Z_X(); break;
				case ORA_ABS:	ORA_ABS(); break;
				case ORA_ABS_X:	ORA_ABS_X(); break;
				case ORA_ABS_Y:	ORA_ABS_Y(); break;
				case ORA_IND_X:	ORA_IND_X(); break;
				case ORA_IND_Y:	ORA_IND_Y(); break;
				
				case PHA_IMP: 	PHA_IMP(); break;
				
				case PHP_IMP: 	PHP_IMP(); break;
				
				case PLA_IMP: 	PLA_IMP(); break;
				
				case PLP_IMP: 	PLP_IMP(); break;
				
				case ROL_A: 	ROL_A(); break;
				case ROL_Z: 	ROL_Z(); break;
				case ROL_Z_X: 	ROL_Z_X(); break;				
				case ROL_ABS: 	ROL_ABS(); break;
				case ROL_ABS_X: ROL_ABS_X(); break;
				
				case ROR_A: 	ROR_A(); break;
				case ROR_Z: 	ROR_Z(); break;
				case ROR_Z_X: 	ROR_Z_X(); break;				
				case ROR_ABS: 	ROR_ABS(); break;
				case ROR_ABS_X: ROR_ABS_X(); break;
				
				case RTI_IMP: 	RTI_IMP(); break;
				
				case RTS_IMP: 	RTS_IMP(); break;
				
				case SBC_IMM: 	SBC_IMM(); break;
				case SBC_Z: 	SBC_Z(); break;
				case SBC_Z_X: 	SBC_Z_X(); break;				
				case SBC_ABS: 	SBC_ABS(); break;
				case SBC_ABS_X: SBC_ABS_X(); break;
				case SBC_ABS_Y: SBC_ABS_Y(); break;
				case SBC_IND_X: SBC_IND_X(); break;
				case SBC_IND_Y: SBC_IND_Y(); break;
				
				case SEC_IMP: 	SEC_IMP(); break;
				
				case SED_IMP: 	SED_IMP(); break;
				
				case SEI_IMP: 	SEI_IMP(); break;
				
				case STA_Z:		STA_Z(); break;
				case STA_Z_X:	STA_Z_X(); break;
				case STA_ABS:	STA_ABS(); break;
				case STA_ABS_X:	STA_ABS_X(); break;
				case STA_ABS_Y:	STA_ABS_Y(); break;
				case STA_IND_X:	STA_IND_X(); break;
				case STA_IND_Y:	STA_IND_Y(); break;
				
				case STX_Z:		STX_Z(); break;
				case STX_Z_Y:	STX_Z_Y(); break;
				case STX_ABS:	STX_ABS(); break;
				
				case STY_Z:		STY_Z(); break;
				case STY_Z_X:	STY_Z_X(); break;
				case STY_ABS:	STY_ABS(); break;
				
				case TAX_IMP:	TAX_IMP(); break;
				
				case TAY_IMP:	TAY_IMP(); break;
				
				case TSX_IMP:	TSX_IMP(); break;
				
				case TXA_IMP:	TXA_IMP(); break;
				
				case TXS_IMP:	TXS_IMP(); break;
				
				case TYA_IMP:	TYA_IMP(); break;
				
				/*illegal instructions*/
				
				case ILL_DCP_Z:			ILL_DCP_Z(); break;
				case ILL_DCP_Z_X:		ILL_DCP_Z_X(); break;
				case ILL_DCP_ABS:		ILL_DCP_ABS(); break;
				case ILL_DCP_ABS_X:		ILL_DCP_ABS_X(); break;
				case ILL_DCP_ABS_Y:		ILL_DCP_ABS_Y(); break;
				case ILL_DCP_IND_X:		ILL_DCP_IND_X(); break;
				case ILL_DCP_IND_Y:		ILL_DCP_IND_Y(); break;
				
				case ILL_ISB_Z:			ILL_ISB_Z(); break;
				case ILL_ISB_Z_X:		ILL_ISB_Z_X(); break;
				case ILL_ISB_ABS:		ILL_ISB_ABS(); break;
				case ILL_ISB_ABS_X:		ILL_ISB_ABS_X(); break;
				case ILL_ISB_ABS_Y:		ILL_ISB_ABS_Y(); break;
				case ILL_ISB_IND_X:		ILL_ISB_IND_X(); break;
				case ILL_ISB_IND_Y:		ILL_ISB_IND_Y(); break;
				
				case ILL_LAX_Z:			ILL_LAX_Z(); break;
				case ILL_LAX_Z_Y:		ILL_LAX_Z_Y(); break;
				case ILL_LAX_ABS:		ILL_LAX_ABS(); break;
				case ILL_LAX_ABS_Y:		ILL_LAX_ABS_Y(); break;
				case ILL_LAX_IND_X:		ILL_LAX_IND_X(); break;
				case ILL_LAX_IND_Y:		ILL_LAX_IND_Y(); break;
				
				//IMP
				case ILL_NOP_IMP_0:		ILL_NOP_IMP(); break;
				case ILL_NOP_IMP_1:		ILL_NOP_IMP(); break;
				case ILL_NOP_IMP_2: 	ILL_NOP_IMP(); break;
				case ILL_NOP_IMP_3: 	ILL_NOP_IMP(); break;
				case ILL_NOP_IMP_4: 	ILL_NOP_IMP(); break;
				case ILL_NOP_IMP_5: 	ILL_NOP_IMP(); break;
				//IMM
				case ILL_NOP_IMM_0: 	ILL_NOP_IMM(); break;
				case ILL_NOP_IMM_1: 	ILL_NOP_IMM(); break;
				case ILL_NOP_IMM_2: 	ILL_NOP_IMM(); break;
				case ILL_NOP_IMM_3: 	ILL_NOP_IMM(); break;
				case ILL_NOP_IMM_4: 	ILL_NOP_IMM(); break;
				//Z
				case ILL_NOP_Z_0: 		ILL_NOP_Z(); break;
				case ILL_NOP_Z_1: 		ILL_NOP_Z(); break;
				case ILL_NOP_Z_2: 		ILL_NOP_Z(); break;
				//Z_X
				case ILL_NOP_Z_X_0: 	ILL_NOP_Z_X(); break;
				case ILL_NOP_Z_X_1: 	ILL_NOP_Z_X(); break;
				case ILL_NOP_Z_X_2: 	ILL_NOP_Z_X(); break;
				case ILL_NOP_Z_X_3: 	ILL_NOP_Z_X(); break;
				case ILL_NOP_Z_X_4: 	ILL_NOP_Z_X(); break;
				case ILL_NOP_Z_X_5: 	ILL_NOP_Z_X(); break;
				//ABS
				case ILL_NOP_ABS_0: 	ILL_NOP_ABS(); break;
				//ABS_X
				case ILL_NOP_ABS_X_0: 	ILL_NOP_ABS_X(); break;
				case ILL_NOP_ABS_X_1: 	ILL_NOP_ABS_X(); break;
				case ILL_NOP_ABS_X_2: 	ILL_NOP_ABS_X(); break;
				case ILL_NOP_ABS_X_3: 	ILL_NOP_ABS_X(); break;
				case ILL_NOP_ABS_X_4: 	ILL_NOP_ABS_X(); break;
				case ILL_NOP_ABS_X_5: 	ILL_NOP_ABS_X(); break;
				
				case ILL_RLA_Z:			ILL_RLA_Z(); break;
				case ILL_RLA_Z_X:		ILL_RLA_Z_X(); break;
				case ILL_RLA_ABS:		ILL_RLA_ABS(); break;
				case ILL_RLA_ABS_X:		ILL_RLA_ABS_X(); break;
				case ILL_RLA_ABS_Y:		ILL_RLA_ABS_Y(); break;
				case ILL_RLA_IND_X:		ILL_RLA_IND_X(); break;
				case ILL_RLA_IND_Y:		ILL_RLA_IND_Y(); break;
				
				case ILL_RRA_Z:			ILL_RRA_Z(); break;
				case ILL_RRA_Z_X:		ILL_RRA_Z_X(); break;
				case ILL_RRA_ABS:		ILL_RRA_ABS(); break;
				case ILL_RRA_ABS_X:		ILL_RRA_ABS_X(); break;
				case ILL_RRA_ABS_Y:		ILL_RRA_ABS_Y(); break;
				case ILL_RRA_IND_X:		ILL_RRA_IND_X(); break;
				case ILL_RRA_IND_Y:		ILL_RRA_IND_Y(); break;
				
				case ILL_SAX_Z:			ILL_SAX_Z(); break;
				case ILL_SAX_Z_Y:		ILL_SAX_Z_Y(); break;
				case ILL_SAX_ABS:		ILL_SAX_ABS(); break;
				case ILL_SAX_IND_X:		ILL_SAX_IND_X(); break;
				
				case ILL_SBC_IMM:		ILL_SBC_IMM(); break;
				
				case ILL_SLO_Z:			ILL_SLO_Z(); break;
				case ILL_SLO_Z_X:		ILL_SLO_Z_X(); break;
				case ILL_SLO_ABS:		ILL_SLO_ABS(); break;
				case ILL_SLO_ABS_X:		ILL_SLO_ABS_X(); break;
				case ILL_SLO_ABS_Y:		ILL_SLO_ABS_Y(); break;
				case ILL_SLO_IND_X:		ILL_SLO_IND_X(); break;
				case ILL_SLO_IND_Y:		ILL_SLO_IND_Y(); break;
				
				case ILL_SRE_Z:			ILL_SRE_Z(); break;
				case ILL_SRE_Z_X:		ILL_SRE_Z_X(); break;
				case ILL_SRE_ABS:		ILL_SRE_ABS(); break;
				case ILL_SRE_ABS_X:		ILL_SRE_ABS_X(); break;
				case ILL_SRE_ABS_Y:		ILL_SRE_ABS_Y(); break;
				case ILL_SRE_IND_X:		ILL_SRE_IND_X(); break;
				case ILL_SRE_IND_Y:		ILL_SRE_IND_Y(); break;
				
				
				default:
					System.out.println(String.format("ERROR: UNKNOWN INSTRUCTION $%01X AT $%02X", IR, UINT16(PC)-1));
					//printNestestLog();
					throw new Exception("UNKNOWN OPCODE");					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void printNestestLog()
	{
		//System.out.println(String.format("\tPC: %02X  OPCODE: %01X  OPERAND(S): %01X %01X     A:%01X X:%01X Y:%01X P:%01X SP:%01X CYC:%d\tLINE: %d", UINT16(PC), IR, _read8rom((short)(UINT16(PC)+1)), _read8rom((short)(UINT16(PC)+2)), A, X, Y, P, SP, cycleTotal+6, lineNumber));
		System.out.println(String.format("\tPC: %02X  OPCODE: %01X  OPERAND(S): %01X %01X     A:%01X X:%01X Y:%01X P:%01X SP:%01X CYC:%d\tLINE: %d, %04X", UINT16(PC), IR, 0, 0, A, X, Y, P, SP, cycleTotal+6, lineNumber, UINT16(COMBINE16(BYTESAFE((short) 0x02), BYTESAFE((short) 0x03)))));
	}
	
	private void autoNestest()
	{		
		lineNumber++;
		try {
			line = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(line == null) return;
		int pc 	= Integer.parseInt(line.substring(0, 4), 16);
		int a 	= Integer.parseInt(line.substring(50, 52), 16);
		int x 	= Integer.parseInt(line.substring(55, 57), 16);
		int y 	= Integer.parseInt(line.substring(60, 62), 16);
		int p 	= Integer.parseInt(line.substring(65, 67), 16);
		int sp 	= Integer.parseInt(line.substring(71, 73), 16);
		int cyc = Integer.parseInt(line.substring(90));
			
		if(UINT16(PC) != pc || UINT8(A) != a || UINT8(X) != x || UINT8(Y) != y || UINT8(P) != p || cyc != cycleTotal + 6)		
		{			
			System.out.println(String.format("autoNestest(): mismatch at LINE %d", lineNumber));
			//System.out.println(String.format("autoNestest(): Error(s): %s", err));
			//System.out.println(String.format("\tpc: %02X  OPCODE: %01X  OPERAND(S): %01X %01X     a:%01X x:%01X y:%01X p:%01X sp:%01X cyc:%d", UINT16(PC), IR, _read8rom((short)(UINT16(PC)+1)), _read8rom((short)(UINT16(PC)+2)), a, x, y, p, sp, cyc));
			System.out.println(String.format("\tpc: %02X  OPCODE: %01X  OPERAND(S): %01X %01X     a:%01X x:%01X y:%01X p:%01X sp:%01X cyc:%d", UINT16(PC), IR, 0, 0, a, x, y, p, sp, cyc));
			printNestestLog();
			System.exit(0);
		}
	}	
	
	public void CYCLE()
	{	
		if(GuiController.pause)
		{	
			//only update memory space snapshot upon request, because it is very performance heavy
			if(DebugPool.updateMemorySpace)
			{
				updateDebugPoolMemorySpaceSnapshot();
				DebugPool.updateMemorySpace = false;
			}
			
			if(GuiController.step)
			{
				GuiController.step = false;
				stepping = true;
			}
			else
			{
				if(!stepping) return;
			}
		}
		
		getOrPut = !getOrPut;
		if(OAM_DMA)
		{
			if(alignDMA)
			{
				//by this point, the CPU has already successfully halted
				
				if(getOrPut == DMA_GET)	//only start DMA transfer on GET cycle
				{
					if(oamDebug) System.out.println(String.format("[*]Alignment complete: (cycle %d, [%d])", cycle, cycleTotal));
					alignDMA = false;
					cDMA = PPU.OAMADDR;	//set DMA counter to current OAM write address
				}
				else if(getOrPut == DMA_PUT)
				{
					;	//wait for GET cycle
				}				
			}
			else
			{
				OAM_DMA();
				//cycle++;	//not sure if DMA cycles should also increment inctrusction cycle, so disable it for now
				cycleTotal++;				
			}
			return;
		}
			
		//if(debug) System.out.println(String.format("\t\tCPU.CYCLE(): cycle %d, NMI=%b", cycle, NMI));	
		/*if(UINT16(PC) == 0xC5E5)
		{
			A = 0x20;
		}*/
		if(cycle == 1) FETCH();		
		if(IRQ)
		{				
			IRQ();
		}
		else if(NMI)
		{				
			NMI();
		}
		else
		{
			//if(cycle == 1) FETCH();
			DECODE_AND_EXECUTE();	
		}			
		
		if(FINISH)
		{		
			if(stepping)
			{
				stepping = false;				
			}
			FINISH = false;

			if(NMI)
			{
				NMI = false;
				PPU.NMI = false;
			}
			if(PPU.NMI)
			{
				//System.out.println("NMI PENDING!");
				if(!NMI) NMI = true;
				//PPU.NMI = false;
			}
			
			return;
		}		
		cycle++;	//increment instruction cycle
		cycleTotal++;
	}
}
