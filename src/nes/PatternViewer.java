package nes;

import java.awt.*;

import javax.swing.JPanel;

public class PatternViewer extends JPanel{
	
	private final int PIXEL_SIZE = 2;
	private final int WIDTH  = 128 * PIXEL_SIZE;
	private final int HEIGHT = 120 * PIXEL_SIZE;
	private final int PATTERN_TABLE_ENTRY_SIZE = 0x1000;
	private final int PATTERN_TABLE_SIZE = 0x2000;	
	private final int NUM_TILES_PER_PAGE = 16 * 16;
	private final int TILE_SIZE = 16;	//16 bytes per tile
	private final int TILE_WIDTH = 8;
	private final int TILE_HEIGHT = 8;
	
	private final int NUM_PAGES = 2;	
	
	private byte[] patternTable = new byte[PATTERN_TABLE_SIZE];
	
	private final Color[] TEMP_PALETTE = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN,};
	
	public PatternViewer()
	{
		init();
	}
	
	private void init()
	{
		setPreferredSize(new Dimension(WIDTH*2, HEIGHT));		
		//setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		setBackground(Color.BLACK);
		
	}
	
	public void update()
	{			
		for(int i = 0;i < PATTERN_TABLE_SIZE; i++)
		{
			patternTable[i] = DebugPool.patternTable[i];
		}
	}
	
	private byte getTileByteLeft(int page, int tile, int row)
	{
		return patternTable[(page * PATTERN_TABLE_ENTRY_SIZE) + (tile * TILE_SIZE) + row + 8];
	}
	
	private byte getTileByteRight(int page, int tile, int row)
	{
		return patternTable[(page * PATTERN_TABLE_ENTRY_SIZE) + (tile * TILE_SIZE) + row];
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		int pixel;		//color index into palette
		byte tileByteLeft;
		byte tileByteRight;
		
		int x = 0;
		int y = 0;
		
		for(int page = 0; page < NUM_PAGES; page++)
		{
			x = (page * WIDTH);
			y = 0;			
			for(int tile = 0; tile < NUM_TILES_PER_PAGE; tile++)
			{			
				if(tile % 16 == 0)
				{
					x = (page * WIDTH);
					if(tile != 0) y += (PIXEL_SIZE * TILE_HEIGHT);	//go down a row except first row
				}
				else
				{
					x += (PIXEL_SIZE * TILE_WIDTH);
				}
				
				for(int row = 0; row < TILE_HEIGHT; row++)
				{
					tileByteLeft = getTileByteLeft(page, tile, row);
					tileByteRight = getTileByteRight(page, tile, row);												
					
					for(int col = 0; col < TILE_WIDTH; col++)
					{
						pixel = Util.BIT(tileByteLeft, 7 - col) << 1;
						pixel = (pixel & 0b10) | Util.BIT(tileByteRight, 7 - col); 
						
						g2.setColor(TEMP_PALETTE[pixel]);															
						
						g2.drawRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
						g2.fillRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
						
						x += PIXEL_SIZE;
					}
					
					x -= (PIXEL_SIZE * TILE_WIDTH);
					
					y += PIXEL_SIZE;
				}
				
				y -= (PIXEL_SIZE * TILE_HEIGHT);
			}			
		}
	}

}
