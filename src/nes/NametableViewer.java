package nes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class NametableViewer extends JPanel{

	private final int PIXEL_SIZE = 1;	
	private final int PATTERN_TABLE_ENTRY_SIZE = 0x1000;
	private final int PATTERN_TABLE_SIZE = 0x2000;	
	private final int NUM_TILES_PER_PAGE = 16 * 16;	
	private final int TILE_SIZE = 16;	//16 bytes per tile
	private final int TILE_WIDTH = 8;
	private final int TILE_HEIGHT = 8;
	
	private final int NAMETABLE_SIZE = 0x1000;
	
	private final int NUM_TILES_ACROSS = 32;
	private final int NUM_TILES_DOWN = 32;
	private final int NUM_TILES_NAMETABLE = NUM_TILES_ACROSS * NUM_TILES_DOWN;
	
	private final int NUM_NAMETABLES_ACROSS = 2;
	private final int NUM_NAMETABLES_DOWN = 2;
	
	public final int NAMETABLE_MIRROR_HORIZONTAL = 0;
	public final int NAMETABLE_MIRROR_VERTICAL = 1;
	
	private final int NAMETABLE_WIDTH = PIXEL_SIZE * NUM_TILES_ACROSS * TILE_WIDTH;
	private final int NAMETABLE_HEIGHT = PIXEL_SIZE * NUM_TILES_DOWN * TILE_HEIGHT;
	private final int WIDTH  = NAMETABLE_WIDTH * NUM_NAMETABLES_ACROSS;
	private final int HEIGHT = NAMETABLE_HEIGHT * NUM_NAMETABLES_DOWN;
	
	private final int NUM_PAGES = 4;
	
	private byte[] nametable = new byte[NAMETABLE_SIZE];
	private byte[] patternTable = new byte[PATTERN_TABLE_SIZE];
	
	private final Color[] TEMP_PALETTE = {Color.BLACK, Color.RED, Color.YELLOW, Color.BLUE};
	
	private boolean debug = false;		
	
	public NametableViewer()
	{
		init();
	}
	
	private void init()
	{
		setPreferredSize(new Dimension(WIDTH, HEIGHT));		
		//System.out.println(String.format("\tNametableViewer.init(): WIDTH, HEIGHT (%d, %d)", WIDTH, HEIGHT));
		//setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		setBackground(Color.BLACK);		
	}
	
	public void update()
	{			
		patternTable = DebugPool.patternTable;
		nametable = DebugPool.nametable;
	}
	
	private byte getTileByteLeft(int page, int tile, int row)
	{
		return patternTable[(Util.BIT(DebugPool.PPUCTRL, DebugPool.CTRL_BG_ADDR) * PATTERN_TABLE_ENTRY_SIZE) + (tile * TILE_SIZE) + row + 8];
	}
	
	private byte getTileByteRight(int page, int tile, int row)
	{
		return patternTable[(Util.BIT(DebugPool.PPUCTRL, DebugPool.CTRL_BG_ADDR) * PATTERN_TABLE_ENTRY_SIZE) + (tile * TILE_SIZE) + row];
	}
	
	private boolean isPixelInsideCamera(int x, int y)
	{
		int tramX = 0;
		int tramY = 0;
		int tramXEnd;
		int tramYEnd;
		
		tramX += (DebugPool.NtSelect(DebugPool.t) & 0b01) * 256;
		tramX += DebugPool.coarseX(DebugPool.t) * 8;
		tramX += DebugPool.fineX;
		tramX %= 512;
		
		tramY += ((DebugPool.NtSelect(DebugPool.t) & 0b10) >>> 1) * 240;
		tramY += DebugPool.coarseY(DebugPool.t) * 8;
		tramY += DebugPool.fineY(DebugPool.t);
		tramY %= 480;
		
		tramXEnd = (tramX + 256) % 512;
		tramYEnd = (tramY + 240) % 480;
		
		if((x >= tramX) && (x < tramXEnd))
		{
			if((y >= tramY) && (y < tramYEnd))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void drawTile(Graphics2D g2, int x, int y, int tile, int page)
	{
		byte tileByteLeft;
		byte tileByteRight;
		int pixel;
		int frameBufferIndex = 0;
		int paletteIndex = 0;
		
		//System.out.println("=====================================");
		for(int row = 0; row < TILE_HEIGHT; row++)
		{
			tileByteLeft = getTileByteLeft(page, tile, row);
			tileByteRight = getTileByteRight(page, tile, row);												
			
			for(int col = 0; col < TILE_WIDTH; col++)
			{
				pixel = Util.BIT(tileByteLeft, 7 - col) << 1;
				pixel = (pixel & 0b10) | Util.BIT(tileByteRight, 7 - col); 
				
				//if(((Util.UINT16(DebugPool.v) >> 10) & 0b11) == (page & 0b11))
				//if(isPixelInsideCamera(x + col, y + row))
				if(isPixelInsideCamera(x, y))
				{
					g2.setColor(TEMP_PALETTE[pixel]);
					
					/*
					 * INSTEAD OF USING THE FRAMEBUFFER, LOOK UP THE COLOR FROM THE ATTRIBTUE TABLE
					 * OR ELSE IT WON'T WORK PROPERLY!!!!
					*/
					
					//System.out.println(String.format("(%d, %d), col: %d, row: %d", x, y, col, row));
															
					//just draws the current screen onto the highlighted arae
					frameBufferIndex = ((y * 256) + x) % 61440;
					//System.out.println(String.format("(%d, %d): %d", x + col, y + row, frameBufferIndex));
					paletteIndex = DebugPool.frameBuffer[frameBufferIndex];
					//g2.setColor(Screen.PALETTE[paletteIndex]);
					
				}
				else
				{
					g2.setColor(Screen.PALETTE[0x10 * pixel]);
				}
				
				g2.drawRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
				g2.fillRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
				
				x += PIXEL_SIZE;
			}
			
			x -= (PIXEL_SIZE * TILE_WIDTH);
			
			y += PIXEL_SIZE;
		}
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		int x = 0;
		int y = 0;
		int tile = 0;
		int page = 0;
		int ntSelect = 0;
		
		for(int down = 0; down < NUM_NAMETABLES_DOWN; down++)
		{
			for(int across = 0; across < NUM_NAMETABLES_ACROSS; across++)
			{				
				page = (down * NUM_NAMETABLES_ACROSS) + across;
				if(debug) System.out.println("==========================================================[" + page + "]====================================================");
				for(int tileDown = 0; tileDown < NUM_TILES_DOWN; tileDown++)
				{
					if(debug) System.out.print("|");
					for(int tileAcross = 0; tileAcross < NUM_TILES_ACROSS; tileAcross++)
					{
						if(DebugPool.nametableMirror == NAMETABLE_MIRROR_HORIZONTAL)
						{
							ntSelect = down;
						}
						else if(DebugPool.nametableMirror == NAMETABLE_MIRROR_VERTICAL)
						{
							ntSelect = across;
						}
						//tile = nametable[(tileDown * NUM_TILES_DOWN) + tileAcross];
						tile = Util.UINT8(nametable[(ntSelect * NUM_TILES_NAMETABLE) + (tileDown * NUM_TILES_ACROSS) + tileAcross]);
						//System.out.println(String.format("\tpaint(%d, %d): tile ($%02X)", down, across, tile));
						if(debug) System.out.print(String.format(" %02X ", tile));
						
						//if(tile == 0x20) tile = 0x30;
							
						drawTile(g2, x, y, tile, page);
						
						x += (TILE_WIDTH * PIXEL_SIZE);	//next tile
					}
					if(debug) System.out.println("|");
					x -= (TILE_WIDTH * PIXEL_SIZE) * NUM_TILES_ACROSS;	//move x back to start
					y += (TILE_HEIGHT * PIXEL_SIZE);	//next row
				}
				x += NAMETABLE_WIDTH;	//next nametable
				x %= WIDTH;
				y -= NAMETABLE_HEIGHT;
			}
			y += NAMETABLE_HEIGHT;	//go down a nametable
			y %= HEIGHT;
		}		
		//System.exit(0);
	}	
}
