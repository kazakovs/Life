package r47717.life.field;

import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class LifeField extends JPanel implements Runnable {

	private Thread thread;
	private boolean stopped = false;

	private final int BORDER = 15;

	private final int WIDTH = 700;
	private final int COLS = 70;

	private final int HEIGHT = 500;
	private final int ROWS = 50;

	private final int CELLS = 700;

	private final int DELAY = 500;


	private char[][] field = new char[COLS][ROWS];

	public LifeField() {
		super();
		fillRandom();
		thread = new Thread(this);
		start();
	}

	private void fillRandom() {
		Random rand = new Random();

		for(int i = 0; i < COLS; i++)
			for(int j = 0; j < ROWS; j++)
				field[i][j] = 0;

		for(int k = 0; k < CELLS; k++) {
			int i = rand.nextInt(COLS);
			int j = rand.nextInt(ROWS);
			field[i][j] = 1;
		}

	}

	///////////////////////////////////////////////////////////////////////////////////

	private void nextDay() {
		char[][] buf = new char[COLS][ROWS];

		// create new generation in buf
		//
		for(int i = 0; i < COLS; i++)
			for(int j = 0; j < ROWS; j++) {
				switch(getNeighbors(i, j)){
					case 2:
						buf[i][j]=field[i][j];
						break;
					case 3:
						buf[i][j]=1;
						break;
					default:
						buf[i][j]=0;
				}
			}
		this.field = buf;
	}

	private int normalizeValue(int value, int max){
		if(value<0)
			return max-1;
		if(value>=max)
			return 0;
		return value;
	}

	private int getNeighbors(int i, int j) {

		int result = 0;
		for(int h = -1; h<=1; h++){
			int x=normalizeValue(i+h, COLS);
			for(int v=-1; v<=1;v++){
				int y=normalizeValue(j+v, ROWS);
				if((h!=0||v!=0)&&(field[x][y]==1)){
					result++;
				}
			}
		}

		return result;

	}


	///////////////////////////////////////////////////////////////////////////////////

	private void paintGrid(Graphics g) {
		g.setColor(Color.GRAY);

		int x1, x2;
		int y1, y2;
		int dx = WIDTH/COLS;
		int dy = HEIGHT/ROWS;

		// draw horizontal lines
		x1 = y1 = y2 = BORDER;
		x2 = WIDTH + BORDER;
		for(int i = 0; i <= ROWS; i++) {
			g.drawLine(x1, y1, x2, y2);
			y1 = y2 = y1 + dy;
		}

		// draw vertical lines
		x1 = x2 = y1 = BORDER;
		y2 = HEIGHT + BORDER;
		for(int i = 0; i <= COLS; i++) {
			g.drawLine(x1, y1, x2, y2);
			x1 = x2 = x1 + dx;
		}
	}

	private void paintCell(Graphics g, int i, int j) {
		int dx = WIDTH/COLS;
		int dy = HEIGHT/ROWS;
		int x = i * dx + BORDER;
		int y = j * dy + BORDER;

		if(field[i][j] == 1)
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.WHITE);
		g.fillRect(x + 1, y + 1, dx - 2, dy - 2);
	}

	private void paintCells(Graphics g) {
		for(int i = 0; i < COLS; i++)
			for(int j = 0; j < ROWS; j++)
				paintCell(g, i, j);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintGrid(g);
		paintCells(g);
	}


	///////////////////////////////////////////////////////////////////////////////////

	@Override
	public void run() {
		while(!stopped) {
			try {
				thread.sleep(DELAY);
			} catch (InterruptedException e) {
				stopped = true;
			}
			nextDay();
			repaint();
		}
	}

	public void start() {
		stopped = false;
		thread.start();
	}

	public void stop() {
		stopped = true;
	}

}
