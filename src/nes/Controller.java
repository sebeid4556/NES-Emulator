package nes;

public class Controller {
	
	public final int BUTTON_A 		= 7;
	public final int BUTTON_B 		= 6;
	public final int BUTTON_SELECT 	= 5;
	public final int BUTTON_START 	= 4;
	public final int BUTTON_UP 		= 3;
	public final int BUTTON_DOWN 	= 2;
	public final int BUTTON_LEFT 	= 1;
	public final int BUTTON_RIGHT 	= 0;
	
	private byte strobe;
	private byte shift;
	
	public Controller()
	{
		strobe = 0;
		shift = 0;		
	}
	
	public byte getStrobe()
	{
		return (byte) (strobe & 1);
	}
	
	public void writeStrobe(byte b)
	{
		strobe = (byte) (b & 1);
	}
	
	public byte getNextButton()
	{
		byte msb = (byte) (Util.UINT8(shift) >> 7);
		shift <<= 1;
		return msb;
		/*if((Util.UINT8(strobe) & 1) == 1)
		{
			return (byte) ((Util.UINT8(shift) & BUTTON_A) >>> 7);
		}
		else if((Util.UINT8(strobe) & 0) == 0)
		{
			byte bit = (byte)(Util.UINT8(shift) & 1);
			shift >>>= 1;
			return bit;
		}
		else
		{
			System.out.println("Controller.getNextButton(): FATAL ERROR: code should not reach here.");
			System.exit(0);
			return 0;
		}*/
	}
	
	public void captureInput()
	{
		shift = GuiController.buttons;
	}
	
	public void updateStates()
	{	
		/*System.out.println(String.format("buttons.size() = %d", GuiController.buttons.size()));
		for(int i : GuiController.buttons)
		{			
			Util.BIT(shift, i, 1);
		}
		GuiController.buttons.clear();*/		
		if(Util.UINT8(strobe) == 1)
		{
			for(int i = 0; i < 8; i++)
			{
				shift = Util.BIT(shift, i, Util.BIT(GuiController.buttons, i));
			}
		}
		//shift = 10;
		//System.out.println(String.format("shift: %X, buttons: %X", shift, GuiController.buttons));		
	}
	
}
