package ui;
/**
 * An interface for displaying system controls to the user in the form of a png image.
 * This screen always returns to the skyview on request.
 */
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import system.Application;

public class Controls extends Screen {
	
	// logo image
	private Image image;
	
	// define commands
	private Command cmBACK;			

	public Controls(Application app) {
		super(app);
		
		// add commands
		cmBACK  = new Command("Back", Command.ITEM, 1);
		
		// add commands
		this.addCommand(cmBACK);
		
		// assign command listener
		this.setCommandListener(this);	
		
		// try to load image
	    try {
	        image = Image.createImage ("/controls.png"); //gal.png
	    } catch (Exception e) {
	        System.out.println("Unable to load Image: "+e);
	    }
	}

	/**
	 * Key pressed on phone, perform appropriate action
	 */
	public void commandAction(Command c, Displayable s) {
		// return to the skyview (-1)
    	this.app.updateScreen(-1);
	}	
	
	/**
	 * The main Canvas paint method
	 */
	public void paint( Graphics g ) {
		/** black background **/
	    g.setColor(0, 0, 0);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    
	    /** screen title **/
 		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
		g.setColor(0x0FFFFFF);
		g.drawString(" Controls ", 7, 0, Graphics.TOP | Graphics.LEFT );		    
	    
		// display image
		g.drawImage (image, getWidth()/2, getHeight()/2, Graphics.HCENTER | Graphics.VCENTER);
	}
		
	
}
