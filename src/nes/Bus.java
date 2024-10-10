package nes;

public class Bus {
	/*Control bus values*/
	public final int CTRL_READ = 1;
	public final int CTRL_WRITE = 0;
	
	public short ADDR;	//16-bit address bus
	public byte DATA;	//8-bit data bus
	public int CTRL;	//1-bit control bus
	
	public byte read()
	{
		return DATA;
	}
	
	public void write(short addr)
	{
		ADDR = addr;
	}
	
	public void write(byte data)
	{
		DATA = data;
	}
}
