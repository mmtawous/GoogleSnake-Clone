package edu.ncsu.mmtawous.snake;

import java.awt.Taskbar;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameFrame extends JFrame implements KeyListener {
	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	static Snake snake = Snake.getInstance();
	ImageIcon icon = new ImageIcon("assets/snake_icon.png");
	private static Queue<KeyEvent> keyStrokes;
	static int direction = 0;
	private static Clip upAudio, rightAudio, leftAudio, downAudio, endAudio, eatAudio;

	public GameFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		keyStrokes = new LinkedList<KeyEvent>();
		this.setTitle("Snake Game");
		this.setIconImage(icon.getImage());
		final Taskbar taskbar = Taskbar.getTaskbar();

		try {
			// set icon for MacOS (and other systems which do support this method)
			taskbar.setIconImage(icon.getImage());

			upAudio = AudioSystem.getClip();
			rightAudio = AudioSystem.getClip();
			leftAudio = AudioSystem.getClip();
			downAudio = AudioSystem.getClip();
			endAudio = AudioSystem.getClip();
			eatAudio = AudioSystem.getClip();

			upAudio.open(AudioSystem.getAudioInputStream(new File("assets/audio/up.wav")));
			rightAudio.open(AudioSystem.getAudioInputStream(new File("assets/audio/right.wav")));
			leftAudio.open(AudioSystem.getAudioInputStream(new File("assets/audio/left.wav")));
			downAudio.open(AudioSystem.getAudioInputStream(new File("assets/audio/down.wav")));
			endAudio.open(AudioSystem.getAudioInputStream(new File("assets/audio/end.wav")));
			eatAudio.open(AudioSystem.getAudioInputStream(new File("assets/audio/eat.wav")));

		} catch (final UnsupportedOperationException e) {
			System.out.println("The os does not support: 'taskbar.setIconImage'");
		} catch (final SecurityException e) {
			System.out.println("There was a security exception for: 'taskbar.setIconImage'");
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
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
	public void keyPressed(KeyEvent event) { // process keystroke with queue
		keyStrokes.add(event);

	}

	public static Timer move = new Timer(2, e -> {
		if (!snake.isInBound() || snake.checkCollision()) {
			direction = 0;
			snake.gameOver();
			snake.repaint();
			playAudio(endAudio);
		} else if (snake.getHead().x % 30 == 0 && snake.getHead().y % 30 == 0) {
			processKeyStroke();
			if (snake.checkApple()) {
				playAudio(eatAudio);
			}
			if (direction == KeyEvent.VK_LEFT) {
				snake.moveLeft();
			} else if (direction == KeyEvent.VK_RIGHT) {
				snake.moveRight();
			} else if (direction == KeyEvent.VK_UP) {
				snake.moveUp();
			} else if (direction == KeyEvent.VK_DOWN) {
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

	public static void processKeyStroke() {
		if (!keyStrokes.isEmpty() && snake.completedMovement()) {
			snake.setCompletedMovement(false);
			KeyEvent event = keyStrokes.remove();

			if (event.getKeyCode() == KeyEvent.VK_LEFT && !snake.gameOver) {
				if (snake.newGame) {
					snake.newGame = false;
				}

				if (direction != KeyEvent.VK_RIGHT) {
					direction = KeyEvent.VK_LEFT;
					playAudio(leftAudio);
				}
			}

			if (event.getKeyCode() == KeyEvent.VK_RIGHT && !snake.gameOver && !snake.newGame) {
				if (direction != KeyEvent.VK_LEFT) {
					direction = KeyEvent.VK_RIGHT;
					playAudio(rightAudio);
				}
			}

			if (event.getKeyCode() == KeyEvent.VK_UP && !snake.gameOver) {
				if (snake.newGame) {
					snake.newGame = false;
				}
				if (direction != KeyEvent.VK_DOWN) {
					direction = KeyEvent.VK_UP;
					playAudio(upAudio);
				}
			}

			if (event.getKeyCode() == KeyEvent.VK_DOWN && !snake.gameOver) {
				if (snake.newGame) {
					snake.newGame = false;
				}
				if (direction != KeyEvent.VK_UP) {
					direction = KeyEvent.VK_DOWN;
					playAudio(downAudio);
				}
			}

			if (event.getKeyCode() == KeyEvent.VK_SPACE && snake.gameOver) {
				snake.newGame();
			}
		}
	}

	private static void playAudio(Clip audio) {
		if (audio.isRunning()) {
			audio.stop();
		}

		audio.setFramePosition(0);
		audio.start();
	}

}
