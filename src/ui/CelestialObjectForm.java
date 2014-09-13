package ui;
/**
 * This class displays information regarding a specific CelestialObject e.g. a star.. a planet..
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import javax.microedition.lcdui.*;
import system.Application;
import system.Config;
import objects.*;

public class CelestialObjectForm extends Form implements CommandListener {
	
	// reference to object
	CelestialObject co = null;
	
	// remote data
	String data = null;
	
	// calling application
	Application app;

	/**
	 * New Form showing celestial object details
	 * @param app - calling application
	 * @param c - constellation of interest array index
	 */
	public CelestialObjectForm(Application app, CelestialObject co) {
		// call super class with application name
		super(Config.APP_NAME);
		this.app = app;
		this.co = co;

		// add object information to form
		if(!co.getName().equals("")) {
			this.append("NAME: " + co.getName());
		}
		// format alt/az
		int falt = new Double(co.getAltitude()).intValue();
		int faz  =  new Double(co.getAzimuth()).intValue();
		this.append("\nALTITUDE: " +falt+ "°\nAZIMUTH: " +faz+ "°\n");
		
		// add object specific information
		if(co instanceof Star) { 
			Star s = (Star)co;
			this.append("DECLINATION: " + s.getDEC() + "\nRIGHT ASCENSION: " + s.getRA());
			this.append("MAGNITUDE: " + s.getMag() + "\nSPECTRUM: " + s.getSpectrum());
			
			if(s.getBayer()!=-1) {
				this.append("\nBAYER DESIGNATION: " + Config.greekSymbol[s.getBayer()]);
			}
		}
        // create and add command to confirm location
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
