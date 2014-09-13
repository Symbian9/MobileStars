package ui;
/** 
 * This class represents the view of the night sky!
 * Using a projection, Celestial Objects and Constellations will be plotted to the display.
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.*;
import resources.*;
import datasets.*;
import system.Application;
import objects.*;

public class SkyView extends Canvas implements CommandListener  {
	
	// the sky views projection
	private Projection projection;
	
	// Which application called the sky view
	private Application application;
	
	// skyviews GPS
	private LocOrientation gps = null;
	
	// GPS rotation control lock
	private boolean GPSLock = false;
	
	// which constellation is currently selected?
	private int currentConst = -1;
	
	// which celestial object is currently selected?
	private int currentco    = -1;
	
	// verbose mode
	private boolean verbose = false;
	
	// current magnitude threshold for stars
	private int threshold    = 6; // everything shown at the star
	
	// UI commands
	Command cmExit, cmHelp, cmInfo1, cmInfo2, cmFind, cmGoto, cmDetails, cmCont;

	// temporary variables
	long result;
	int  visibleStars;

	/**
	 * Constructor of sky view which defines commands for this canvas
	 * @param application that called this class
	 */
	public SkyView(Application app) {
		
		// record calling application
		this.application = app;
		
		// selected projection - based on configuration!
		switch(app.getConfig().getProjection()) {
			case 0:
				projection = new EqualArea(this);
			break;	
			case 1:
				projection = new Stereographic(this);
			break;	
			case 2:
				projection = new EqualArea(this);
			break;				
		}		
		
		// create commands for the menu		
		cmHelp    = new Command("Help", Command.ITEM, 1);
		cmInfo2   = new Command("Object Info", Command.ITEM, 2);
		cmInfo1   = new Command("Constellation", Command.ITEM, 2);	
		cmFind    = new Command("Search", Command.ITEM, 2);
		cmDetails = new Command("My Details", Command.ITEM, 2);
		cmCont    = new Command("Controls", Command.ITEM, 2);
		cmExit    = new Command("Main Menu", Command.ITEM, 2);
		
		// add commands
		this.addCommand(cmExit);
		this.addCommand(cmHelp);
		this.addCommand(cmInfo1);
		this.addCommand(cmInfo2);
		this.addCommand(cmDetails);
		this.addCommand(cmCont);
		this.addCommand(cmFind);
		
		// assign command listener
		this.setCommandListener(this);
		
		// attempt GPS *
		try {
			new Thread((gps=new LocOrientation(this))).start();
		} catch (Exception e) {
			// no problem, but gps failed.
			System.out.println("GPS FAILED.");
		}
		
	}
	
	
	/**
	 * Increment the visible star counter by one
	 */
	public void incCounter() {
		application.incCounter();
	}	
	
	
	/**
	 * Key pressed on phone: perform appropriate action
	 */
	public void commandAction(Command c, Displayable s) {
	    if (c == cmExit) {
	        // nuke skyview and return to main menu
	    	application.nukeSkyView(); // kills all associated threads
	    	application.updateScreen(0);	    	
	    }
	    if(c == cmInfo1) {
	    	// constellation data
	    	if(currentConst!=-1) {
	    		application.showConstellationInfo(currentConst);
	    	}
	    }
	    if(c == cmInfo2) {
	    	// celestial object data
			if(currentco!=-1) {
				CelestialObject co = (CelestialObject)application.getVector().elementAt(currentco);
				application.showCelestialInfo(co);
			}	    		    	
	    }		    
	    if(c == cmHelp) {
	    	// show help form.
	    	application.updateForm(3);
	    }
	    if(c == cmFind) {
	    	// conduct a search of the celestial objects vector - utalizing zoom to object?
	    	application.updateForm(5);
	    }
	    if(c == cmDetails) {
	    	// show user details - location, date and time...
	    	application.showDetails();
	    }
	    if(c == cmCont) {
	    	// show controls image
	    	application.updateScreen(5);
	    }	    
	    
	}	
	
	/**
	 * The main Canvas paint method
	 */
	public void paint( Graphics g ) {
		
	    System.out.println("circle is: "+projection.getCircle());
		
		/** get visible star counter **/
		visibleStars = application.getCounter();
		
		/** black background **/
	    g.setColor(0, 0, 0);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    
	    /** Plot the sky using projection **/
	    projection.plotSkyGrid(g);
	    
	    /** Draw the sky grid **/
	    if(this.application.getConfig().getDisplayGrid()) {	    
	    	System.out.println("drawing grid!!");
	    	projection.drawGrid(g);
	    } else {
	    	System.out.println("NOT !!! drawing grid!!");
	    }
	    
	    /** Draw compass points **/
	    projection.drawCompass(g);	   
	    
	    /** plot & draw visible constellations **/
	    projection.plotConstellations(g);	    
	    
	    /** Plot visible stars **/
	    projection.plotObjects(g, application);
	    
	    /** Draw visible objects **/
	    projection.drawObjects(g, application);  

		/** display selected constellation name **/
		projection.writeConstellationName(g, this.currentConst);
		
		/** display selected celestial object **/
		if(this.currentco!=-1) {
			projection.writeObjectName(g, (CelestialObject)application.getVector().elementAt(currentco));
		}
		
		/** display current magnitude threshold **/
		projection.writeMagThreshold(g, threshold);
	}

	/** Handle user key presses for navigation of night sky **/
	public void keyPressed(int code) {
		if(verbose) {
			System.out.println("(" + code + ") key press!");
		}
		switch(code) {
			case KEY_NUM1:
				if(!this.GPSLock) {
					// adjust rotation by -5 (if not locked by GPS)
					projection.updateRotate(-5);
					repaint();
				}
			break;
			case KEY_NUM3:
				if(!this.GPSLock) {
					// adjust rotation by +5 (if not locked by GPS)
					projection.updateRotate(5);
					repaint();
				}
			break;
			case KEY_NUM5:
				// adjust zoom by 20
				projection.updateCircle(20);
				projection.resize(true);
				repaint();
			break;
			case KEY_NUM0:
				// adjust zoom by -20
				projection.updateCircle(-20);
				projection.resize(true);
				repaint();				
			break;	
			case -3:
				// move left
				projection.updateXOffset(10);
				repaint();				
			break;		
			case -4:
				// move right
				projection.updateXOffset(-10);
				repaint();				
			break;	
			case -1:
				// move up
				projection.updateYOffset(10);
				repaint();				
			break;
			case -2:
				// move down
				projection.updateYOffset(-10);
				repaint();				
			break;
			case KEY_NUM6:
				// navigate celestial objects: FORWARD
				nextObject();
				repaint();			
			break;
			case KEY_NUM4:
				// navigate celestial objectsn: BACK
				prevObject();
				repaint();						
			break;
			case KEY_NUM9:
				// navigate constellations: FORWARD
				nextConst();
				repaint();
			break;
			case KEY_NUM7:
				// navigate constellations: BACK
				prevConst();
				repaint();
			break;
			case KEY_STAR:
				// show less stars!
				threshold = Math.max(1, --threshold);
				repaint();
			break;
			case KEY_POUND:
				System.out.println(KEY_POUND + " pressed!");
				// show more stars!
				threshold = Math.min(6, ++threshold);
				repaint();
			break;
		}		 
	}	
	
	/**
	 * Highlight the next celestial object
	 */
	public void nextObject() {
		// unhighlight current selection, if any
		if(currentco!=-1) {
			CelestialObject co = (CelestialObject)application.getVector().elementAt(currentco);
			co.highlight(false);
		}
		// reset current to zero if above max
		if(currentco>=application.getVector().size()-1) {
			currentco = 0;
		} else {
			currentco++;
		}

		CelestialObject co = (CelestialObject)application.getVector().elementAt(currentco);
		if(!co.getName().equals("")&&co.isOnScreen()&&co.isVisible()) {
			co.highlight(true);
		} else {
			nextObject();
		}
	}
	
	/**
	 * Highlight the previous celestial object
	 */
	public void prevObject() {
		// unhighlight current selection, if any
		if(currentco!=-1) {
			CelestialObject co = (CelestialObject)application.getVector().elementAt(currentco);
			co.highlight(false);
		}
		// reset current to zero if above max
		if(currentco<=0) {
			currentco = application.getVector().size()-1;
		} else {
			currentco--;
		}

		CelestialObject co = (CelestialObject)application.getVector().elementAt(currentco);
		if(!co.getName().equals("")&&co.isOnScreen()&&co.isVisible()) {
			co.highlight(true);
		} else {
			prevObject();
		}	
	}
	
	/**
	 * Highlight the next visible constellation
	 */
	public void nextConst() {
		if(currentConst==-1) {
			currentConst = 0;
		}
		Constellations.consts[currentConst].highlight(false);
		currentConst = (currentConst+1)%(Constellations.consts.length-1);
		// is the next constellation is not visible, recursively call this function.
		if(Constellations.consts[currentConst].isVisible()) {
			Constellations.consts[currentConst].highlight(true);
		} else {
			nextConst();
		}
	}
	
	/**
	 * Highlight the previous visible constellation
	 */
	public void prevConst() {
		if(currentConst==-1) {
			currentConst = Constellations.consts.length-1;
		}		
		Constellations.consts[currentConst].highlight(false);
		System.out.println(currentConst);
		currentConst = (currentConst-1)%(Constellations.consts.length-1);
		if(currentConst<=0) currentConst = Constellations.consts.length-1;
		System.out.println(currentConst);
		// is the next constellation is not visible, recursively call this function.
		if(Constellations.consts[currentConst].isVisible()) {
			Constellations.consts[currentConst].highlight(true);
		} else {
			prevConst();
		}
	}	

	/** 
	 * Allow keys to be held down resulting in simulation of constant presses! 
	 **/
	public void keyRepeated(int keyCode) {
	   if (hasRepeatEvents()) {
	      keyPressed(keyCode);
	   }
	}	
	
	/**
	 * Get skyViews projection
	 * @return skyview Projection
	 */
	public Projection getProjection() {
		return this.projection;
	}
	
	/**
	 * Get current constellation
	 * @return
	 */
	public int getCurrentConst() {
		return this.currentConst;
	}
	
	/**
	 * Set current constellation
	 * @param c
	 */
	public void setCurrentConst(int c) {
		this.currentConst = c;
	}
	
	/**
	 * Get current Celestial Object
	 * @return
	 */
	public int getCurrentCo() {
		return this.currentco;
	}
	
	/**
	 * Set the current Celestial Object
	 * @param c
	 */
	public void setCurrentCo(int c) {
		this.currentco = c;
	}
	
	/**
	 * Get this skyviews application
	 */
	public Application getMyApp() {
		return this.application;
	}
	
	/**
	 * Get current magnitude threshold
	 * @return
	 */
	public int getThreshold() {
		return this.threshold;
	}
	
	/**
	 * Lock rotation controls for GPS
	 */
	public void lock4GPS() {
		this.GPSLock = true;
	}
	
	/**
	 * unlock rotation controls for GPS
	 */
	public void unlock4GPS() {
		this.GPSLock = false;
	}
		
	
	/**
	 * Update view orientation (GPS only)
	 * @param newAzimuth
	 */
	public void setAzimuth(int newAzimuth) {
		this.projection.setRotate(newAzimuth);
		repaint();
	}
	
	/**
	 * Stop the GPD thread
	 */
	public void killGPS() {
		if(this.gps!=null) {
			this.gps.stop();
			this.gps = null;
		}
	}
}
