package nes;

import java.awt.*;
import java.util.Random;

import javax.swing.JPanel;

public class Screen extends JPanel{		
	
	public static final int WIDTH  = 256;
	public static final int HEIGHT = 240;
	
	public static final int BUFFER_SIZE = WIDTH * HEIGHT;
	public static int[] frameBuffer = new int[BUFFER_SIZE];
	
	public final int FPS_UNLIMITED = -1;
	private int FPS = 60;
	private long INTERVAL = 1000 / FPS; 
	private long before = System.currentTimeMillis();
	private long now = 0;
	
	private int currentFrame = 0;
	private long timeFrameStart = System.currentTimeMillis();
	private int count = 0;
	
	public static final int MIN_PIXEL_SIZE = 3;
	public static final int MAX_PIXEL_SIZE = 20;
	public static final int DEFAULT_PIXEL_SIZE = MIN_PIXEL_SIZE;
	public static int pixel_size = DEFAULT_PIXEL_SIZE;
	
	public static Color[] COLORS = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.WHITE};
	
	public final Color[] TEMP_PALETTE = {Color.BLACK, Color.RED, Color.YELLOW, Color.BLUE};
	
	public static final Color[] PALETTE = {
			new Color(0x666666), new Color(0x002A88), new Color(0x1412A7), new Color(0x3B00A4), new Color(0x5C007E), new Color(0x6E0040), new Color(0x6C0600), new Color(0x561D00), 
			new Color(0x333500), new Color(0x0B4800), new Color(0x005200), new Color(0x004F08), new Color(0x00404D), new Color(0x000000), new Color(0x000000), new Color(0x000000), 
			new Color(0xADADAD), new Color(0x155FD9), new Color(0x4240FF), new Color(0x7527FE), new Color(0xA01ACC), new Color(0xB71E7B), new Color(0xB53120), new Color(0x994E00), 
			new Color(0x6B6D00), new Color(0x388700), new Color(0x0C9300), new Color(0x008F32), new Color(0x007C8D), new Color(0x000000), new Color(0x000000), new Color(0x000000), 
			new Color(0xFFFEFF), new Color(0x64B0FF), new Color(0x9290FF), new Color(0xC676FF), new Color(0xF36AFF), new Color(0xFE6ECC), new Color(0xFE8170), new Color(0xEA9E22), 
			new Color(0xBCBE00), new Color(0x88D800), new Color(0x5CE430), new Color(0x45E082), new Color(0x48CDDE), new Color(0x4F4F4F), new Color(0x000000), new Color(0x000000), 
			new Color(0xFFFEFF), new Color(0xC0DFFF), new Color(0xD3D2FF), new Color(0xE8C8FF), new Color(0xFBC2FF), new Color(0xFEC4EA), new Color(0xFECCC5), new Color(0xF7D8A5), 
			new Color(0xE4E594), new Color(0xCFEF96), new Color(0xBDF4AB), new Color(0xB3F3CC), new Color(0xB5EBF2), new Color(0xB8B8B8), new Color(0x000000), new Color(0x000000) 
			};

	
	private Random rand;
	private int r;
	private int pixel = 0;
	
	private boolean randomizeScreen = false;
	private static final boolean DEFAULT_GRID = false;
	private boolean grid = DEFAULT_GRID;
	public static final int DEFAULT_GRID_WEIGHT = 2;
	public static int GRID_WEIGHT = DEFAULT_GRID_WEIGHT;
	public int total_width;
	public int total_height;	
	public int pictureRegionWidth;
	public int pictureRegionHeight;
	public int gridWidth;
	public int gridHeight;
	
	public static Color GRID_COLOR = Color.GRAY;
	
	private int PADDING = 0;
		
	
	public Screen()
	{
		init();
	}
	
	private void init()
	{		
		
		setPreferredSize(new Dimension(256*pixel_size, 240*pixel_size));		
		//setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setBackground(Color.GRAY);		
		
		clearFrameBuffer();
		initRandom();
	}
	
	private void clearFrameBuffer()
	{
		for(int i = 0; i < BUFFER_SIZE; i++)
		{
			frameBuffer[i] = 0x1f;
		}	
	}
	
	private void initRandom()
	{
		rand = new Random();
	}
	
	public void setFPS(int f)
	{
		if(f > -1)
		{
			FPS = f;
			INTERVAL = 1000 / FPS;
			
		}
	}
		
	public void waitFrame()
	{
		if(FPS == FPS_UNLIMITED) return;		
		now = System.currentTimeMillis();
		//if(count == 0) before = now;
		if(currentFrame == 0) before = now;
		
		if(currentFrame == 0)
		{
			timeFrameStart = now;								
		}
		
		if((now - before) < INTERVAL)
		{
			
			try
			{
				Thread.sleep(INTERVAL - (now - before));				
				
			}catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		else if((now - before) >= INTERVAL)
		{
			System.out.println(String.format("waitFrame(%d): LATE by %d milliseconds!!", currentFrame, (now - before) - INTERVAL));
		}
		before = System.currentTimeMillis();
	}
	
	public void update()
	{
		waitFrame();
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		int xOffset = 0;
		int yOffset = 0;
		
		if(grid)
		{
			g2.drawRect(PADDING, PADDING, gridWidth, gridHeight);
			g2.fillRect(PADDING, PADDING, gridWidth, gridHeight);
		}
		else
		{
			g2.drawRect(PADDING, PADDING, pictureRegionWidth, pictureRegionHeight);
			g2.fillRect(PADDING, PADDING, pictureRegionWidth, pictureRegionHeight);
		}		
		
		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{												
				pixel = (y * WIDTH) + x; 
				//g2.setColor(TEMP_PALETTE[frameBuffer[pixel]]);
				g2.setColor(PALETTE[frameBuffer[pixel]]);
				
				if(randomizeScreen)
				{
					r = rand.nextInt(5);
					g2.setColor(COLORS[r]);
				}
				g2.drawRect(PADDING + pixel_size*x + xOffset, PADDING + pixel_size*y + yOffset, pixel_size, pixel_size);
				g2.fillRect(PADDING + pixel_size*x + xOffset, PADDING + pixel_size*y + yOffset, pixel_size, pixel_size);
				
				if(grid) xOffset += GRID_WEIGHT;
			}			
			if(grid)
			{
				xOffset = 0;
				yOffset += GRID_WEIGHT;
			}
		}
		currentFrame++;
	}
	
	public void render()
	{
		;
	}
}
