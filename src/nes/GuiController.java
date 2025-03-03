package nes;

public class GuiController {
	public static boolean pause = true;	//initially set to pause?
	public static boolean step = false;
	
	public static final int DEBUG_CPU = 0;
	public static final int DEBUG_PPU = 1;
	public static int DEBUG_SELECT = DEBUG_CPU;	//show CPU debug pane by default?
	
	public static final int BUTTON_A 		= 7;
	public static final int BUTTON_B 		= 6;
	public static final int BUTTON_SELECT 	= 5;
	public static final int BUTTON_START 	= 4;
	public static final int BUTTON_UP 		= 3;
	public static final int BUTTON_DOWN 	= 2;
	public static final int BUTTON_LEFT 	= 1;
	public static final int BUTTON_RIGHT 	= 0;
	
	public static byte buttons;
	
}
