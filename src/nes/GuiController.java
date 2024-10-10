package nes;

import java.util.ArrayList;

//the controller
public class GuiController {
	public static boolean pause = false;
	public static boolean step = false;	
	
	public static final int BUTTON_A 		= 7;
	public static final int BUTTON_B 		= 6;
	public static final int BUTTON_SELECT 	= 5;
	public static final int BUTTON_START 	= 4;
	public static final int BUTTON_UP 		= 3;
	public static final int BUTTON_DOWN 	= 2;
	public static final int BUTTON_LEFT 	= 1;
	public static final int BUTTON_RIGHT 	= 0;
	
	//public static ArrayList<Integer> buttons = new ArrayList<Integer>();
	public static byte buttons;
	
	/*public void LDA_IND_Y()
	{
		switch(cycle)	//correct implementation of IND_Y read
		{			
			case 2: 				
				address = (short)UINT8(BYTE(PC));		
				PC++;
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
					_LDA();
					FINISH();
					break;
				}	
				break;
			case 6:
				high += 0x01;
				effective = COMBINE16(low, high);		
				operand = BYTE(effective);
				_LDA();
				FINISH();
				break;
		}
	}*/
	
}
