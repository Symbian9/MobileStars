package ui;
/**
 * This class displays 'about' information regarding the application.
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class AboutForm extends Form implements CommandListener {
	
	// calling application
	Application app;

	/**
	 * Public AboutForm - displays information about the application
	 * @param app - calling application
	 */
	public AboutForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;

		// add constellation information to form
		this.append(Config.APP_NAME + " was designed and built by Simon Fleming as a final year Computer Science project while studying at Sussex University, Falmer, UK.");           

        // create and add command to confirm location
		this.addCommand(new Command("Back", Command.ITEM, 1));
 
        this.setCommandListener(this);		
	}
	
	/**
	 * This method is called when the location is confirmed.
	 */
	public void commandAction(Command c, Displayable d){	
		// deal with user command..... user wants to return to the main menu
		app.updateScreen(0);
	}	
}
