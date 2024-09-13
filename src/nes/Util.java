package nes;

public class Util {
	public static int BIT(byte b, int t)
	{
		int bit = t % 8;
		return (((UINT8(b) & (1 << bit)) >>> bit) & 1);
	}
	
	public static byte BIT(byte b, int t, int n)
	{
		b = (byte) (0x00 | (UINT8(b) & (0xFF ^ (1 << t))));	//zero the bit
		b = (byte) (UINT8(b) | ((n & 1) << t));	//set to either 0 or 1
		return b;
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
}
