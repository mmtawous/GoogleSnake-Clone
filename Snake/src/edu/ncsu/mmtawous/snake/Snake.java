package edu.ncsu.mmtawous.snake;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Snake extends JPanel implements ActionListener {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	public static final int SEGMENT_LENGTH = 30;
	public static Snake instance;
	public int appleX = 90;
	public int appleY = 90;
	public Point[] points;
	public ArrayList<Rectangle> rects;
	public Color headColor = new Color(19, 41, 75);
	public Color bodyColor = new Color(29, 99, 219);
	public Color borderColor = new Color(87, 138, 52, 255);
	public Color headTemp;
	public Color bodyTemp;
	public Color borderTemp;
	public Dimension segmentSize = new Dimension(30, 30);
	public int bodyParts = 6;
	public int score;
	public int highScore;
	public boolean gameOver = false;
	public boolean newGame = true;
	public boolean completedMovement;
	public Image background;
	public Image apple;
	public Image backgroundTemp;
	public Image appleTemp;
	public Image gameOver2;
	public Image gameOverApple;
	public Image highscore;
	public ImageIcon PlayAgain;
	public ImageIcon Quit;
	public Rectangle gameOver1 = null;
	public JButton playAgain = new JButton();
	public JButton quit = new JButton();

	private Snake() {
		try {
			background = ImageIO.read(new File("assets/background.png"));
			apple = ImageIO.read(new File("assets/apple.png"));
			gameOver2 = ImageIO.read(new File("assets/gameOver2.png"));
			gameOverApple = ImageIO.read(new File("assets/gameOverApple.png"));
			highscore = ImageIO.read(new File("assets/highscore.png"));
			PlayAgain = new ImageIcon("assets/playAgain.png");
			Quit = new ImageIcon("assets/quit.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setPreferredSize(new Dimension(1290, 570));
		this.setLayout(null);

		rects = new ArrayList<Rectangle>();

		rects.add(0, new Rectangle(new Point(630, 300), segmentSize));
		for (int i = 1; i < bodyParts; i++) {
			rects.add(i, new Rectangle(rects.get(i - 1).x + SEGMENT_LENGTH, rects.get(i - 1).y, SEGMENT_LENGTH,
					SEGMENT_LENGTH));
		}

		playAgain.setBounds(505, 340, 110, 62);
		playAgain.setContentAreaFilled(false);
		playAgain.setFocusPainted(false);
		playAgain.setBorderPainted(false);
		playAgain.setIcon(PlayAgain);
		playAgain.setFocusable(false);
		playAgain.addActionListener(this);
		playAgain.setVisible(false);

		quit.setBounds(620, 340, 110, 62);
		quit.setContentAreaFilled(false);
		quit.setFocusPainted(false);
		quit.setBorderPainted(false);
		quit.setIcon(Quit);
		quit.setFocusable(false);
		quit.addActionListener(this);
		quit.setVisible(false);
		this.add(playAgain);
		this.add(quit);

		movement.start();
	}

	public static Snake getInstance() {
		if (instance == null) {
			instance = new Snake();
		}

		return instance;
	}

	Timer movement = new Timer(10, e -> {
		if (points != null) {

			for (int i = 0; i < bodyParts; i++) {
				if (rects.get(i).x > points[i].x) {
					rects.get(i).x -= 3;
				} else if (rects.get(i).x < points[i].x) {
					rects.get(i).x += 3;
				}

				if (rects.get(i).y > points[i].y) {
					rects.get(i).y -= 3;
				} else if (rects.get(i).y < points[i].y) {
					rects.get(i).y += 3;
				}
			}
		}

		repaint();

		if (points != null) {
			for (int i = 0; i < bodyParts; i++) {
				if (rects.get(i).x != points[i].x || rects.get(i).y != points[i].y) {
					break;
				}
			}
		}

		setCompletedMovement(getHead().x % 30 == 0 && getHead().y % 30 == 0);

	});

	public boolean isInBound() {
		if (rects.get(0).x <= 0 || (rects.get(0).x + SEGMENT_LENGTH) >= getWidth() || rects.get(0).y <= 30
				|| (rects.get(0).y + SEGMENT_LENGTH) >= getHeight()) {

			return false;
		} else {
			return true;
		}
	}

	public void newApple() {
		Random rand = new Random();

		ArrayList<Point> empty = new ArrayList<Point>();

		for (int i = 3; i <= 16; i++) {
			for (int j = 2; j <= 40; j++) {
				empty.add(new Point(j * 30, i * 30));
			}
		}

		for (int i = 0; i < bodyParts; i++) {
			Point currentPoint = new Point(rects.get(i).x, rects.get(i).y);

			if (empty.contains(currentPoint)) {
				empty.remove(currentPoint);
			}
		}

		if (empty.size() > 0) {
			Point chosen = empty.get(rand.nextInt(empty.size()));
			appleX = chosen.x;
			appleY = chosen.y;
		} else {
			System.out.println("Congrats! You won!");
			gameOver();
		}
	}

	public void checkApple() {
		if (appleX == rects.get(0).x && appleY == rects.get(0).y) {
			rects.add(new Rectangle(rects.get(bodyParts - 1).x + SEGMENT_LENGTH, rects.get(bodyParts - 1).y,
					SEGMENT_LENGTH, SEGMENT_LENGTH));
			bodyParts++;
			score++;

			if (score > highScore) {
				highScore = score;
			}

			newApple();
		}
	}

	public boolean checkCollision() {
		for (int i = 1; i < bodyParts; i++) {
			if (rects.get(i).x == rects.get(0).x && rects.get(i).y == rects.get(0).y) {
				return true;
			}
		}

		return false;
	}

	public void moveRight() {
		this.points = new Point[bodyParts];
		int x;
		int y;
		for (int i = bodyParts - 1; i > 0; i--) {
			x = rects.get(i - 1).x;
			y = rects.get(i - 1).y;
			points[i] = new Point(x, y);
		}

		points[0] = new Point(rects.get(0).x + SEGMENT_LENGTH, rects.get(0).y);

	}

	public void moveLeft() {
		this.points = new Point[bodyParts];
		int x;
		int y;
		for (int i = bodyParts - 1; i > 0; i--) {
			x = rects.get(i - 1).x;
			y = rects.get(i - 1).y;
			points[i] = new Point(x, y);
		}

		points[0] = new Point(rects.get(0).x - SEGMENT_LENGTH, rects.get(0).y);

	}

	public void moveUp() {
		this.points = new Point[bodyParts];
		int x;
		int y;
		for (int i = bodyParts - 1; i > 0; i--) {
			x = rects.get(i - 1).x;
			y = rects.get(i - 1).y;
			points[i] = new Point(x, y);
		}

		points[0] = new Point(rects.get(0).x, rects.get(0).y - SEGMENT_LENGTH);

	}

	public void moveDown() {
		this.points = new Point[bodyParts];
		int x;
		int y;
		for (int i = bodyParts - 1; i > 0; i--) {
			x = rects.get(i - 1).x;
			y = rects.get(i - 1).y;
			points[i] = new Point(x, y);
		}

		points[0] = new Point(rects.get(0).x, rects.get(0).y + SEGMENT_LENGTH);
	}

	public void gameOver() {
		gameOver = true;
		GameFrame.move.stop();
		movement.stop();
		headTemp = headColor;
		bodyTemp = bodyColor;
		borderTemp = borderColor;

		headColor = headColor.darker();
		bodyColor = bodyColor.darker();
		borderColor = borderColor.darker();
		RescaleOp rop = new RescaleOp(0.7f, 0, null);
		BufferedImage backgroundBuff = rop.filter((BufferedImage) background, null);
		BufferedImage appleBuff = rop.filter((BufferedImage) apple, null);

		backgroundTemp = background;
		appleTemp = apple;

		background = (Image) backgroundBuff;
		apple = (Image) appleBuff;

		gameOver1 = new Rectangle(495, 70, 300, 430);
		playAgain.setVisible(true);
		quit.setVisible(true);
	}

	public void newGame() {
		newGame = true;
		bodyParts = 6;
		score = 0;
		rects = new ArrayList<Rectangle>();
		rects.add(0, new Rectangle(new Point(630, 300), segmentSize));
		for (int i = 1; i < bodyParts; i++) {
			rects.add(i, new Rectangle(rects.get(i - 1).x + SEGMENT_LENGTH, rects.get(i - 1).y, SEGMENT_LENGTH,
					SEGMENT_LENGTH));
		}
		gameOver1 = null;

		headColor = headTemp;
		bodyColor = bodyTemp;
		borderColor = borderTemp;
		background = backgroundTemp;
		apple = appleTemp;
		gameOver = false;
		playAgain.setVisible(false);
		quit.setVisible(false);
		newApple();
		this.points = null;
		movement.restart();
		GameFrame.move.restart();
		repaint();
	}

	public Rectangle getHead() {
		return rects.get(0);
	}

	public boolean completedMovement() {
		return completedMovement;
	}

	public void setCompletedMovement(boolean completedMovement) {
		this.completedMovement = completedMovement;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.drawImage(background, 0, 0, null);

		g2d.setPaint(headColor);
		g2d.fillRoundRect(rects.get(0).x, rects.get(0).y, rects.get(0).width, rects.get(0).height, 100, 10);

		g2d.setPaint(bodyColor);
		for (int i = 1; i < bodyParts; i++) {
			g2d.fillRoundRect(rects.get(i).x, rects.get(i).y, rects.get(i).width, rects.get(i).height, 100, 10);
		}

		g2d.setPaint(borderColor);
		g2d.setStroke(new BasicStroke(30));
		g2d.drawRect(15, 45, 1260, 510);
		g2d.drawLine(0, 15, 1290, 15);

		g2d.drawImage(apple, appleX, appleY, null);

		g2d.drawImage(apple, 30, 10, null);
		g2d.setPaint(new Color(255, 255, 255));
		g2d.setFont(new Font("Calibri", Font.PLAIN, 20));
		g2d.drawString(Integer.toString(score), 70, 40);

		if (gameOver1 != null) {
			g2d.setPaint(new Color(86, 193, 248, 255));
			g2d.fillRoundRect(gameOver1.x, gameOver1.y, gameOver1.width, gameOver1.height, 10, 10);

			g2d.drawImage(gameOver2, 495, 410, null);

			g2d.drawImage(gameOverApple, 545, 100, null);

			g2d.setPaint(new Color(255, 255, 255));
			g2d.setFont(new Font("Calibri", Font.PLAIN, 40));

			g2d.drawString(Integer.toString(score), 555, 230);

			g2d.drawImage(highscore, 670, 100, null);
			g2d.drawString(Integer.toString(highScore), 680, 230);

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == playAgain) {
			newGame();
		}

		if (e.getSource() == quit) {
			System.exit(0);
		}

	}

}
