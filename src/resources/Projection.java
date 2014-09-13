package resources;
/**
 * This class represents the method of projection used and its related calculations
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;
import objects.*;
import system.Application;
import datasets.*;
import ui.SkyView;

public abstract class Projection {
	
	// project variables
	protected Canvas c;
	protected SkyView sv;
	protected Application app;
	protected int height,width;
	protected boolean resize=false;
	protected int circle,radios, mincircle;
	protected int rotate;
	// moving left and right
	protected int xOffset=0;
	protected int yOffset=0;
	// number of visible constellations
	protected int visibleConsts = 0;
	// currently selected constellation
	protected int cnum = 0;

	
	/**
	 * Projection constructor
	 * @param c - the canvas
	 */
	public Projection(Canvas c) {
		// assign canvas
		this.c = c;
		
		// assign app
		sv = (SkyView)c;
		this.app = sv.getMyApp();

		// define minimum circle size (zoom out)
    	height = c.getHeight();
		width  = c.getWidth();	
		if(height<=width) {
			mincircle = height;
		} else {
			mincircle = width;
		}
		
    	height = c.getHeight();
		width = c.getWidth();		
		
		//override circle size
		if(!resize) {
			
			if(height<=width) {
				circle = height;
			} else {
				circle = width;
			}		
							
			// give some padding for the default size
			circle = circle - 20; 
		}
		
		radios = circle/2;		
		
		// padding
		mincircle  = mincircle - 20;		

	}
	
	/**
	 * draw the representation of sky for this projection
	 */
	public abstract void plotSkyGrid(Graphics g);
	
	
	/**
	 * Draw the grid for this projection
	 */
	public abstract void drawGrid(Graphics g);
	
	/**
	 * Convert an Altitude / Azimuth to an X,Y in this projection
	 * @param ALT
	 * @param AZ
	 * @return [0] - x
	 * @return [1] - y
	 */
	public abstract int[] alt_az_to_x_y(double ALT, double AZ);
	
	/**
	 * Plot all of the visible constellations
	 * @param g graphics object
	 */
	public void plotConstellations(Graphics g) {
		// reset visible constellations
		this.visibleConsts = 0;
		for(int i=0; i<Constellations.consts.length; i++) {
			// this constellation has line instructions, plot them
			plotConstellation(g, i);	
		}		
	}

	/**
	 * Draw a single constellation of lines
	 */
	public void plotConstellation(Graphics g, int c) {

		// assume constellation is not visible..
		Constellations.consts[c].visible(false);
		
		// set line colour and style		
		if(Constellations.consts[c].isHighlighted()) {
			g.setStrokeStyle(Graphics.SOLID);
			if(this.app.getConfig().getColour()==2) {
				// red eye mode
				g.setColor(0x7f1919);
			} else {
				g.setColor(0xffffff);
			}
		} else {
			if(this.app.getConfig().getColour()==2) {
				// red eye mode
				g.setColor(0x3c1717);
			} else {
				g.setColor(0x5589b5);
			}
			g.setStrokeStyle(Graphics.DOTTED);			
		}
			
		// collect the points
		Vector v = Constellations.consts[c].getLines();
		
		// line start and end x,y's
		int x1,y1,x2,y2;
		int visibleLines = 0;
		
		if(v!=null) {
			for(int i=0; i<v.size(); i++) {
				try {
					Line l = (Line)v.elementAt(i);
					// calculate x,y's for line
					int[] t = alt_az_to_x_y(l.al1, l.az1);
					x1 = t[0];
					y1 = t[1];
					int[] t1 = alt_az_to_x_y(l.al2, l.az2);
					x2 = t1[0];
					y2 = t1[1];
					//System.out.println(x1 + " " + y1 + " " + x2 + " "+y2);
					if(l.al1>0&&l.al2>0) {					
						if((x2<0||x2>=this.c.getWidth())||(y2<0||y2>=this.c.getHeight())) {
							// the X, Y are out of scope of the display! force hidden
							//hidden = true;
						} else {
							g.drawLine(x1, y1, x2, y2);
							visibleLines++;
						}										
					}
				
				} catch(Exception e ) {
					System.out.println("bad ");
				}				
			}	
			
			if(visibleLines>=1) { // ==v.size()
				// consellation is visible
				Constellations.consts[c].visible(true);
			}
			
			// reset the number of visible lines.
			visibleLines =0;
		}
		
	}		
	
	/**
	 * Return the number of visible constellations
	 * @return number of constellations
	 */
	public int numVisibleConsts() {
		return this.visibleConsts;
	}
	
	/**
	 * Plot all visible objects to screen
	 * @param g - graphics
	 * @param app - Application
	 */
	public void plotObjects(Graphics g, Application app) {		
		// loop through each celestial object, plotting appropriately
		Vector co = app.getVector();
		for(int i=0; i<co.size(); i++) {
			CelestialObject c = (CelestialObject)co.elementAt(i);
			plotObject(c, g);
		}			
	}
	
	/**
	 * Plot a single Object: gaining X,Y position
	 * @param object to plot
	 * @param graphics object
	 */
	public void plotObject(CelestialObject s, Graphics g) {

		// convert alt/az to x,y coordinates
		int[] t = alt_az_to_x_y(s.getAltitude(), s.getAzimuth());
		int x = t[0];
		int y = t[1];
	
		// update the celestial objects X, Y
	    s.setXY(x, y);
		
	}
	
	/**
	 * Draw plotted celestial objects
	 * @param graphics object
	 */
	public void drawObjects(Graphics g, Application app) {
		// loop through each celestial object, drawing appropriately
		Vector co = app.getVector();
		for(int i=0; i<co.size(); i++) {
			CelestialObject c = (CelestialObject)co.elementAt(i);
			if(c.isVisible()&&c.getMag()<=sv.getThreshold()) {
				c.onScreen(true);
				drawAnObject(c, g, app);
			} else {
				c.onScreen(false);
			}
		}
	}
	
	
	/**
	 * Draw a celestial object
	 * @param object to draw
	 * @param graphics object
	 */
	public void drawAnObject(CelestialObject c, Graphics g, Application app) {
		
		// get the current configuration colour setting
		int cset = app.getConfig().getColour();		
	    	    
	    // get the star coordinates
	    int x = c.getX();
	    int y = c.getY();
	    
	    // draw a star
	    if(c instanceof Star) {
	    	Star s = (Star)c;
		    // should the star is visible on screen? Based on current circle size.

		    if(this.circle<300) {	    	
		    	if(s.getMag()<3) {
		    		// pass back to star draw method
		    		s.draw(g, this.circle, cset);
		    		c.onScreen(true);
		    	} else {
		    		c.onScreen(false);
		    	}
		    } else if(this.circle<500) {
		    	if(s.getMag()<4) {
		    		// pass back to star draw method
		    		s.draw(g, this.circle, cset);
		    		c.onScreen(true);
		    	} else {
		    		c.onScreen(false);
		    	}
		    } else {
		    	if(s.getMag()<6) {
		    		// pass back to star draw method
		    		s.draw(g, this.circle, cset);
		    		c.onScreen(true);
		    	} else {
		    		c.onScreen(false);
		    	}
		    }
		    
		    // bayer designation displayed if required
		    if(circle>1250&&this.app.getConfig().getBayer()) {
			    // show Bayer Designation if available
			    if(s.getBayer()!=-1&&s.getBayer()<7) {
				    g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
					g.setColor(0x0FFFFFF);
					String[] greeks = {"\u03B1","\u03B2","\u03B3","\u03B4","\u03B5","\u03B6","\u03B7"};
				    g.drawString(greeks[s.getBayer()], x, y, Graphics.TOP | Graphics.LEFT );
			    }	
		    }
		    
	    } else {
	    	// all other objects... the sun, moon and planets etc
	    	c.draw(g, this.circle, cset);
	    }   
	    
	    // determine if this object is onscreen at all in terms of x,y
	    if(x<0||x>this.width||y<0||y>this.height) {
	    	c.onScreen(false);
	    } else {
	    	c.onScreen(true);
	    }
	
	}
	
	/**
	 * Update the rotate variable (rotation)
	 * @param u amount to adjust by
	 */
	public void updateRotate(int u) {
		this.rotate = (this.rotate+u)%360;
	}
	
	/**
	 * Update the circle size (zoom)
	 * @param u amount to adjust by
	 */
	public void updateCircle(int u) {
        /* don't allow past max zoom out */        
        if(circle+u<mincircle) { 
        	this.circle = mincircle;
        } else { 
        	this.circle += u;
        }	
        
    	// bring circle back to center when zooming out
        if(u<0) {
			if(xOffset>0) {
				xOffset -= 10;
				if(xOffset<0) {
					xOffset = 0;
				}
			}
			if(xOffset<0) {
				xOffset+= 10;
				if(xOffset>0) {
					xOffset = 0;
				}
			}
			if(yOffset>0) {
				yOffset -= 10;
				if(yOffset<0) {
					yOffset = 0;
				}
			}
			if(yOffset<0) {
				yOffset+= 10;
				if(yOffset>0) {
					yOffset = 0;
				}
			}			
        }
        // update radios
        this.radios = circle/2;
	}
	
	/**
	 * Update the circle x offset to move left and right
	 * @param o
	 */
	public void updateXOffset(int o) {
		this.xOffset += o;
	}
	
	/**
	 * Update the circle y offset to move up and down
	 * @param o
	 */
	public void updateYOffset(int o) {
		this.yOffset += o;
	}
	
	
	/**
	 * Toggle resize boolean
	 */	
	public void resize(boolean r) {
		resize = r;
	}
	
	
	/**
	 * Draw compass points
	 * @param g - graphics object
	 */
	public abstract void drawCompass(Graphics g);
	
	
	/**
	 * Display the current magnitude threshold
	 * @param g
	 * @param mag
	 */
	public void writeMagThreshold(Graphics g, int mag) {
		g.setColor(0x0FFFFFF);	
		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		g.drawString(""+mag, 2 , 0, Graphics.TOP | Graphics.LEFT );
	}
	
	/**
	 * Print the current selected constellation name
	 * @param g - Graphics object
	 * @param c - selected constellation
	 */
	public void writeConstellationName(Graphics g, int c) {
		if(c!=-1) {			
		    // Set Font style and colour
		    g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
			g.setColor(0x0FFFFFF);			
			// give background
			g.setColor(000);
			g.fillRect(0, this.c.getHeight()-30, this.c.getWidth(), 20);
			// write name
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
			g.setColor(0x0FFFFFF);
			g.drawString(Constellations.consts[c].getFullName()+" «", this.c.getWidth()-10 , this.c.getHeight()-30, Graphics.TOP | Graphics.RIGHT );
		}
	}
	
	/**
	 * Display the currently selected celestial objects name
	 * @param g
	 * @param co
	 */
	public void writeObjectName(Graphics g, CelestialObject co) {
		if(co!=null) {
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));
			g.setColor(0x1eff00);
			g.drawString(co.getName(), this.c.getWidth()-10 , 10, Graphics.TOP | Graphics.RIGHT );
		}
	}
	
	/**
	 * Print the current selected star name
	 * @param g - Graphics object
	 * @param s - selected star
	 */
	public void writeStarName(Graphics g, int s) {
		if(s!=0) {
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));
			g.setColor(0x1eff00);
			g.drawString(Stars.stars[s].getName().trim(), this.c.getWidth()-10 , 10, Graphics.TOP | Graphics.RIGHT );
		}
	}
	
	/**
	 * Get the current circle size
	 * @return
	 */
	public int getCircle() {
		return this.circle;
	}
	
	/**
	 * Set circle size, updating radios accordingly
	 * @param c - new circle size
	 */
	public void setCircle(int c) {
		radios = c/2;	
		this.circle = c;
	}

	/**
	 * Get the X Offset
	 * @return - x offset
	 */
	public int getXOffSet() {
		return this.xOffset;
	}
	
	/**
	 * Set the X Offset
	 * @param x
	 */
	public void setXOffSet(int x) {
		this.xOffset = x;
	}
	
	/**
	 * Get the Y Offset
	 * @return - y offset
	 */
	public int getYOffSet() {
		return this.yOffset;
	}
	
	/**
	 * Set the Y Offset
	 * @param y
	 */
	public void setYOffSet(int y) {
		this.yOffset = y;
	}	
	
	/**
	 * get rotate variable
	 * @return
	 */
	public int getRotate() {
		return this.rotate;
	}
	
	/**
	 * Set the rotate variable
	 * @param r - new rotate value
	 */
	public void setRotate(int r) {
		this.rotate = r;
	}
	
	/**
	 * Get the currently selected constellation
	 * @return selcted constellation
	 */
	public int getcNum() {
		return this.cnum;
	}
	
	/**
	 * Set the selected constellation
	 * @param c - contellation to select
	 */
	public void setcNum(int c) {
		this.cnum = c;
	}
}
