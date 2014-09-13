package resources;
/**
 * Stereographic Projection representation
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import objects.Sun;

public class Stereographic extends Projection {

	int counter=0;
	
	/**
	 * Sterographic projection constructor
	 * @param c - the canvas being used
	 */
	public Stereographic(Canvas c) {
		super(c);
	}
	
	public void plotSkyGrid(Graphics g) {
		
		/** draw outer circle **/    
		g.setColor (40, 40, 40);
		g.drawArc((width-circle)/2+xOffset, (height-circle)/2+yOffset, circle, circle, 0, 360);
		
		/** find out if the sun is visible **/
		boolean daytime = false;
		if(this.app.getVector().elementAt(0) instanceof Sun) {
			Sun sun = (Sun)this.app.getVector().elementAt(0);
			if(sun.isVisible()) {
				daytime = true;
			}
		}
		
		/** draw mini circles - if required **/
		if(this.app.getConfig().getAtmos()&&this.app.getConfig().getColour()!=2) {
			int portion = circle/9;
			double p = 1;
			// define portions for this grid
			double[] pg = {0.8, 1.6, 2.4, 3.3, 4.2, 5.2, 6.3, 7.5, 9.1};			
			for(int i=8; i>0; i--) {
				p = portion*pg[i];
				g.setColor (i*2, 0, 3*(i*4)); // night time
				if(daytime) {
					g.setColor (i*4, 0, 3*(i*8)); // day time
				} 				
				g.fillArc((width-(int)p)/2+xOffset, (height-(int)p)/2+yOffset, (int)p, (int)p, 0, 360);
			}				
		}
	}
	
	
	/**
	 * Draw the grid for this projection
	 */
	public void drawGrid(Graphics g) {
		
		// set colour of grid
		g.setColor (0x2d376a); 
		
		/** draw reference circles **/
		int portion = circle/9;
		double p = 1;
		
		// define portions for this grid
		double[] pg = {0.8, 1.6, 2.4, 3.3, 4.2, 5.2, 6.3, 7.5};
		
		for(int i=0; i<8; i++) {
		    p = portion*pg[i];
			g.drawArc((width-(int)p)/2+xOffset, (height-(int)p)/2+yOffset, (int)p, (int)p, 0, 360);
		}

		/** draw reference lines **/
		for(int i=0; i<10; i++) {
		 	 p = i*30;			
		     int x1 = (width/2) +  xOffset  + (int)(radios * Math.cos((p+rotate)*Math.PI/180));
		     int y1 = (height/2) + yOffset - (int)(radios * Math.sin((p+rotate)*Math.PI/180)); 		     
		     int x2 = (width/2) +  xOffset  - (int)(radios * Math.cos((p+rotate)*Math.PI/180));
		     int y2 = (height/2) + yOffset + (int)(radios * Math.sin((p+rotate)*Math.PI/180));  
		     g.drawLine(x2, y2, x1, y1);
		}	
		
		/** Show Altitude/Azimuth numbers! **/
		if(this.circle>750) {
			
		}
	}		

	
	/**
	 * Draw compass points
	 */
	public void drawCompass(Graphics g) {
		
		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_LARGE));
		if(this.app.getConfig().getColour()==2) {
			// redeye mode
			g.setColor(0x7f1919);
		} else {
			g.setColor(0, 255, 0);
		}
		
		/**
		 * West are east are reversed
		 */
			
		/* north */
        int x = (width/2) + (int)(85 * Math.cos((90+rotate)*Math.PI/180));
	    int y = (height/2) - (int)(85 * Math.sin((90+rotate)*Math.PI/180)); 
	    g.drawString("N", x, y, Graphics.TOP | Graphics.HCENTER );
	    
	    /*  west */ 		    
        x = (width/2) + (int)(85 * Math.cos((0+rotate)*Math.PI/180));
	    y = (height/2) - (int)(85 * Math.sin((0+rotate)*Math.PI/180));  		    
	    g.drawString("W", x, y, Graphics.TOP | Graphics.HCENTER );
	    
	    /* east */
        x = (width/2) + (int)(85 * Math.cos((180+rotate)*Math.PI/180));
	    y = (height/2) - (int)(85 * Math.sin((180+rotate)*Math.PI/180));  			    
	    g.drawString("E", x, y, Graphics.TOP | Graphics.HCENTER );
	    
	    /* south */
        x = (width/2) + (int)(85 * Math.cos((270+rotate)*Math.PI/180));
	    y = (height/2) - (int)(85 * Math.sin((270+rotate)*Math.PI/180));  		    
	    g.drawString("S", x, y, Graphics.TOP | Graphics.HCENTER );
	}
	
	/**
	 * Convert an Altitude / Azimuth to an X,Y in this projection
	 * @param ALT
	 * @param AZ
	 * @return [0] - x
	 * @return [1] - y
	 */
	public int[] alt_az_to_x_y(double ALT, double AZ) {
		
		// Theta = 90 - altitude
		// Phi = azimuth
		// r = 1 - all the same distance

		// x = r * sin(Theta) * cos(Phi)
		// y = r * sin(Theta) * sin(Phi)
		// z = r * cos(Theta)
		
		double x, y, z;
		// http://en.wikipedia.org/wiki/Spherical_coordinates
		double theta = Math.toRadians(90-ALT);
		double phi   = Math.toRadians(AZ+90+rotate);
		double r1    = radios;
		
		// http://en.wikipedia.org/wiki/Spherical_coordinates
		x = Math.sin(theta) * Math.cos(phi);
		y = Math.sin(theta) * Math.sin(phi);
		z = Math.cos(theta);

		// get sterographic X, Y
		// http://en.wikipedia.org/wiki/Stereographic_projection
		// 1 - z actually needed as 1 + z!
		int[] r = {0,0};
		r[0] = (int)((width/2)  + xOffset  +  (x / (1 + z))*r1);
		r[1] = (int)((height/2) + yOffset  -  (y / (1 + z))*r1);

		System.out.println(x + ", " + y + ", " + z);
		return r;
	}
	
}
