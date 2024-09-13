package nes;

import java.awt.*;

import javax.swing.JPanel;

public class PpuViewer extends JPanel{
	
	private final int NUM_CYCLES = 341;
	private final int NUM_SCANLINES = 262;		
	
	private final int PIXEL_SIZE = 2;
	private final int GRID_WEIGHT = 0;
	
	private boolean grid = true;
	
	private int MAIN_WIDTH = NUM_CYCLES * 2;
	private int MAIN_HEIGHT = NUM_CYCLES * 2;
	
	private int CYCLE_WIDTH = (PIXEL_SIZE + GRID_WEIGHT) * NUM_CYCLES;
	private int CYCLE_HEIGHT = (PIXEL_SIZE + GRID_WEIGHT) * NUM_SCANLINES;
	
	private Color GRID_COLOR = Color.GRAY;
	
	private JPanel mainPane;
	private JPanel cycleViewer;
	
	public PpuViewer()
	{
		init();
	}
	
	private void init()
	{
		/*mainPane = new JPanel();
		mainPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		mainPane.setBackground(Color.RED);*/
		
		int w = (NUM_CYCLES * PIXEL_SIZE) + (GRID_WEIGHT * NUM_CYCLES);
		int h = (NUM_SCANLINES * PIXEL_SIZE) + (GRID_WEIGHT * NUM_SCANLINES);	
		w = CYCLE_WIDTH;
		h = CYCLE_HEIGHT;
		
		setPreferredSize(new Dimension(w, h));
		setBackground(Color.RED);
		
		cycleViewer = new JPanel();				
		cycleViewer.setPreferredSize(new Dimension(w, h));
		cycleViewer.setBackground(GRID_COLOR);
		
		//mainPane.add(cycleViewer);		
		add(cycleViewer);
	}
	
	public void update()
	{
		repaint();
	}
	
	private void drawGrid(Graphics2D g2)
	{
		int w = (NUM_CYCLES * PIXEL_SIZE) + (GRID_WEIGHT * NUM_CYCLES);
		int h = (NUM_SCANLINES * PIXEL_SIZE) + (GRID_WEIGHT * NUM_SCANLINES);	

		g2.setColor(GRID_COLOR);
		g2.drawRect(0, 0, w, h);
		g2.fillRect(0, 0, w, h);
	}
	
	public void paint(Graphics g)
	{
		//System.out.println(String.format("PpuViewer.paint(%d, %d)", DebugPool.dot, DebugPool.scanline));
		Graphics2D g2 = (Graphics2D) g;
		
		int x = GRID_WEIGHT;
		int y = GRID_WEIGHT;
		
		drawGrid(g2);
		for(int scanline = 0; scanline < NUM_SCANLINES; scanline++)
		{
			for(int dot = 0; dot < NUM_CYCLES; dot++)
			{
				//System.out.println(String.format("PpuViewer.paint(%d, %d)", dot, scanline));
				//System.out.println(String.format("PpuViewer.paint(%d, %d)", x, y));
				//g2.setColor((dot == DebugPool.dot && scanline == DebugPool.scanline) ? Color.RED : Color.WHITE);
				//if(dot % 16 == 0 || scanline % 16 == 0) g2.setColor(Color.RED);
				if(dot < 256 && scanline < 240) g2.setColor(Color.BLACK);
				else g2.setColor(Color.GRAY);
				if(dot == DebugPool.dot && scanline == DebugPool.scanline) g2.setColor(Color.GREEN);
				g2.drawRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
				g2.fillRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
				x += PIXEL_SIZE + GRID_WEIGHT;
			}
			x = GRID_WEIGHT;
			y += PIXEL_SIZE + GRID_WEIGHT;
		}
	}
}
