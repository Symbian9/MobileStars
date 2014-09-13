package ui;
/**
 * This form allows a user to specify a new location in terms of latitude and longitude
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;

public class LongLatForm extends Form implements CommandListener {
	
	// Textfield variables
	private TextField tb_long, tb_lat, country, area;
	
	// calling application
	private Application app;
	
	// define commands
	private Command cmOK;
	private Command cmBACK;		
	
	/**
	 * Public LongLatForm constructor to create the new form allowing for manual entry of long/lat details
	 * @param app - calling application
	 */
	public LongLatForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		this.append("Please specify a new location.\n");           
        

		// country
		this.append("Country name:");
		country  = new TextField("", ""+this.app.getConfig().getCountry(), 40, TextField.ANY);
		this.append(country);		
		
		// area
		this.append("Town/City Name:");
		area  = new TextField("", ""+this.app.getConfig().getTown(), 40, TextField.ANY);
		this.append(area);	
		
		// longitude
		this.append("Longitude: (east is represented by a negative number)");
		tb_long   = new TextField("", ""+this.app.getConfig().getLong(), 8, TextField.DECIMAL);
		this.append(tb_long);
				
		// latitude
		this.append("Latitude:");
		tb_lat   = new TextField("", ""+this.app.getConfig().getLat(), 8, TextField.DECIMAL);
		this.append(tb_lat);

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
			double longitude = Double.parseDouble(tb_long.getString());
			double latitude = Double.parseDouble(tb_lat.getString());

			// filter errors			
				// latitude : -90 to +90.
				if(latitude<-90||latitude>90) {
					errors = true;
				}			
				// longitude : -180 to +180
				if(longitude<-180||longitude>180) {
					errors = true;
				}

			
			if(!errors) {
				// save new location details
				app.getConfig().setLoc(-1); // no relation to places file
				app.getConfig().setCountry(country.getString());
				app.getConfig().setTown(area.getString());
				app.getConfig().setLat(latitude);
				app.getConfig().setLong(longitude);
				
				// configuration now OK
				app.getConfig().configNowOK();
				
				// update user message
				SetupMenu sm  = (SetupMenu)app.getScreen(2);
				sm.updateUsrMsg("Location updated successfully.");		
				
				// location updated, return to options menu...
				app.updateScreen(2);
			} else {
				// show alert for errors
				Alert alert = new Alert("Invalid Long/Lat", "Please supply a valid longitude and latitude!", null, AlertType.INFO);	
				this.app.getDisplay().setCurrent(alert);			
			}
		} else {
			// go back
			app.updateScreen(2);
		}
	}	
}
