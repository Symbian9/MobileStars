package ui;
/**
 * Screen showing that updating is taking place
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class Updating extends Screen {

	// private variables
	private Image image;
	
	/**
	 * Main menu constructor
	 */
	public Updating(Application app) {
		
		super(app);
		this.app=app;
		
		// try to load image
	    try {
	        image = Image.createImage ("/logo.png");
	    } catch (Exception e) {
	        System.out.println("Unable to load Image: "+e);
	    }

	}
	
	/**
	 * Updates are now complete
	 * @param outcome - did the updates complete successfully?
	 * @param type - type of update
	 */
	public void complete(boolean outcome, String type) {
		System.out.println("UPDATES COMPLETE!");
	    // update complete, show setup menu
	    UpdatesMenu um = (UpdatesMenu)app.getScreen(3);
	    if(outcome) {
	    	um.updateUsrMsg(type+" updates successful");
	    } else {
	    	um.updateUsrMsg(type+" updates unsuccessful");
	    }
	    app.updateScreen(3);
	}

	/**
	 * The main Canvas paint method
	 */
	public void paint( Graphics g ) {		
		// black background 
	    g.setColor(0, 0, 0);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    g.setColor(0x0FFFFFF);		

	    // show welcome message
	    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
		g.drawString(Config.APP_NAME, 10, 5, Graphics.TOP | Graphics.LEFT );	
		
		// Display Loading with current progress :)
	    g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));	
	    g.drawString("loading updates from server", 10, 35, Graphics.TOP | Graphics.LEFT );
		
		// display image
		g.drawImage (image, 190, 255, Graphics.BOTTOM | Graphics.RIGHT);
	}
	
	/** Handle user key presses **/
	public void keyPressed(int code) {
		// keys do nothing on the loading screen? Perhaps to cancel?		 
	}
	
	/**
	 * Handle user key presses
	 */
	public void commandAction(Command c, Displayable s) {
		// keys do nothing on the loading screen? Perhaps to cancel?
	}	
	
}
