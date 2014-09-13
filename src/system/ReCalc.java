package system;
/**
 * Responsible for triggering a recalculation of the celestial objects shown on the skyview!
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
public class ReCalc implements Runnable {
	
	// private variables
	private Application app;
	private int interval;
	private long start;
	private boolean running = true;
	private boolean verbose = false;
	
	/**
	 * Create a new Recalculation object
	 * @param Application - calling application
	 * @param Interval - interval in Minutes!
	 */
	public ReCalc(Application app, int interval) {
		if(verbose) {
			System.out.println("RECALC: loaded!");
		}
		this.app = app;
		this.interval = interval;
		this.start = System.currentTimeMillis();
	}
	
	/**
	 * End the recalculation thread
	 */
	public void stop() {
		this.running = false;
	}
	
	/**
	 * Update at the correct interval
	 */
	public void run() {
		while(running) {
			long current = System.currentTimeMillis();
			long diff = ((current - start)/1000)/60;
			if(verbose) {
				System.out.println("RECALC: " + diff);
			}
			
			if(diff>=interval) {
				// reload night sky. essentially a stop / start
				this.app.reCalc();
				running = false;
			}
		
			// sleep for one minute
			try {
				if(verbose) {
					System.out.println("RECALC: sleeping for one minute.");
				}
				Thread.sleep(60000);
			} catch (Exception e) {
				// failed to sleep
			}
			
		}
	}

}
