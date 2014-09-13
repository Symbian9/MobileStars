package ui;
/**
 * This class displays the most important user settings
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class DetailsForm extends Form implements CommandListener {
	
	// remote data
	String data = null;
	
	// calling application
	Application app;

	/**
	 * New Form showing user details
	 * @param app - calling application
	 */
	public DetailsForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;

		this.append("YOUR SETTINGS:");
		this.append("\nLocation: " + app.getConfig().getTown());
		this.append("\nLongitude: " + app.getConfig().getLong());
		this.append("\nLatitude: " + app.getConfig().getLat());
		this.append("\nTime: " + app.getConfig().getDay(false) + "/" + app.getConfig().getMonth(false) + "/" + app.getConfig().getYear(false) + " " + app.getConfig().getHour(false) + ":" + app.getConfig().getMin(false));
		
        // create and add command to return to the previous page
		this.addCommand(new Command("Back", Command.ITEM, 1));          
        this.setCommandListener(this);		
	}
	
	/**
	 * This method is called when the user wishes to return to the skyview
	 */
	public void commandAction(Command c, Displayable d){	
		//  user wants to return to the skyview
		app.updateScreen(-1);
	}	
}
