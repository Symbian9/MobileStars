package resources;
/**
 * This class is used to deal with all Location->Orientation activity
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
// location api
import javax.microedition.location.*;

import ui.SkyView;

public class LocOrientation implements Runnable {
	
	// Controlling SkyView
	private SkyView sv;
	
	// verbose mode
	private boolean verbose = true;

	// thread running?
	private boolean running = true;
	
	// current orientation
	private Orientation orientation;
	
	/**
	 * Create a new GPD object
	 */
	public LocOrientation(SkyView sv) throws Exception {
		// assign skyview
		this.sv = sv;

	}

	
	/**
	 * Stop the GPS thread of execution
	 */
	public void stop() {
		this.running = false;
	}
	
	/**
	 * Manage location activities (specifically rotation of device)
	 * http://en.wikipedia.org/wiki/Azimuth
	 */
	public void run() {
		
		while(running) {

			try {
				Thread.sleep(1000);
			} catch (Exception e ){
				// failed to sleep
			}
			
			if(verbose) {
				System.out.println("checking device azimuth...");
			}
			
			try {
				// lock rotation controls on skyview
				this.sv.lock4GPS();	
				orientation = Orientation.getOrientation();
				// update projection orientation
				float az = orientation.getCompassAzimuth();
				this.sv.setAzimuth((int)az);				
			} catch (Exception e) {
				// failed to update orientation, ignored.
				System.out.println("Orientation failed and releasing the lock.");
				this.sv.unlock4GPS();
				this.running = false;
			} catch (java.lang.Error e1) {
				// failed to update orientation, not present.
				System.out.println("Orientation failed and releasing the lock.");
				this.sv.unlock4GPS();
				this.running = false;
			}

			
		}
	}

}
