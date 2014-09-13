package ui;
/**
 * Screen showing progress of loading all visible celestial objects. 
 * Progress updated by ApLoader via the updateProgress method
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class Loading extends Screen {

	// current progress
	private int progress=0;
	private String dots = "";
	private Image image;
	// debug
	private String msg;
	
	/**
	 * Main menu constructor
	 */
	public Loading(Application app) {
		
		super(app);
		this.app=app;
		msg = "";
		
		// try to load image
	    try {
	        image = Image.createImage ("/logo.png"); //gal.png
	    } catch (Exception e) {
	        System.out.println("Unable to load Image: "+e);
	    }
	    
	}
	
	/**
	 * Update the progress percentage and repaint
	 * @param progress
	 */
	public void updateProgress(int progress) {
		this.progress=Math.min(progress, 100); // dont exceed 100%
		// repaint screen, progress has been updated.
		repaint();
	}
	
	/**
	 * Update the debugging String 
	 * @param msg
	 */
	public void updateMessage(String msg) {
		this.msg = msg;
	}
	
	/**
	 * The main Canvas paint method
	 */
	public void paint( Graphics g ) {
		
		// black background 
	    g.setColor(0, 0, 0);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    g.setColor(0x0FFFFFF);
		
		// manage the loading dots
		if(progress%20==0)  dots+=".";		
		if(dots.length()>3||progress==100) dots="";
		
	    // show welcome message
	    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
		g.drawString(Config.APP_NAME, 10, 5, Graphics.TOP | Graphics.LEFT );	
		
		// Display Loading with current progress :)
	    g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));	
	    g.drawString("loading your night sky =°) "+dots, 10, 35, Graphics.TOP | Graphics.LEFT );				
		
		// draw progression loading bar
		g.drawRect(10, 55, this.getWidth()-20, 14);
		g.setColor(0x00cf05);
		int largest  = this.getWidth()-21;
		int barFill  = largest*(progress+1)/100;		
		g.fillRect(11, 56, barFill, 13);
		g.setColor(0x0FFFFFF);
		
		// display % complete on loading bar
	    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
		g.drawString(progress + "%", 16, 55, Graphics.TOP | Graphics.LEFT );
		
		// display debug info if available
		if(!msg.equals("")) {
		    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
			g.drawString(msg, 16, 70, Graphics.TOP | Graphics.LEFT );
		}
		
		// display image
		g.drawImage (image, 190, 255, Graphics.BOTTOM | Graphics.RIGHT);
		
	}
	
	/**
	 * Handle user key presses
	 */
	public void commandAction(Command c, Displayable s) {
		// keys do nothing on the loading screen? Perhaps to cancel?
	}
	
	/** Handle user key presses **/
	public void keyPressed(int code) {
		// keys do nothing on the loading screen? Perhaps to cancel?		 
	}
	
}
