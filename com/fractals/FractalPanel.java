package com.fractals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

public class FractalPanel extends JPanel implements Runnable {

	/** Two colors of the fractal */
	private Color color1 = Color.red;
	private Color color2 = Color.blue;

	/** Indicate whether or not a redraw is necessary */
	private boolean needsRedraw = true;

	/** Area fractal is drawn on */
	private JPanel canvas;
	
	/** Input and color picker area */
	private JPanel sidePanel;

	/** Fractal storage */
	private Image buffer = null;
	/** Color picking GUI elements */
	private JButton colorChooser1;
	private JButton colorChooser2;

	/** Integer inputs for fractal parameters */
	private IntegerInput input_a;
	private IntegerInput input_b;
	private IntegerInput input_c;
	private IntegerInput input_dots;


	/**
	 * Default constructor
	 */
	public FractalPanel() {}
	
	/** 
	 * Set program's GUI components
	 */
	public void init() {
		final FractalPanel self = this;
		// set up the components
		
		self.setLayout(new BorderLayout());
		
		canvas = new JPanel() {
			// Draw the contents of buffer to the screen
			public void paint (final Graphics g) {
				if (buffer != null)
					g.drawImage(buffer, 0, 0, this);
			}
		};

		canvas.setBackground(Color.white);
		canvas.setPreferredSize(new Dimension(400, 400));
		add(canvas, "Center");

		// Set up input area and buttons to the right of the canvas
		sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		sidePanel.setBackground(Color.lightGray);

		colorChooser1 = new JButton("Color 1");
		colorChooser1.setForeground(color1);
		colorChooser1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {				
				try {
					Color temp1 = JColorChooser.showDialog(self, "Color 1", color1);					
					if(temp1 != null){
						color1 = temp1;
						colorChooser1.setForeground(color1);
						redrawFractal();
					}
				} catch (final Exception ex) {}
				
			}
		});

		colorChooser2 = new JButton("Color 2");
		colorChooser2.setForeground(color2);
		colorChooser2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					Color temp2 = JColorChooser.showDialog(self, "Color 2", color2);					
					if(temp2 != null){
						color2 = temp2;
						colorChooser2.setForeground(color2);
						redrawFractal();
					}					
				} catch (final Exception ex) {}				
			}
		});

		input_a = new IntegerInput("a:", 31, 3);
		input_a.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					final int a = input_a.getInt();
					redrawFractal();
				} catch (final Exception ex) {};
			}
		});

		input_b = new IntegerInput("b:", 3, 3);
		input_b.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					final int b = input_b.getInt();
					redrawFractal();
				} catch (final Exception ex) {};
			}
		});

		input_c = new IntegerInput("c:", 5, 3);
		input_c.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					final int c = input_c.getInt();
					redrawFractal();
				} catch (final Exception ex) {};
			}
		});

		input_dots = new IntegerInput("# dots:", 65, 3);
		input_dots.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					final int nDots = input_dots.getInt();
					redrawFractal();
				} catch (final Exception ex) {};
			}
		});
		// add components to sidePanel
		sidePanel.add(input_a);
		sidePanel.add(input_b);
		sidePanel.add(input_c);
		sidePanel.add(input_dots);
		sidePanel.add(colorChooser1);
		sidePanel.add(colorChooser2);
		add(sidePanel, "East");
	}

	/**
	 * Redraw the fractal after user has changed a parameter
	 */
	private void redrawFractal() {
		canvas.repaint();
		synchronized (canvas) {
			needsRedraw = true;
			canvas.notifyAll();
		}
	}

	/**
	 * Draws the fractal into the graphics context gbuffer, sets
	 * that as the canvas image and tells the canvas to repaint itself.
	 */
	public void run() {
		boolean done = false;
		needsRedraw = true;
		while (!done) {
			while (needsRedraw) {
				needsRedraw = false;
				final Fractal fractal = new Fractal(input_a.getInt(), input_b.getInt(), 
												   input_c.getInt(), color1, color2);
				final Image nextImage = createImage(canvas.getSize().width, canvas.getSize().height);
				final Graphics gbuffer = nextImage.getGraphics();
				
				buffer = nextImage;

				final int nDots = input_dots.getInt();
				
				for (int i = 0; i < nDots; ++i) {
					fractal.recordBounds(1000);
					Thread.yield();
				}

				fractal.reset();

				for (int i = 0; i < nDots; ++i) {
					fractal.drawFractal(gbuffer, canvas.getSize(), 1000);
					canvas.repaint();
					Thread.yield();
				}

				synchronized (canvas) {
					canvas.repaint();
					if (!needsRedraw) {
						try {
							canvas.wait();
						} catch (final InterruptedException e1) {
							// exit quietly
							needsRedraw = false;
							done = true;							
						}
					}
				}
			}
		}
	}
}
