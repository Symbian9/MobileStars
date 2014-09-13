package ui;
/**
 * This class displays textual help information to a user
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class HelpForm extends Form implements CommandListener {

	// remote data
	String data = null;
	
	// calling application
	Application app;

	/**
	 * Public HelpForm constructor displays help information
	 * @param app - calling application
	 */
	public HelpForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;

		// add constellation information to form
		this.append("Welcome to the help section. The circle that you will see in the application is a representation of the nightsky, as if you were looking directly up at the sky while laying on the ground. To be use this representation, try looking north (where the Letter 'N' representing north will be at the bottom of the screen), zoom in slightly and try to match it up with the sky.");           

        // create and add command to confirm location
		this.addCommand(new Command("Back", Command.ITEM, 1));
       // this.addCommand(new Command("MORE INFO", Command.ITEM, 2));	            
        this.setCommandListener(this);		
	}
	
	/**
	 * This method is called when the user wishes to return to the skyview
	 */
	public void commandAction(Command c, Displayable d){	
		// deal with user command..... user wants to return to the skyview
		app.updateScreen(-1);
	}	
}
