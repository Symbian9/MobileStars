package ui;
/**
 * The setup menu screen
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.*;
import system.Application;

public class SetupMenu extends Screen {
	
	// define the menu options
	private String[] menu_option = {
		"Location",
		"Date/Time",
		"Projection",
		"Colours",
		"Settings",
		"Long/Lat Entry"
	};
	
	// current menu option
	private int current=0;
	
	// user message
	private String usrMsg="";
	
	// define commands
	private Command cmOK;
	private Command cmBACK;	
	
	// logo image
	private Image image;	
		
	
	/**
	 * Main menu constructor
	 */
	public SetupMenu(Application app) {
		super(app);
		
		// add commands
		cmOK    = new Command("OK", Command.ITEM, 0);
		cmBACK  = new Command("Back", Command.ITEM, 1);
		
		// add commands
		this.addCommand(cmOK);
		this.addCommand(cmBACK);
		
		// assign command listener
		this.setCommandListener(this);		
		
		// try to load image
	    try {
	        image = Image.createImage ("/logo.png"); //gal.png
	    } catch (Exception e) {
	        System.out.println("Unable to load Image: "+e);
	    }		
	}
	
	/**
	 * Update the user message
	 * @param s - string to display
	 */
	public void updateUsrMsg(String s) {
		this.usrMsg = s;
	}
	
	/**
	 * The main Canvas paint method
	 */
	public void paint( Graphics g ) {
		/** black background **/
	    g.setColor(0, 0, 0);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    
		/** page title **/
 		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
		g.setColor(0x0FFFFFF);
		g.drawString(" - Setup Menu -", 7, 0, Graphics.TOP | Graphics.LEFT );	    

	    /** Display each menu option **/
	    for(int i=0; i<menu_option.length; i++) {
	    	if(current==i) {
	    		// show highlighted colour
	    		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
	    		g.setColor(0x0FFFFFF);
	    		g.drawString("[" + menu_option[i] + "]", 7, 7+(i+1)*20, Graphics.TOP | Graphics.LEFT );
	    	} else {
	    		// show standard colour
	    		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE));
	    		g.setColor(0xcccccc); 	   
	    		g.drawString(menu_option[i], 7, 7+(i+1)*20, Graphics.TOP | Graphics.LEFT );
	    	}	    	
	    }
	    
	    /** display user message **/
	    if(this.usrMsg!=null) {
	    	g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
    		g.setColor(0xcccccc); 	   
    		g.drawString(usrMsg, 7, 150, Graphics.TOP | Graphics.LEFT );
	    }
	    
		// display image
		g.drawImage (image, 190, 255, Graphics.BOTTOM | Graphics.RIGHT);	    
	}
	
	/** Handle user key presses **/
	public void commandAction(Command c, Displayable s) {
	    if (c == cmOK) {
	       // simulate pressing key 5 / joystick down
	    	keyPressed(KEY_NUM5);
	    }
	    if(c == cmBACK) {
			// Return to the main menu
			this.app.updateScreen(0);
	    }
	}		
		
	public void keyPressed(int code) {
		System.out.println("(" + code + ") key press!");
		switch(code) {
			case KEY_NUM5: case -5: case -7:
				// reset user msg
				this.usrMsg = null;
				// action the selected option
				switch(current){
					case 0:					
						// UPDATE LOCATION
						app.updateForm(0);
						// toggle skip
						//LocationForm lf = (LocationForm)app.getForm(0);						
					break;
					case 1:
						// update date / time (fresh form each time)
						app.doDateTime();
					break;
					case 2:
						// update projection
						app.updateForm(1);
					break;
					case 3:
						// update colours
						app.updateForm(2);
					break;
					case 4:
						// update other settings
						app.updateForm(4);
					break;
					case 5:
						// update location (manual)
						app.updateForm(7);
					break;
				}
					
			break;
			case KEY_NUM2: case -1:
				// move to previous item in the menu
				current = (current-1) % (menu_option.length);
				System.out.println(current);
				if(current==-1) current=menu_option.length-1; // reset current
				System.out.println(current);
				repaint();
			break;
			case KEY_NUM8: case -2:
				// move to next item in the menu
				current = (current+1) % (menu_option.length);
				System.out.println(current);
				repaint();				
			break;		
		}
		 
	}
	
}
