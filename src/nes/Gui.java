package nes;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Gui {
	
	private JFrame f;
	private JPanel dbgPane;
	
	private Screen screen;
	private PatternViewer patternViewer;
	private NametableViewer nametableViewer;
	private CpuViewer CPUViewer;
	private PpuViewer PPUViewer;
	
	public Font fontNES;
	
	private float FONT_SIZE = 17f;
	
	private ArrayList<JComponent> updateQueue = new ArrayList<JComponent>();
	
	public Gui()
	{
		initAll();
	}
	
	private void initAll()
	{
		initResources();
		
		initDebugPane();
		initJFrame();
	}
	
	private void initFont()
	{
		try
		{
			fontNES = Font.createFont(Font.TRUETYPE_FONT, new File("resource/font/nes.ttf")).deriveFont(FONT_SIZE);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(fontNES);			
		}catch(IOException e)
		{
			e.printStackTrace();
		}catch(FontFormatException e)
		{
			e.printStackTrace();
		}
	}
	
	private void initResources()
	{
		initFont();
	}
	
	private void initScreen()
	{
		screen = new Screen();
		screen.setFPS(60);
		//screen.setFPS(screen.FPS_UNLIMITED);
	}
	
	private void initDebugPane()
	{
		dbgPane = new JPanel();
		dbgPane.setPreferredSize(new Dimension(256*2, 240*3));
		dbgPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		dbgPane.setBackground(Color.BLUE);
	}
	
	private void initPatternViewer()
	{
		patternViewer = new PatternViewer();
	}
	
	private void initNametableViewer()
	{
		nametableViewer = new NametableViewer();
	}
	
	private void initCpuViewer()
	{
		CPUViewer = new CpuViewer(fontNES);
	}
	
	private void initPpuViewer()
	{
		PPUViewer = new PpuViewer();
	}
	
	private void initJFrame()
	{
		f = new JFrame();
		
		f.setTitle("NES Emulator");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		f.setResizable(false);
		f.setUndecorated(false);
		f.setLayout(new BorderLayout());
		//f.setLocationRelativeTo(null);
		f.setLocation(0, 50);
		
		initScreen();
		initPatternViewer();
		initNametableViewer();	
		initCpuViewer();
		initPpuViewer();
				
		//f.getContentPane().add(CPUViewer, BorderLayout.EAST);
		f.getContentPane().add(dbgPane, BorderLayout.CENTER);
		f.getContentPane().add(screen, BorderLayout.WEST);
		//f.getContentPane().add(PPUViewer, BorderLayout.WEST);
		
		dbgPane.add(patternViewer);
		dbgPane.add(nametableViewer);
		f.pack();
		f.setVisible(true);
		
		f.setFocusable(true);
		f.requestFocus();
		f.requestFocusInWindow();
		f.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				;
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
				//GuiController.buttons.clear();		
				
				switch(e.getKeyCode())
				{
					case KeyEvent.VK_L:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_A, 1);
						break;
					case KeyEvent.VK_K:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_B, 1);
						break;
					case KeyEvent.VK_SHIFT:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_SELECT, 1);
						break;
					case KeyEvent.VK_ENTER:						
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_START, 1);
						//System.out.println(String.format("buttons: %X", GuiController.buttons));
						break;
					case KeyEvent.VK_W:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_UP, 1);
						break;
					case KeyEvent.VK_S:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_DOWN, 1);
						break;
					case KeyEvent.VK_A:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_LEFT, 1);
						break;
					case KeyEvent.VK_D:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_RIGHT, 1);						
						break;
					
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				switch(e.getKeyCode())
				{
					case KeyEvent.VK_L:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_A, 0);
						break;
					case KeyEvent.VK_K:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_B, 0);
						break;
					case KeyEvent.VK_SHIFT:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_SELECT, 0);
						break;
					case KeyEvent.VK_ENTER:						
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_START, 0);
						//System.out.println(String.format("buttons: %X", GuiController.buttons));
						break;
					case KeyEvent.VK_W:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_UP, 0);
						break;
					case KeyEvent.VK_S:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_DOWN, 0);
						break;
					case KeyEvent.VK_A:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_LEFT, 0);
						break;
					case KeyEvent.VK_D:
						GuiController.buttons = Util.BIT(GuiController.buttons, GuiController.BUTTON_RIGHT, 0);
						break;
					case KeyEvent.VK_SPACE:
						GuiController.pause = !GuiController.pause;
						CPUViewer.setPauseButtonState();
						break;
					
				}
			}
		});
				
	}
	
	public void addToUpdateQueue(JComponent jc)
	{
		updateQueue.add(jc);
	}
	
	public void clearUpdateQueue()
	{
		updateQueue.clear();
	}
	
	public void updateQueue()
	{
		for(int i = 0; i < updateQueue.size(); i++)
		{
			updateQueue.get(i).update();
		}
	}
	
	public void updateScreen()
	{
		screen.update();
		f.repaint();
	}
	
	public void updateDebug()
	{
		patternViewer.update();
		nametableViewer.update();		
		//f.repaint();
	}
	
	public void updateCpuViewer()
	{
		CPUViewer.update();
	}
	
	public void updatePpuViewer()
	{
		//PPUViewer.update();
	}
	
	public void updateAll()
	{
		//screen.update();
		patternViewer.update();
		nametableViewer.update();
		CPUViewer.update();
		f.repaint();
	}
}
