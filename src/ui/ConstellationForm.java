package ui;
/**
 * This class displays information regarding a specific constellation
 */
import javax.microedition.lcdui.*;
import objects.Constellation;
import datasets.Constellations;
import system.Application;
import system.Config;

public class ConstellationForm extends Form implements CommandListener {

	// constellation reference and request object
	Constellation constellation   = null;
	
	// remote data
	String data = null;
	
	// calling application
	Application app;

	/**
	 * Public ConstellationForm constructor displays a specific constellations textual information
	 * @param app - calling application
	 * @param c - constellation of interest array index
	 */
	public ConstellationForm(Application app, int c) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;

		// add constellation information to form
		this.append(Constellations.consts[c].getFullName()+"\n");
		this.append(Constellations.consts[c].getFurtherInfo());           

        // create and add command to confirm location
		this.addCommand(new Command("Back", Command.ITEM, 1));            
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
