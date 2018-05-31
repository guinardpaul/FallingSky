package com.fallingsky;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.fallingsky.input.KeyManager;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private final int WIDTH = 300;
	private final int HEIGHT = 400;
	private final int SCALE = 3;
	private final Dimension SIZE = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	private static final String NAME = "Falling Sky";

	private JFrame frame;

	private boolean running;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;

	// Input
	private KeyManager keyManager;

	public Game() {
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		// Input
		keyManager = new KeyManager();
		addKeyListener(keyManager);

		frame = new JFrame(NAME);
		frame.setResizable(false);
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public synchronized void start() {
		if (running)
			return;
		thread = new Thread(this, "Game Thread");
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		init();

		int fps = 60;
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;

		requestFocus();
		while (running) {
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				ticks++;
				delta--;
			}
		}

		stop();
	}

	private void init() {

	}

	private void tick() {
		keyManager.tick();
	}

	private void render() {
		bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		g = bs.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		// Drawing
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
		// End Drawing
		bs.show();
		g.dispose();
	}

	public static void main(String[] args) {
		new Game().start();
	}

}
