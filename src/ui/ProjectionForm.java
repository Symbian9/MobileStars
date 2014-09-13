package ui;
/**
 * This class collects and saves a users preferred projection type
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class ProjectionForm extends Form implements CommandListener {
	
	// choice group holding the places
	private ChoiceGroup cg;
	
	// calling application
	private Application app;
	
	/**
	 * Public SetupForm constructor to create the new form, adding required fields for user to select.
	 * @param app - calling application
	 */
	public ProjectionForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		this.append("You can view the night sky in a number of different ways, please select your preferred view from the following options:");           
                    
        // include all projection types   
        cg=new ChoiceGroup("Projection:",Choice.EXCLUSIVE);
 
        for(int i=0; i<Config.projections.length;  i++) {
        	//if(i>0) cg.setFont(i-1, Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        	cg.append(Config.projections[i], null);        	
        }        
        
        // set choice group selected index
        cg.setSelectedIndex(app.getConfig().getProjection(), true);
        
        
        // add dynamic location choices to form
        this.append(cg);

        // create and add command to confirm location
        this.addCommand(new Command("Confirm", Command.ITEM, 2));	            
        this.setCommandListener(this);		
	}
	
	/**
	 * This method is called when the location is confirmed.
	 * The location is translated and applied to the current configuration
	 */
	public void commandAction(Command c, Displayable d){
		System.out.println("called" + cg.getSelectedIndex());
		
		// collect selected location values and update configuration
		int index = cg.getSelectedIndex();
		app.getConfig().setProjection(index);		
		
		// update user message
		SetupMenu sm  = (SetupMenu)app.getScreen(2);
		sm.updateUsrMsg("Pprojection updated successfully.");			

		// projection updated, return to options menu...
		app.updateScreen(2);

	}	
}
