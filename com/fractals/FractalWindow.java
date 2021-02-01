package com.fractals;

import javax.swing.*;
import java.awt.event.*;

public class FractalWindow {

    /**
       Run as a standalone application, opening a window containing
       the fractal panel.
       @param args not used
    */
    public static void main (final String args[]) {
		final FractalPanel panel = new FractalPanel();
		panel.init();

		final JFrame theWindow = new JFrame();
		theWindow.getContentPane().add(panel);
		theWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});
		theWindow.setTitle("Threaded Fractals");

		final JMenuBar menubar = new JMenuBar();
		final JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);

		final JMenuItem exitItem = new JMenuItem("Quit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent ev) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		theWindow.setJMenuBar(menubar);

		theWindow.pack();
		theWindow.setVisible(true);

		final Thread drawingThread = new Thread(panel, "DrawingThread");
	drawingThread.start();
    }     
}
