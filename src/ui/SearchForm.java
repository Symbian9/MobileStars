package ui;
/**
 * This form will provide a search form for user.
 * Constellations or CelestialObjects will be searched.
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;
import java.util.Vector;

public class SearchForm extends Form implements CommandListener {
	// choice groups holding the options
	private ChoiceGroup sc;
	
	// Textfield variables
	private TextField st;
	
	// calling application
	private Application app;
	
	// define commands
	private Command cmOK;
	private Command cmBACK;	

	/**
	 * Public SetupForm constructor to create the new form, adding required fields for user to select.
	 * @param app - calling application
	 */
	public SearchForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		this.append("Please enter a term and select a search category:");   
		
		// search term
		st  = new TextField("", "", 15, TextField.ANY);
		this.append(st);	
		
		// display the search category
		sc=new ChoiceGroup("Search Type:",Choice.EXCLUSIVE);
		sc.append("Constellations", null);
		sc.append("Celestial Objects", null);
		
		// set choice group selected index
		sc.setSelectedIndex(0, true);
		this.append(sc);
		
		// add commands
		cmOK    = new Command("OK", Command.ITEM, 0);
		cmBACK  = new Command("Back", Command.ITEM, 1);
		
		// add commands
		this.addCommand(cmOK);
		this.addCommand(cmBACK);
		
        this.setCommandListener(this);		
	}

	
	
	/**
	 * This method is called when the colours are confirmed
	 */
	public void commandAction(Command c, Displayable d){

		if(c==this.cmOK) {
			System.out.println("CALLED " + sc.getSelectedIndex());
			
			// collect user search
			Vector v = this.app.conductSearch(st.getString(), sc.getSelectedIndex());
			
			// display results as a list (if any)
			if(v.size()==0) {
				// no results - show alert!
				Alert alert = new Alert("No results found!", "No objects exist with this name or you name a spelling mistake. Please try again", null, AlertType.INFO);	
				this.app.getDisplay().setCurrent(alert);
			} else {
				// one result found and one result highlighted! return to skyview
				System.out.println("RESULTS FOUND!");
				this.app.updateScreen(-1);
			}
		} else {
			// back
			this.app.updateScreen(-1);
		}
	}	
}
