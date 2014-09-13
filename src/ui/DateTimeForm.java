package ui;
/**
 * This form allows a user to update the system date and time.
 * Invalid dates will not be saved and will result in an Alert error message.
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class DateTimeForm extends Form implements CommandListener {
	
	// Textfield variables
	private TextField tb_day, tb_month, tb_year, tb_hours, tb_mins;
	
	// calling application
	private Application app;
	
	// define commands
	private Command cmOK;
	private Command cmBACK;		

	/**
	 * Public SetupForm constructor to create the new form, adding required fields for user to select.
	 * @param app - calling application
	 */
	public DateTimeForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		this.append("Please update the system date / time");           
        
		// display current date for updating	
		this.append("Current Date: DD/MM-YYYY");
		// display date text fields
		tb_day   = new TextField("", ""+this.app.getConfig().getDay(false), 2, TextField.DECIMAL);
		this.append(tb_day);
		tb_month = new TextField("", ""+this.app.getConfig().getMonth(false), 2, TextField.DECIMAL);
		this.append(tb_month);
		tb_year  = new TextField("", ""+this.app.getConfig().getYear(false), 4, TextField.DECIMAL);
		this.append(tb_year);				
		this.append("Current Time: Hours/Minutes");
		// display time text fields
		tb_hours  = new TextField("", ""+this.app.getConfig().getHour(false), 2, TextField.DECIMAL);
		this.append(tb_hours);
		tb_mins  = new TextField("", ""+this.app.getConfig().getMin(false), 2, TextField.DECIMAL);
		this.append(tb_mins);

        // create and add command to confirm location
		cmOK    = new Command("OK", Command.ITEM, 0);
		cmBACK  = new Command("Back", Command.ITEM, 1);   
		
		// add commands
		this.addCommand(cmOK);
		this.addCommand(cmBACK);
		
		// set command listener
        this.setCommandListener(this);		
	}

	
	
	/**
	 * This method is called when the colours are confirmed
	 */
	public void commandAction(Command c, Displayable d){
		if(c==this.cmOK) {
			// check for errors
			boolean errors = false;
			
			// collect form parameters
			int year = Integer.parseInt(tb_year.getString());
			int month = Integer.parseInt(tb_month.getString());
			int day = Integer.parseInt(tb_day.getString());
			int hours = Integer.parseInt(tb_hours.getString());
			int mins = Integer.parseInt(tb_mins.getString());
			
			// filter errors
			if(year<1) {
				errors = true;
			}
			if(month<1||month>12) {
				errors = true;
			}
			if(day<1||day>31) {
				errors = true;
			}
			if(hours<0||hours>24) {
				errors = true;
			}
			if(mins<0||mins>59) {
				errors = true;
			}		
			
			if(!errors) {
				// save new date / time (if not errors)
				this.app.getConfig().setDateTime(year, 
											     month, 
											     day, 
											     hours, 
											     mins);
				
				// update user message
				SetupMenu sm  = (SetupMenu)app.getScreen(2);
				sm.updateUsrMsg("Date/time updated successfully.");		
				
				// projection updated, return to options menu...
				app.updateScreen(2);
			} else {
				// show alert for errors
				Alert alert = new Alert("Invalid Date/Time Entered", "Please supply a valid date and time!", null, AlertType.INFO);	
				this.app.getDisplay().setCurrent(alert);			
			}
		} else {
			// go back
			app.updateScreen(2);
		}
	}	
}
