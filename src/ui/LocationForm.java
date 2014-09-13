package ui;
/**
 * This form collects a users location for the available places.
 * Places are also loaded from a resource file through the getPlaces() method
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import java.io.InputStream;
import javax.microedition.lcdui.*;
import objects.Place;
import system.Application;
import system.Config;

public class LocationForm extends Form implements CommandListener {
	
	// define possible location array
	private Place[] places = new Place[120];
	
	// choice group holding the places
	private ChoiceGroup cg;
	
	// calling application
	private Application app;
	
	// should this form skip to skyview?
	private boolean skip = false;
	
	// define commands
	private Command cmOK;
	private Command cmBACK;		
	
	/**
	 * Make this form skip to sky view
	 */
	public void skipIt() {
		this.skip = true;
	}	

	/**
	 * Public SetupForm constructor to create the new form, adding required fields for user to select.
	 * @param app - calling application
	 */
	public LocationForm(Application app) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		//his.append("Welcome first time user!");
		this.append("To see a view of your night sky we must determine your location. Please select the location nearest to you from the following list then press 'confirm'.");           
                    
        // dynamically load countries from text file resource (loading bar?)  
        getPlaces();            
        cg=new ChoiceGroup("Location:",Choice.EXCLUSIVE);
 
        for(int i=0; i<places.length;  i++) {
        	cg.append(places[i].getCountry() + ", " + places[i].getCity(),null);        	
        }        
        
        // add dynamic location choices to form
        this.append(cg);
        
        // set the selected index
        cg.setSelectedIndex(app.getConfig().getLoc()+1, true);

    	// add commands
		cmOK    = new Command("OK", Command.ITEM, 0);
		cmBACK  = new Command("Back", Command.ITEM, 1);
		
		// add commands
		this.addCommand(cmOK);
		this.addCommand(cmBACK);
		
		// assign command listener
		this.setCommandListener(this);		
	}
	
	/**
	 * Retrieve places from resource file.
	 */
	public void getPlaces() {
		try {
			// determine the size of the file
			int dsize = 0;
			InputStream is = getClass().getResourceAsStream("/places.txt");
			byte b = (byte)is.read();
			while (b != -1) {
			dsize ++;
			b = (byte)is.read();
			}
			is.close();
	
			// create byte array to hold the file contents
			byte buf[] = new byte[dsize];
	
			// read the contents into the byte array
			is = getClass().getResourceAsStream("/places.txt");
			is.read(buf);
			is.close();
	
			// convert the array into string
			String mytext = new String(buf);
			String thisLine;
			
			int index4 = 0, index5 = 0, counter = 0;
			while (index5!=-1&&index4!=-1) {
				index5 = mytext.indexOf('\n', index4);
				
				if (index5 == -1) {
					// add the last line to vector
					thisLine = mytext.substring(index4);
				} else {
					// add every line to vector
					thisLine = mytext.substring(index4, index5);
				}

				// collect place attributes and add to local array
				try { 				
					// country
					String country = thisLine.substring(0, thisLine.indexOf(","));
						thisLine = thisLine.substring(thisLine.indexOf(",")+1, thisLine.length());
					// city
					String city    = thisLine.substring(0, thisLine.indexOf(","));
						thisLine = thisLine.substring(thisLine.indexOf(",")+1, thisLine.length());
					// longitude
					double lon     = Double.parseDouble(thisLine.substring(0, thisLine.indexOf(",")));
						thisLine = thisLine.substring(thisLine.indexOf(",")+1, thisLine.length());
					// latitude
					double lat     = Double.parseDouble(thisLine.substring(0, thisLine.length()));
					// add new place
					places[counter++] = new Place(country, city, lon, lat);
				} catch (Exception e) {
					// failed to load configuration - this is expected on first load.
					System.out.println(e); 
				}				
				
				// continue..
				index4 = index5 + 1;
			}			
			
		} catch (Exception e) {
			// failed to load configuration file
			System.out.println("failed." +e);
		}
		
		System.out.println("done :)");
	}
	
	
	/**
	 * This method is called when the location is confirmed.
	 * The location is translated and applied to the current configuration
	 */
	public void commandAction(Command c, Displayable s) {
	    if (c == cmOK) {
	    	// collect selected location values and update configuration
			int index = cg.getSelectedIndex();
			app.getConfig().setLoc(index);
			app.getConfig().setCountry(places[index].getCountry());
			app.getConfig().setTown(places[index].getCity());
			app.getConfig().setLat(places[index].getLatitude());
			app.getConfig().setLong(places[index].getLongitude());
			
			System.out.println("lat: " +places[index].getLatitude() + " long: " + places[index].getLongitude());
			System.out.println("lat: " + app.getConfig().getLat() + " long: " + app.getConfig().getLong());
			
			// configuration now OK
			app.getConfig().configNowOK();
			
			// return to setup menu with new user message
			SetupMenu sm  = (SetupMenu)app.getScreen(2);
			sm.updateUsrMsg("location updated successfully.");	

			if(skip) {
				// start application: load sky view
				app.startNow();		
			} else {
				// return to main menu
				app.updateScreen(2);
			}
	    }
	    if(c == cmBACK) {
			// return to main menu
			this.app.updateScreen(0);
	    }	
		
	}	
}
