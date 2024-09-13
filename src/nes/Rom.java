package nes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Rom 
{
	public String path;
	
	public static final int KB = 0x400;
	public static final int PRG_UNIT_SIZE = 16 * KB;
	public static final int CHR_UNIT_SIZE = 8 * KB;
	public static final int HEADER_SIZE = 16;	
	public static final int PRG_BANKS = 2;
	
	public static final int CHR_RAM = 0;
	
	public byte[] buffer;	/*holds entire file*/
	public byte[] BUFFER;	/*holds ROM data*/
	
	public byte[] header = new byte[HEADER_SIZE];
	public byte[] PRG_ROM = new byte[PRG_UNIT_SIZE * 2];	//holds entire PRG-ROM data
	public byte[] CHR_ROM = new byte[CHR_UNIT_SIZE];	//holds entire CHR-ROM data
	
	/*Hold currently loaded ROM data*/
	public byte[] PRG_ROM_LOADED = new byte[PRG_UNIT_SIZE * 2];	//2 banks of PRG-ROM data (32 KB)
	
	public int file_size;
	public int PRG_units;
	public int CHR_units;	
	public int PRG_size;
	public int CHR_size;
	
	public int mirror;
	
	Rom(String p)
	{				
		path = p;
		init();
		updateDebugPool();
	}
	
	private void updateDebugPool()
	{	
		DebugPool.ROM = BUFFER.clone();
		DebugPool.PRG_size = PRG_size;
	}
	
	private void init()
	{
		try
		{
			/*Load entire file into buffer*/
			buffer = Files.readAllBytes(Paths.get(path));
			file_size = buffer.length;
			System.out.println("ROM size: " + buffer.length + " bytes");
			
			//System.out.println(String.format("\tRom(): BUFFER SIZE: %d", (buffer.length - HEADER_SIZE)));
			
			BUFFER = new byte[buffer.length - HEADER_SIZE];
			for(int i = 0; i < BUFFER.length; i++) BUFFER[i] = buffer[HEADER_SIZE + i];
			
			/*Load iNES Header*/
			for(int i = 0; i < HEADER_SIZE; i++) header[i] = buffer[i];
			
			mirror = (byte)(header[6] & 1);
			System.out.println("NAMETABLE MIRRORING: " + ((mirror == 0) ? "HORIZONTAL" : "VERTICAL or OTHER"));
			
			/*Load PRG ROM*/
			PRG_units = header[4];
			System.out.println("PRG UNITS: " + PRG_units);
			//PRG_size = (PRG_units * PRG_UNIT_SIZE) % (0x8000 + 1);	//make sure  Cpu._read8rom() doesn't cause an index out of bounds 	
			PRG_size = (PRG_units * PRG_UNIT_SIZE);
			System.out.println(String.format("[!]DIAGNOSTICS: PRG_ROM[] = %d bytes, buffer[] = %d bytes", PRG_ROM.length, buffer.length));
			if(PRG_units == 1)	//if there is only one PRG-ROM bank, then mirror the rest
			{
				for(int j = 0; j < 2; j++)
				{
					for(int i = 0; i < PRG_UNIT_SIZE; i++)
					{
						PRG_ROM[(j * PRG_UNIT_SIZE) + i] = buffer[HEADER_SIZE + i];
					}
				}				
			}
			else	//two or more banks
			{
				for(int i = 0; i < (PRG_UNIT_SIZE * 2); i++)
				{
					PRG_ROM[i] = buffer[HEADER_SIZE + i];
				}
			}
			System.out.println("PRG-ROM size: " + (PRG_size / KB) + " KB (" + PRG_size + ")");
			
			/*Load CHR ROM*/
			CHR_units = header[5];
			System.out.println("CHR UNITS: " + ((CHR_units == 0) ? "CHR-RAM" : CHR_units));
			if(CHR_units > 0)
			{
				CHR_size = CHR_units * CHR_UNIT_SIZE;
				CHR_ROM = new byte[CHR_size];
				for(int i = 0; i < CHR_UNIT_SIZE; i++)
				{				
					CHR_ROM[i] = buffer[HEADER_SIZE + (PRG_units * PRG_UNIT_SIZE) + i];
				}
				System.out.println("CHR-ROM size: " + (CHR_size / KB) + " KB (" + CHR_size + ")");
			}
			
		}		
		catch(IOException e)	//error reading file
		{
			e.printStackTrace();
		} 
	}
}
