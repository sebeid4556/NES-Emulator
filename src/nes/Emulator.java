package nes;

import javax.swing.SwingUtilities;

public class Emulator 
{
	//CPU and PPU should synchronize every 12 master clock cycles
	private static final int MASTER_CYCLE_SYNC = 12;
	private static final int MASTER_CYCLE_STEP = 4;	//emulator will run 4 master cycles at a time
	
	//1 CPU cycle every 12 master clock cycles
	private static final int CPU_CYCLE_FREQ = 12;
	
	//3 PPU cycles every CPU cycle, or every 4 master clock cycles
	private static final int PPU_CYCLE_FREQ = 4;
	
	/*64-bit value for master clock should be big enough to not max out*/
	//note: will reverse once halfway due to lack of unsigned values
	private long masterCycle = 0;	
	
	private Bus CPU_BUS;	//cpu internal bus	
	private Bus IO_BUS;		//bus shared between cpu and ppu
	private Cpu CPU;
	private Ppu PPU;
	private Rom ROM;
	private Gui GUI;
	private Controller CONTROLLER;
	
	Emulator()
	{
		init();
	}
	
	private void init()	
	{
		/*SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				GUI = new Gui();
				GUI.update();
			}
		});*/
		GUI = new Gui();
	}
	
	public void loadROM(String p)
	{
		//TODO: implement parse ROM header beforehand for size, mapper, etc.				
		
		CONTROLLER = new Controller();
		
		CPU_BUS = new Bus();
		IO_BUS = new Bus();
		ROM = new Rom(p);					
		PPU = new Ppu(ROM);
		CPU = new Cpu(CPU_BUS, IO_BUS, PPU, ROM, CONTROLLER);
				
		
	}
	
	public void run()
	{		
		System.out.println("========START EXECUTION========");	
		
		CPU.POWER();
		
		CPU.toggleBreakpoint(false);
		CPU.setBreakpointAt(0xE322);
		
		PPU.setUpdateDebugPool(true);			
		
		while(true)
		{												
			//The CPU runs every 3 PPU cycles			
			masterCycle += MASTER_CYCLE_STEP;
			if((masterCycle % PPU_CYCLE_FREQ) == 0)
			{			
				PPU.CYCLE();
				if(masterCycle == CPU_CYCLE_FREQ)
				{				
					CPU.CYCLE();
					masterCycle = 0;
					
					//GUI.updateCpuViewer();
					//GUI.updateDebug();
				}
				//GUI.updatePpuViewer();
				if(PPU.shouldDrawFrame())
				{
					//GUI.updateAll();
					GUI.updateDebug();
					GUI.updateScreen();
				}
			}
		}
	}
}
