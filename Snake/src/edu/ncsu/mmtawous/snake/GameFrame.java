package edu.ncsu.mmtawous.snake;

import java.awt.Taskbar;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameFrame extends JFrame implements KeyListener{
	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	static Snake snake = Snake.getInstance();
	ImageIcon icon = new ImageIcon("assets/snake_icon.png");
	static int direction = 0;

	public GameFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		this.setTitle("Snake Game");
		this.setIconImage(icon.getImage());
		final Taskbar taskbar = Taskbar.getTaskbar();

		try {
			// set icon for MacOS (and other systems which do support this method)
			taskbar.setIconImage(icon.getImage());
		} catch (final UnsupportedOperationException e) {
			System.out.println("The os does not support: 'taskbar.setIconImage'");
		} catch (final SecurityException e) {
			System.out.println("There was a security exception for: 'taskbar.setIconImage'");
		}

		this.add(snake);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		move.start();
	}

	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.application.name", "Snake Game");

		SwingUtilities.invokeLater(new Runnable() {

	        @Override
	        public void run() {
	            new GameFrame();          
	        }
	    });

	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_LEFT && !snake.gameOver) {
			if (snake.newGame) {
				snake.newGame = false;
			}
			if (direction != KeyEvent.VK_RIGHT) {
				direction = KeyEvent.VK_LEFT;
			}
		}

		if (event.getKeyCode() == KeyEvent.VK_RIGHT && !snake.gameOver && !snake.newGame) {
			if (direction != KeyEvent.VK_LEFT) {
				direction = KeyEvent.VK_RIGHT;
			}
		}

		if (event.getKeyCode() == KeyEvent.VK_UP && !snake.gameOver) {
			if (snake.newGame) {
				snake.newGame = false;
			}
			if (direction != KeyEvent.VK_DOWN) {
				direction = KeyEvent.VK_UP;
			}
		}

		if (event.getKeyCode() == KeyEvent.VK_DOWN && !snake.gameOver) {
			if (snake.newGame) {
				snake.newGame = false;
			}
			if (direction != KeyEvent.VK_UP) {
				direction = KeyEvent.VK_DOWN;
			}
		}

		if (event.getKeyCode() == KeyEvent.VK_SPACE && snake.gameOver) {
			snake.newGame();
		}
		
	}
	
	public static Timer move = new Timer(5, e -> {
		if (!snake.isInBound() || snake.checkCollision()) {
			direction = 0;
			snake.gameOver();
			snake.repaint();
		} else {
			if (direction == KeyEvent.VK_LEFT && snake.arrived) {
				snake.checkApple();
				snake.moveLeft();
			} else if (direction == KeyEvent.VK_RIGHT && snake.arrived) {
				snake.checkApple();
				snake.moveRight();
			} else if (direction == KeyEvent.VK_UP && snake.arrived) {
				snake.checkApple();
				snake.moveUp();
			} else if (direction == KeyEvent.VK_DOWN && snake.arrived) {
				snake.checkApple();
				snake.moveDown();
			}
		}
	});
	
	@Override
	public void keyTyped(KeyEvent e) {	
	}

	@Override
	public void keyReleased(KeyEvent e) {	
	}

}