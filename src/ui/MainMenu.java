package ui;
/**
 * The main menu screen, the first user interface
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.*;
import system.Application;

public class MainMenu extends Screen {
	
	// define the menu options
	private String[] menu_option = {
		"Start",
		"Setup",
		"Updates",
		"About",
		"Exit"		
	};
	
	// logo image
	private Image image;
	
	// define commands
	private Command cmOK;
	private Command cmBACK;	
	
	// current option
	private int current=0;
	
	/**
	 * Main menu constructor
	 */
	public MainMenu(Application app) {
		super(app);
		
		// add commands
		cmOK    = new Command("OK", Command.ITEM, 0);
		cmBACK  = new Command("Exit", Command.ITEM, 1);
		
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
	 * Key pressed on phone, perform appropriate action
	 */
	public void commandAction(Command c, Displayable s) {
	    if (c == cmOK) {
	       // simulate pressing key 5 / joystick down
	    	keyPressed(KEY_NUM5);
	    }
	    if(c == cmBACK) {
			// EXIT application.
			try {
				this.app.destroyApp(true);
			} catch (Exception msce) {
				// failed to close app
			}
	    }
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
		g.drawString(" - Main Menu -", 7, 0, Graphics.TOP | Graphics.LEFT );		

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
	    
		// display image
		g.drawImage (image, 190, 255, Graphics.BOTTOM | Graphics.RIGHT);
	}
	
	/** Handle user key presses **/
	public void keyPressed(int code) {
		System.out.println("(" + code + ") key press!");
		switch(code) {
			case KEY_NUM5: case -5: case -7:
				// action the selected option
				switch(current){
					case 0:
						// load SKY VIEW
						app.startNow();
					break;
					case 1: 
						// load SETUP menu
						this.app.setupMenu(); // auto loads application			
					break;
					case 2:
						// load UPDATES menu
						this.app.updateScreen(3);
					break;
					case 3:
						// show ABOUT screen
						this.app.updateForm(6);
					break;
					case 4:
						// EXIT application.
						try {
							this.app.destroyApp(true);
						} catch (Exception msce) {
							// failed to close app
						}
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
