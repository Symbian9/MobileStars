package ui;
/**
 * This form will collect and save general user settings, including
 * show alt/az grid, show atmosphere and show bayer designations
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class SettingsForm extends Form implements CommandListener {
	// choice groups holding the options
	private ChoiceGroup dg,bd,atmos;
	
	// Textfield variables
	private TextField reCalc;
	
	// calling application
	private Application app;

		/**
		 * Public SetupForm constructor to create the new form, adding required fields for user to select.
		 * @param app - calling application
		 */
		public SettingsForm(Application app) {
			// call super class with application name
			super(Config.APP_NAME);
			this.app = app;
			this.append("Please select your preferred settings:");           
	        
			// display the grid?
			dg=new ChoiceGroup("Display Grid:",Choice.EXCLUSIVE);
			dg.append("yes", null);
			dg.append("no", null);
	        // set choice group selected index
			if(app.getConfig().getDisplayGrid()) {
				dg.setSelectedIndex(0, true);
			} else {
				dg.setSelectedIndex(1, true);
			}
			this.append(dg);
			
			// show bayer designations?
			bd=new ChoiceGroup("Bayer Designations:",Choice.EXCLUSIVE);
			bd.append("yes", null);
			bd.append("no", null);
	        // set choice group selected index
			if(app.getConfig().getBayer()) {
				bd.setSelectedIndex(0, true);
			} else {
				bd.setSelectedIndex(1, true);
			}
			this.append(bd);
			
			// show atmosphere
			atmos=new ChoiceGroup("Atmosphere:",Choice.EXCLUSIVE);
			atmos.append("yes", null);
			atmos.append("no", null);
	        // set choice group selected index
			if(app.getConfig().getAtmos()) {
				atmos.setSelectedIndex(0, true);
			} else {
				atmos.setSelectedIndex(1, true);
			}
			this.append(atmos);
			
			// recalculation period
			this.append("Recalculation period:");
			reCalc  = new TextField("", ""+this.app.getConfig().getReCalc(), 2, TextField.DECIMAL);
			this.append(reCalc);	

	        // create and add command to confirm location
	        this.addCommand(new Command("Confirm", Command.ITEM, 2));	            
	        this.setCommandListener(this);		
		}

		
		
		/**
		 * This method is called when the colours are confirmed
		 */
		public void commandAction(Command c, Displayable d){
			
			// save settings
			app.getConfig().setReCalc(Integer.parseInt(reCalc.getString()));
			
			// set display grid
			boolean grid = false;
			if(dg.getSelectedIndex()==0) {
				grid = true;
			}
			app.getConfig().setDisplayGrid(grid);
			
			// set bayer designations
			boolean bayer = false;
			if(bd.getSelectedIndex()==0) {
				bayer = true;
			}
			app.getConfig().setBayer(bayer);
			
			// set atmosphere
			boolean at = false;
			if(atmos.getSelectedIndex()==0) {
				at = true;
			}
			app.getConfig().setAtmos(at);

			// update user message
			SetupMenu sm  = (SetupMenu)app.getScreen(2);
			sm.updateUsrMsg("settings updated successfully.");		
			
			// settings updated, return to options menu...
			app.updateScreen(2);	
		}	
}
