package system;
/**
 * The Main Application, responsible for starting the main menu and 
 * controlling the flow of control to and from the skyview.
 * This class also controls the main CelestialObject vector.
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import java.util.Vector;
import ui.*;
import objects.*;
import resources.Projection;
import resources.XMLElement;
import resources.HTTPrequester;
import datasets.Constellations;

public class Application extends MIDlet {
	
	// define the configuration
	private Config config = new Config(this);
	
	// define vector of celestial objects
	private Vector celestialObjects = new Vector();
	
	// define vector for application updates
	private Vector celestialObjectsUpdate = new Vector();
	
	// define the sky view
	private SkyView skyview;
	
	// define application loader
	private AppLoader loader;
	
	// define recalculation
	private ReCalc recalc;
	
	// define setup form
	LocationForm setupForm;
	
	// define the first load
	private boolean firstLoad = false;
	
	// define screens
	private Screen[] screens = {
		new MainMenu(this),
		new Loading(this),
		new SetupMenu(this),
		new UpdatesMenu(this),
		new Updating(this),
		new Controls(this)
	};
	
	// define forms
	private Form[] forms = {
		new LocationForm(this),
		new ProjectionForm(this),
		new ColourForm(this),
		new HelpForm(this),
		new SettingsForm(this),
		new SearchForm(this),
		new AboutForm(this),
		new LongLatForm(this)
	};
	
	// current screen, starts at the main menu
	private int current_screen = 1;
	
	// device screen
	private Display phoneDisplay;
	
	// number of visible stars
	int visibleStars=0;	
	
	// innerclass commands (news/weather)
	private Command cmBACK, cmBACK1, cmOK;		
	
	// innerclass control variables
	private String[] links;
	private List newsList;
	private Application application=this;
	
	/** The constructor class for the application **/
	public Application() {	
		
		// get the current display
		phoneDisplay = Display.getDisplay(this);
		
		// show the main menu
		updateScreen(0);
	}
	
	/**
	 * continue when the data is loaded
	 */
	public void loadingComplete() {
		// show the night sky!
		updateScreen(-1);
	} 
	
	/**
	 * Start the recalculation thread
	 */
	public void startReCalc() {
		recalc = new ReCalc(this, config.getReCalc());
		new Thread(recalc).start();	
	}
	
	/**
	 * Reload the skyview with current settings!
	 */
	public void reCalc() {
		
		// save skyView state
		Projection p = this.skyview.getProjection();
		int circle = p.getCircle();
		int xOffSet = p.getXOffSet();
		int yOffSet = p.getYOffSet();
		int rotate = p.getRotate();
		int cnum = p.getcNum();
		int cconst = this.skyview.getCurrentConst();
		int cco = this.skyview.getCurrentCo();
		

			// kill skyview
			this.nukeSkyView();
			// kill recalc
			this.recalc = null;
			// update time
			this.config.incTime();
			// start again 
			this.startNow();
			
		
		// load skyView state
		p = this.skyview.getProjection();
		p.setCircle(circle);
		p.setXOffSet(xOffSet);
		p.setYOffSet(yOffSet);
		p.setRotate(rotate);
		p.setcNum(cnum);
		this.skyview.setCurrentConst(cconst);
		this.skyview.setCurrentCo(cco);
		
	}
	
	/**
	 * Stop the recalculation thread
	 */
	public void stopReCalc() {
		this.recalc.stop();
	}
	
	/**
	 * Start the main part of the application : Show Sky
	 */
	public void startNow() {
		
		// check configuration for first time usage
		if(config.configOK()) { // CONFIG OK - show the night sky for given location
	
			// perform garbage collection
			System.gc(); 		
			// reset the main objects vector!
			celestialObjects = new Vector();
			// new Loading screen
			screens[1] = new Loading(this);
			// reset star loader to help garbage collection
			loader = null;
			// create a new sky view
			skyview = new SkyView(this);	
			System.out.println("* new skyview created.");
			// set loading screen as current
			updateScreen(current_screen=1);
			// load star data via the StarLoader Thread
			loader = new AppLoader(this);
			new Thread(loader).start();	
			System.out.println("* application loading thread activated!");
			
		} else {
			
			// make sure that the location form knows to skip to skyview
			LocationForm lf = (LocationForm)forms[0];
			lf.skipIt();
			// no configuration found. 
			// set setup form as current display to collect preferences
			updateForm(0);
			
		}	
		
	}
	
	/**
	 * Perform application location setup
	 */
	public void doSetup() {
	   setupForm = new LocationForm(this);
       phoneDisplay.setCurrent(setupForm);	
      
	}
	
	/**
	 * Create a new date/time form
	 */
	public void doDateTime() {
		DateTimeForm dtf = new DateTimeForm(this);
		 phoneDisplay.setCurrent(dtf);	
	}
	
	/**
	 * setup by showing the setup menu
	 */
	public void setupMenu() {
		 updateScreen(2);	
	}
		
	/**
	 * Increment the visible star counter by one
	 */
	public void incCounter() {
		visibleStars++;
	}		 
 
	
	/**
	 * return the counter
	 * @return the counter value
	 */
	public int getCounter() {
		return visibleStars;
		
	}
	
	/**
	 * Update the current screen
	 * @param screenIndex
	 */
	public void updateScreen(int screenIndex) {
		System.out.println(screenIndex);
		current_screen = screenIndex;
		if(current_screen==-1) {
			// the sky view screen
			phoneDisplay.setCurrent(skyview);
		} else {
			// a normal screen
			phoneDisplay.setCurrent(screens[current_screen]);
		}
	}
	
	/**
	 * Update current form to display
	 * @param formIndex
	 */
	public void updateForm(int formIndex) {
		phoneDisplay.setCurrent(forms[formIndex]);
	}
	
	
	/**
	 * Update the progress of the loading screen
	 * @param progress
	 */
	public void updateLoadingScreen(int progress) {
		((Loading)screens[1]).updateProgress(progress);
	}
	
	
	/**
	 * Create a News form to display remote articles
	 * @param v - containing news elements
	 */
	public void listNews(Vector v) {
		
		String[] titles = new String[v.size()/3];
		         links  = new String[v.size()/3];
		int j = 0;
		
		// build news list items
		for(int i=0; i<v.size(); i+=3) {
			XMLElement name = (XMLElement)v.elementAt(i);
			XMLElement description = (XMLElement)v.elementAt(i+1);
			XMLElement link = (XMLElement)v.elementAt(i+2);
			titles[j] = name.getValue() + ":\n" + description.getValue(); // + "\n" + link.getValue();
			links[j]  = link.getValue();
			System.out.println(j +" : "+titles[j++]);
		}
		// create new list 
		newsList = new List("News Articles", List.IMPLICIT, titles, null);
        // create and add command to confirm location
		cmOK   = new Command("OK", Command.BACK, 2);
		cmBACK = new Command("Back", Command.SCREEN, 1);
		newsList.addCommand(cmOK);
		newsList.addCommand(cmBACK);
		
		// new inner class command listener
		newsList.setCommandListener(new CommandListener() {
			  // inner class to handle the user inputs
		      public void commandAction(Command c, Displayable d) {
		    	  System.out.println("Action performed! " + c);
		    	  if(c==cmBACK) {
		    		  // go back: return to the updates menu
		    		  System.out.println("Please go back!!!!");
		    		  application.updateScreen(3);
		    	  } else {
		    		  // get selected index
		    		  int si = newsList.getSelectedIndex();
		    		  // show updating screen
		    		  application.updateScreen(4);
		    		  // show selected news item via HTTP request
		    		  new Thread(new HTTPrequester(3, Integer.parseInt(links[si]), (Updating)screens[4], application)).start();    		  
		    	  }
		      }

		   });
		// set current display as the news list
		getDisplay().setCurrent(newsList);
	}
	
	/**
	 * Show a news article
	 * @param v - the article to display
	 */
	public void showNews(Vector v) {
		// define new form
		String headline = ((XMLElement)v.elementAt(0)).getValue();
		String body = ((XMLElement)v.elementAt(1)).getValue();
		Form newsArticle = new Form(headline);
		newsArticle.append(body);
	     // create and add command to confirm location	
		newsArticle.addCommand(cmBACK1 = new Command("Back", Command.SCREEN, 2));
		// new inner class command listener
		newsArticle.setCommandListener(new CommandListener() {
			  // inner class to handle the user inputs
		      public void commandAction(Command c, Displayable d) {
		    	  if(c==cmBACK1) {
		    		  // go back: return to the news listing
		    		  getDisplay().setCurrent(newsList);
		    	  }	        
		      }

		   });		
		//show the news article
		getDisplay().setCurrent(newsArticle);		
	}
	
	/**
	 * Show the weather
	 * @param v - weather elements to display
	 */
	public void showWeather(Vector v) {
		// collect data
		String sunrise = ((XMLElement)v.elementAt(0)).getValue();
		String sunset  = ((XMLElement)v.elementAt(1)).getValue();
		String day1    = ((XMLElement)v.elementAt(2)).getValue();
		String day2    = ((XMLElement)v.elementAt(3)).getValue();
		String day3    = ((XMLElement)v.elementAt(4)).getValue();
		String day4    = ((XMLElement)v.elementAt(5)).getValue();
		String day5    = ((XMLElement)v.elementAt(6)).getValue();
	
		// define new form
		String headline = "Weather Info";
		String body = "Location: " + this.config.getTown() + "\nSunrise: " + sunrise + "\nSunset: " + sunset + "\nToday: " + day1 + "\nTomorrow: " + day2 + "\nDay3: " +day3+"\nDay4: "+day4+"\nDay5: "+day5;
		Form newsArticle = new Form(headline);
		newsArticle.append(body);
	     // create and add command to confirm location	
		newsArticle.addCommand(cmBACK1 = new Command("Back", Command.SCREEN, 2));
		// new inner class command listener
		newsArticle.setCommandListener(new CommandListener() {
			  // inner class to handle the user inputs
		      public void commandAction(Command c, Displayable d) {
		    	  if(c==cmBACK1) {
		    		  // go back: return to the updates menu
		    		  getDisplay().setCurrent(screens[3]);
		    	  }	        
		      }

		   });		
		//show the news article
		getDisplay().setCurrent(newsArticle);	
	}
	
	/**
	 * Get the configuration
	 * @return current configuration
	 */
	public Config getConfig() {
		return this.config;
	}
	
	/**
	 * Get the screen referenced by index
	 * @param index - screen to get
	 * @return - screen at index
	 */
	public Screen getScreen(int index) {
		return this.screens[index];
	}
	
	/**
	 * Get the form referenced by index
	 * @param index - form to get
	 * @return - form at index
	 */
	public Form getForm(int index) {
		return this.forms[index];
	}	
	
	/**
	 * Retrieve the Celestial Object Vector
	 * @return - celestial object vector.
	 */
	public Vector getVector() {
		return this.celestialObjects;
	}
	
	/**
	 * Retrieve the celestial object updates vector
	 * @return
	 */
	public Vector getUpdatesVector() {
		return this.celestialObjectsUpdate;
	}
	/**
	 * Show details regarding a specific constellation
	 * @param c - constellation index
	 */
	public void showConstellationInfo(int c) {
		phoneDisplay.setCurrent(new ConstellationForm(this, c));	
	}
	
	/**
	 * Show details regarding a specific Celestial Object
	 * @param co - celestial object
	 */
	public void showCelestialInfo(CelestialObject co) {
		phoneDisplay.setCurrent(new CelestialObjectForm(this, co));
	}

	/**
	 * Show current user details: location, time etc
	 */
	public void showDetails() {
		phoneDisplay.setCurrent(new DetailsForm(this));
	}
	
	/**
	 * Is this the first time the application has been loaded?
	 * @return true is this is the first time
	 */
	public boolean isFirstLoad() {
		return this.firstLoad;
	}
	
	/**
	 * Record that this is the first time the application has been loaded,
	 * triggered by a non-existant configuration file.
	 */
	public void setFirstLoad() {
		this.firstLoad = true;
	}
	 
	 
	/** Destroy the application **/
	public void destroyApp(boolean arg0) throws MIDletStateChangeException {
		System.out.println("destorying app");
		if(config.updated()) {
			// write current configuration if updated
			config.writeConfig();
		}
		
		// and destroy
	    notifyDestroyed();
	}

	/** 
	 * Pausing the application 
	**/
	public void pauseApp() {
	    notifyPaused();
	}

	/** Start the application **/
	public void startApp() throws MIDletStateChangeException {
		// nothing happens here.
		// main menu is always presented to the user.
	}	
	
	/**
	 * Destroy the sky view to free system resources!
	 */
	public void nukeSkyView() {
		if(loader!=null) {
			// if available: stop the application loader
			this.loader.stop();
		}
		if(recalc!=null) {
			// if available: stop the recalculation thread
			this.recalc.stop();
		}
		// kill gps thread
		this.skyview.killGPS();
		// reset Co's : make sure all objects are currently not highlighted or visible
		for(int i=0; i<celestialObjects.size(); i++) {
			CelestialObject c = (CelestialObject)celestialObjects.elementAt(i);
			c.highlight(false);
			c.setVisible(false);			
		}
		// reset sky view
		this.skyview = null;
	}
	
	/**
	 * Update the skyview by repainting it
	 */
	public void updateSkyView() {
		this.skyview.repaint();
	}
	
	/**
	 * Collect the display
	 * @return
	 */
	public Display getDisplay() {
		return this.phoneDisplay;
	}
	
	/**
	 * Conduct a system search, return a vector of available objects.
	 * @param sterm - search term
	 * @param stype - search type (0==constellations, 1==objects)
	 * @return Vector containing search results
	 */
	public Vector conductSearch(String sterm, int stype) {
		// results vector
		Vector results=new Vector();
		// force lower case
		sterm = sterm.toLowerCase();
		switch(stype) {
			case 0:
				// constellations
				for(int i=0; i<Constellations.consts.length; i++) {
					if(Constellations.consts[i].getFullName().toLowerCase().equals(sterm)&&Constellations.consts[i].isVisible()) {
						// unhighlight if required
						if(this.skyview.getCurrentConst()!=-1) {
							Constellations.consts[this.skyview.getCurrentConst()].highlight(false);
						}
						// highlight constellation and return
						Constellations.consts[i].highlight(true);
						this.skyview.setCurrentConst(i);
						// break from i loop
						results.addElement(Constellations.consts[i]);
						i=Constellations.consts.length+1;
					}
				}
			break;
			case 1:
				// celestial objects
				for(int i=0; i<this.celestialObjects.size(); i++) {
					CelestialObject co = (CelestialObject)this.celestialObjects.elementAt(i);
					if(co.getName().toLowerCase().equals(sterm)&&co.isVisible()) {
						// highlight Celestial object
						co.highlight(true);
						// unhighlight selected co if required
						if(this.skyview.getCurrentCo()!=-1) {
							co = (CelestialObject)this.celestialObjects.elementAt(this.skyview.getCurrentCo());
							co.highlight(false);
						}
						this.skyview.setCurrentCo(i);
						results.addElement(co);
						// break from i loop
						i=this.celestialObjects.size()+1;
					}					
				}				
			break;
		}
		// return something or nothing.
		return results;
	}

}
