package resources;
/**
 * Equal Area Projection representation
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import objects.Sun;

public class EqualArea extends Projection {

	int counter=0;
	
	/**
	 * Equal Area projection constructor
	 * @param c - the canvas being used
	 */
	public EqualArea(Canvas c) {
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
		int thisSize;
		if(this.app.getConfig().getAtmos()&&this.app.getConfig().getColour()!=2) {
			int portion = circle/9;
			for(int i=9; i>0; i--) {
				thisSize = i*portion;		
				g.setColor (i*2, 0, 3*(i*4)); // night time
				if(daytime) {
					g.setColor (i*4, 0, 3*(i*8)); // day time
				} 		
				g.fillArc((width-thisSize)/2+xOffset, (height-thisSize)/2+yOffset, thisSize, thisSize, 0, 360);
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
		int thisSize;
		int portion = circle/9;
		for(int i=9; i>0; i--) {
			thisSize = i*portion;
			g.drawArc((width-thisSize)/2+xOffset, (height-thisSize)/2+yOffset, thisSize, thisSize, 0, 360);			
		}			
		
		/** draw reference lines **/
		for(int i=0; i<10; i++) {
		 	 int p = i*30;			
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

		int[] r = {0,0};
		// get  position
		double al = 1-(ALT/90); // !!! 1 - alt as greater altitudes are actually at 0!
		double az = AZ+90+rotate;
		
		// + 90 to convert calculation to required grid format
        r[0] = (width/2)+xOffset + (int)((radios*al) * Math.cos((az)*Math.PI/180));
        r[1] = (height/2)+yOffset - (int)((radios*al) * Math.sin((az)*Math.PI/180));
		
		return r;
	}
	
}
