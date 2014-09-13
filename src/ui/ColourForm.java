package ui;
/**
 * This class allows a user to select a colour setting for the application
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class ColourForm extends Form implements CommandListener {
	
	// choice group holding the places
	ChoiceGroup cg;
	
	// calling application
	Application app;

	/**
	 * Public ColourForm displays possible colour options to the user
	 * @param app - calling application
	 */
	public ColourForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		//his.append("Welcome first time user!");
		this.append("Please select your preferred option for the colours of stars:");           
                    
        // Display all colour options  
        cg=new ChoiceGroup("Colours:",Choice.EXCLUSIVE);
 
        for(int i=0; i<Config.colours.length;  i++) {
        	cg.append(Config.colours[i],null);        	
        }        
        
        // set choice group selected index
        cg.setSelectedIndex(app.getConfig().getColour(), true);        
        
        // add dynamic location choices to form
        this.append(cg);

        // create and add command to confirm location
        this.addCommand(new Command("Confirm", Command.ITEM, 2));	            
        this.setCommandListener(this);		
	}

	
	
	/**
	 * This method is called when the colours are confirmed
	 */
	public void commandAction(Command c, Displayable d){
		System.out.println("called" + cg.getSelectedIndex());
		
		// collect selected location values and update configuration
		int index = cg.getSelectedIndex();
		app.getConfig().setColour(index);
		
		// update user message
		SetupMenu sm  = (SetupMenu)app.getScreen(2);
		sm.updateUsrMsg("Colour updated successfully.");		
		
		// projection updated, return to options menu...
		app.updateScreen(2);	
	}	
}
