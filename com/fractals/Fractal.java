package com.fractals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Fractal {

    // The Model

    /** the color used for the first dots drawn (shades gradually into 
	<code>color2</code> as more dots are printed */
    private Color color1;

    /** the color used for the last dots drawn (shades gradually from 
	<code>color1</code> as more dots are printed */
    private Color color2;

    /** one of the parameters governing the fractal shape */
    private int a;
    /** one of the parameters governing the fractal shape */
    private int b;
    /** one of the parameters governing the fractal shape */
    private int c;

    private int nPointsChecked; 
    private int nPointsDrawn; 

    /**
     * The most recently plotted value of x
     */
    private double xLast;
    
    /**
     * The most recently plotted value of y
     */
    private double yLast;
    
    /**
     * Bounding box for this fractal.
     */
    private double xmin = 0.0;
    private double xmax = 0.0;
    private double ymin = 0.0;
    private double ymax = 0.0;

    
    

    public Fractal(int a, int b, int c, Color color1, Color color2) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.nPointsChecked = nPointsDrawn = 0;
        this.color1 = color1;
        this.color2 = color2;
    	xLast = yLast = 0.0;
    }


    /**
     * Resets the last drawn (x,y) position to (0,0).
     * Call this between the last setBounds and the first drawFractal calls. 
     */
    public void reset() {
    	xLast = yLast = 0.0;
    }

    /**
       Interpolate between two extremal integer values
       
       @param x value to return when i==0
       @param y value to return when i==steps-1
       @param steps number of steps to take between x and y
       @param i desired step number
       @return a value that is i/(steps-1) between x and y
     */
    private int interpolate (int x, int y, int i, int steps)
    {
        return (i * y + (steps-i)*x) / steps;
    }


    /**
       Interpolate between two extremal color values
       @param c1 value to return when i==0
       @param c2 value to return when i==steps-1
       @param steps number of steps to take between c1 and c2
       @param i desired step number
       @return a value that is i/(steps-1) between c1 and c2
     */
    private
    Color interpolate(Color c1, Color c2, int i, int steps)
    {
        return new Color (interpolate(c1.getRed(), c2.getRed(), i, steps),
                interpolate(c1.getGreen(), c2.getGreen(), i, steps),
                interpolate(c1.getBlue(), c2.getBlue(), i, steps));
    }


    /**
     * Determine the bounding box required to draw a fractal.
     * Must be performed before any actual drawing.
     * 
     * @param nPoints # of points to compute. Each point affects
     *     the bounding box.
     */
 public void recordBounds(int nPoints)
 {
     double x = xLast;
     double y = yLast;

     for (int dot = 0; dot < nPoints; ++dot) {
             double x2 = nextX(x, y);
             double y2 = nextY(x, y);
             xmin = (x2 < xmin) ? x2 : xmin;
             xmax = (x2 > xmax) ? x2 : xmax;
             ymin = (y2 < ymin) ? y2 : ymin;
             ymax = (y2 > ymax) ? y2 : ymax;
             x = x2;
             y = y2;
     }
     xLast = x;
     yLast = y;
     nPointsChecked += nPoints;
}

    /**
      * Draw a portion of a fractal shape. May be called repeatedly,
      * each time drawing a larger amount;
      * @param g graphics context/device onto which to draw
      * @param component the GUI component into which g draws
      * @param d size of the area in which to draw
      */
    public void drawFractal(Graphics g, Dimension d, int nPoints)
    {
        double dx = ((double) (d.width)) / (xmax - xmin);
        double dy = ((double) (d.height)) / (ymax - ymin);

        double x = xLast;
        double y = yLast;
        for (int dot = nPointsDrawn; dot < nPoints + nPointsDrawn; ++dot) {
            g.setColor (interpolate(color1, color2, dot, nPointsChecked));
            double x2 = nextX(x, y);
            double y2 = nextY(x, y);
            int dotx = (int) (dx * (x2 - xmin));
            int doty = (int) (dy * (y2 - ymin));
            g.drawLine (dotx, doty, dotx, doty);
            x = x2;
            y = y2;
        }
        nPointsDrawn += nPoints;
        xLast = x;
        yLast = y;
    }

    /** One of the two functions that actually define the fractal shape.
        After plotting a point at (x,y), we compute the location of the
        next dot as (nextX(x,y), nextY(x,y)).
        
        @param x x-coordinate of previously-plotted point
        @param y y-coordinate of previously-plotted point
        @return  x-coordinate of next point
        @see #nextY(double x, double y)
     */
    private double nextX (double x, double y)
    {
        if (x == 0.0) {
            return y;
        } else {
            double tmp = Math.log(Math.abs(c * x - b));
            double offset = Math.sin(Math.log(Math.abs(b * x - c))) *
                    Math.atan(tmp * tmp);
            if (x < 0) 
                return y + offset;
            else
                return y - offset;
        }
    }

    /** One of the two functions that actually define the fractal shape.
        After plotting a point at (x,y), we compute the location of the
        next dot as (nextX(x,y), nextY(x,y)).
        
        @param x x-coordinate of previously-plotted point
        @param y y-coordinate of previously-plotted point
        @return  y-coordinate of next point
        @see #nextX(double x, double y)
     */
    private double nextY (double x, double y)
    {
        return a - x;
    }
}