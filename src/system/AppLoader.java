package system;
/**
 * This class loads all the stars from the star resource file and calculates there current 
 * position (altitude and azimuth), alerting Application when it has completed
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import java.io.*;
import resources.Calculations;
import ui.Loading;
import java.util.Vector;
import datasets.*;
import objects.*;

public class AppLoader implements Runnable  {
	
	// continue loading?
	private boolean continueLoading = true;
	
	// total counter for progress of loading
	private int trueCounter = 0;
	
	// constellation index, for adding constellation lines
	private String constellationRef = "AND"; // starting constellation
	private int constIndex = 0;
	private Vector lineStash = new Vector();
	
	// record calling application
	private Application application;
	
	// Calculation reference
	private Calculations calc;

	/**
	 * Record calling application,
	 * Work done within the run method.
	 */
	public AppLoader(Application ap) {				
		this.application=ap;		
	}
	
	
	/**
	 * Reads star data - taken and adapted from http://discussion.forum.nokia.com/forum/archive/index.php/t-60657.html
	 */
	public void run() {
		
		// create new calculation
		calc = new Calculations();		
		
		// create vector used to store lines from data files
		Vector s;
		
		// unhighlight all constellations
		for(int i=0; i<Constellations.consts.length; i++) {
			Constellations.consts[i].highlight(false);
		}
		
		// 1. load sun & moon
		Sun sun = new Sun();
		sun.calculatePosition(application, calc);	
		Moon moon = new Moon();
		moon.calculatePosition(application, calc);
		
		// 2. load planets	
		
			// Mercury
			Mercury mercury = new Mercury(sun);
			mercury.calculatePosition(application, calc);			
		 	// Venus
			Venus venus = new Venus(sun);
			venus.calculatePosition(application, calc);			
			// mars
			Mars mars = new Mars(sun);
			mars.calculatePosition(application, calc);			
			// Jupiter
			Jupiter jupiter = new Jupiter(sun);
			jupiter.calculatePosition(application, calc);			
			// Saturn
			Saturn saturn = new Saturn(sun);
			saturn.calculatePosition(application, calc);					
			// Uranus
			Uranus uranus = new Uranus(sun);
			uranus.calculatePosition(application, calc);			
			// Neptune
			Neptune neptune = new Neptune(sun);
			neptune.calculatePosition(application, calc);
			
			
			// add our galaxy items to main vector
			application.getVector().addElement(sun);
			application.getVector().addElement(moon);			
			application.getVector().addElement(mercury);
			application.getVector().addElement(venus);
			application.getVector().addElement(mars);
			application.getVector().addElement(jupiter);	
			application.getVector().addElement(saturn);
			application.getVector().addElement(uranus);	
			application.getVector().addElement(neptune);		 		

			
		// 2. load updates (if any)
		Vector v = this.application.getUpdatesVector();
		for(int i=0; i<v.size(); i++) {
			// set comet sun and calculate position
			Comet c = (Comet)v.elementAt(i);
			c.setSun(sun);
			c.calculatePosition(application, calc);
			if(c.isVisible()) {
				// only add visible comets.
				application.getVector().addElement(v.elementAt(i));
			}
		}
		
		// 5. load constellation lines
		s = getFile("/constlines.txt");
		System.out.println("++++" + s.size() + " / " + Config.TOTAL_OBJECTS);
		for(int i=0; i<s.size(); i++) {
			// add constellation lines to appropriate constellation
			String l = (String)s.elementAt(i);
			try {
				doLine(l);
				intLoadingCounter();
				if(i==s.size()-1) {
					System.out.println("lines check.");
				}					
			} catch (Exception e) {
				// ignore issue
			}		
		}
		
		// 6. load the brightest stars from static data
		for(int i=0; i<Stars.getNumStars(); i++) {
			Stars.stars[i].setVisible(false); // important (static stars wont become invisible!)
			Stars.stars[i].highlight(false); // important (static stars need to be unhighlighted!)
			// add star
			doStaticStar(i);
			intLoadingCounter();
			if(i==Stars.getNumStars()-1) {
				System.out.println("static stars check.");
			}
		}
		
		// 8a. Load star data file
		s = getFile("/stardata.txt");
		
		/**
		 TESTING FOR SHOWING GRID ALTITUDES
		s = new Vector();
		application.getVector().removeAllElements(); // no objects now.
		// add stars to help with sterographic grid
		for(int i=0; i<10; i++) {
			Star s1 = new Star(1, "test star", 0, 0, "B", 0, 0);
			s1.setAzimuth(180);
			s1.setAlt(i*10);
			s1.setVisible(true);
			s1.onScreen(true);
			s1.setColour(0x0FFFFFF);
			s1.setSize(6);
			application.getVector().addElement(s1);
			System.out.println(i+"star alt="+s1.getAltitude()+" azimuth="+s1.getAzimuth() +" mag:"+s1.getMag() );
		}
		System.out.println("VECTOR OBJECTS SIZE: " +application.getVector().size());
		**/
		
		// 9. get application to show skyview ( preventing file opening lag )
		//System.out.println("trying...");
		try {			
			Loading l = (Loading)application.getScreen(1);
			l.updateMessage("Loading Complete");
			application.loadingComplete();
			application.updateSkyView();
			//Thread.sleep(300);
		} catch (Exception e) {
			Loading l = (Loading)application.getScreen(1);
			l.updateMessage("FAILED " + e);
			System.out.println("failed to show skyview " +e);
		}		
		
		long startedAt = System.currentTimeMillis();
		// 10. load all star data (background loading...)
		for(int i=0; i<s.size(); i++) {
			// add star to main objects vector
			String l = (String)s.elementAt(i);			
			try { 
				doStar(l); 
				// update application display, every 800 stars
				if(i%1000==0) {
					long secs = (System.currentTimeMillis()-startedAt)/1000;
					System.out.println("UPDATED AT : " + i + " |||| " + secs + " seconds");
					application.updateSkyView();
					// rest a while
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// failed to sleep... no probs
					}
				}
			} catch (Exception e) { 
				System.out.println(e); 
			}	
			
			// test if we need to continue loading
			if(!continueLoading) {
				i = s.size();
			}
		}

		// Loading complete...
		System.out.println("everything is loaded. =)");
		
		// start recalculation thread
		this.application.startReCalc();		
	}
	
	/**
	 * Stop loading stars!
	 */
	public void stop() {
		this.continueLoading = false;
	}
	
	/**
	 * Increment the progress counter and update loading screen if required
	 */
	public void intLoadingCounter() {
		if(trueCounter%25==0) {
			double progress = trueCounter;
				   progress = progress/Config.TOTAL_OBJECTS;
				   progress = progress*100;
			int    progress1 = (int)progress;			
			// only visual update at specific intervals.
			application.updateLoadingScreen(progress1);
		}				
		trueCounter++;	
	}
	
	/**
	 * Add a static star to the main objects vector
	 */
	public void doStaticStar(int index) {
		// determine colour/size
		starColour(Stars.stars[index]);	
	    
		// calculate star position (alt/azi)
	    Stars.stars[index].calculatePosition(application, calc);
		
		// add star to vector if visible :)
		if(Stars.stars[index].isVisible()) {
			application.getVector().addElement(Stars.stars[index]);
		}	    
	}
	
	/**
	 * Detail with a constellation line
	 * @param l
	 */
	public void doLine(String l) throws Exception {

		// Format:  <constellation name>,<RA-from>,<Dec-from>,<RA-to>,<Dec-to>
		double RA_TO, DEC_TO, RA_FROM, DEC_FROM;	
		int al1,az1,al2,az2;
		String thisCon = "";
		String ls = l;
		
		// split up line to RA_TO, DEC_TO, RA_FROM, DEC_FROM
		thisCon =  ls.substring(0, ls.indexOf(','));
		ls = ls.substring(ls.indexOf(',')+1);
		
		// determine is this is a new constellation
		if(!thisCon.equals(constellationRef)) {			
			// add the linestash
			Constellations.consts[constIndex].setLines(lineStash);
			// reset linestash
			lineStash = new Vector();
			// increment constellation index
			constIndex++;
			// update string reference
			constellationRef = thisCon;
		}

		// collect vars from string line
		RA_FROM = Double.parseDouble(ls.substring(0, ls.indexOf(',')))/1000;
		ls = ls.substring(ls.indexOf(',')+1);
		
		DEC_FROM = Double.parseDouble(ls.substring(0, ls.indexOf(',')))/100;
		ls = ls.substring(ls.indexOf(',')+1);
		
		RA_TO = Double.parseDouble(ls.substring(0, ls.indexOf(',')))/1000;
		ls = ls.substring(ls.indexOf(',')+1);
		
		DEC_TO = Double.parseDouble(ls.substring(0, ls.length()-1))/100;
		
		// calculate alti/azim points from ra/dec's
		double[] r_from = calc.ra_dec_to_alt_azmiuth(RA_FROM*15, DEC_FROM, application);  // decimal RA hours to degrees
		double[] r_to   = calc.ra_dec_to_alt_azmiuth(RA_TO*15, DEC_TO, application);
		
		al1 = new Double(r_from[1]).intValue();
		az1 = new Double(r_from[0]).intValue();
		al2 = new Double(r_to[1]).intValue();
		az2 = new Double(r_to[0]).intValue();

		// create new line object
		Line al = new Line(al1, az1, al2, az2);
		
		// add line to the vector
		lineStash.addElement(al);					
	}
	
	/**
	 * Convert a string of star data to a star object
	 * @param s
	 */
	public void doStar(String s) {

		String ls=s;
		
		// get star ID
		int ID = Integer.parseInt(ls.substring(0, ls.indexOf('#')));
		ls = ls.substring(ls.indexOf('#')+1);
		
		// get text info/name, removing quotes ""
		//String info = ls.substring(1, ls.indexOf('#')-1);
		ls = ls.substring(ls.indexOf('#')+1);
		
		// get Right Ascension
		double RA = Double.parseDouble(ls.substring(0, ls.indexOf('#')));
		ls = ls.substring(ls.indexOf('#')+1);
		
		// get Declination
		double DEC = Double.parseDouble(ls.substring(0, ls.indexOf('#')));
		ls = ls.substring(ls.indexOf('#')+1);
		
		// get Spectrum
		String spectrum = ls.substring(1, ls.indexOf('#')-1);
		ls = ls.substring(ls.indexOf('#')+1);
		
		// get Magnitude
		int magnitude = Integer.parseInt(ls.substring(0, ls.indexOf('#')));
		ls = ls.substring(ls.indexOf('#')+1);
		
		// get Bayer Designation
		int bayer = Integer.parseInt(ls.substring(0, ls.indexOf('#')));
		ls = ls.substring(ls.indexOf('#')+1);
		
		
		
		// FORCE SIZE 4!!!!!!!!!!!!!!!!!
		//size=2;

		// create new star and assign colour/size
		Star s1 = new Star(ID, "", RA, DEC, spectrum, magnitude, bayer); // info removed
		starColour(s1);			

		// calculate star position (alt/azi)
		s1.calculatePosition(application, calc);
		
		// add star to vector if visible :)
		if(s1.isVisible()) {
			application.getVector().addElement(s1);
		}
		
		// Slow this down!
		try {
			Thread.sleep(1);
		} catch (Exception e) {
			// failed.
		}

	}
	
	/**
	 * Adjust a star colour based on current settings
	 * @return updated Star
	 * @param Star to update
	 */
	public Star starColour(Star s) {
	
		// magnitude & spectrum
		int magnitude = s.getMag();
		String spectrum = s.getSpectrum();
	
		// determine colour/size
		int colour = 0;	
		int size   = 0;
		
		// set colour and size of star according to brightness		
	    switch(magnitude+1)
	    {
	    	case 1:
	    		// white for the brightness
	    		colour = 0x0FFFFFF;
	    		size = 5; //6;
	    	break;
	    	case 2:
	    		colour = 0xf2f2f2;
	    		size = 4; //5;
	    	break;
	    	case 3:
	    		colour = 0xe1e1e1;
	    		size = 4; //4;
	    	break;
	    	case 4:
	    		colour = 0xafafaf;
	    		size = 3;
	    	break;
	    	case 5:
	    		colour = 0x575757;
	    		size = 2;
	    	break;
	    	case 6:
	    		colour = 0x05c5c5c;
	    		size = 1;
	    	break;	    	
	    	case 7:
	    		colour = 0x04d4d4d;
	    		size = 1;
	    	break;
	    }		
	    
	    
		
		// update colour depending on configuration		
		switch(application.getConfig().getColour()) {
			case 0:
				// WHITE - leave colours untouched.
	
			break;
			case 1:
				// COLOURS
	
				// make colour from spectrum Char when in colour mode
				if(spectrum.equals("O")) {
					// blue
					colour = 0x0016ed;
				} else if(spectrum.equals("B")) {
					// blue/white
					colour = 0xb8befc;
				} else if(spectrum.equals("A")) {
					// white
					colour = 0x0FFFFFF;
				} else if(spectrum.equals("F")) {
					// yellow/white
					colour = 0xfffcad;
				} else if(spectrum.equals("G")) {
					// yellow
					colour = 0xfff600;
				} else if(spectrum.equals("K")) {
					// orange
					colour = 0xffae00;
				} else if(spectrum.equals("M")) {
					// red
					colour = 0xe20000;
				}	
				
			break;
			case 2:
				// RED EYE MODE - all red
				colour = 0x810000;
			break;
		}
		
		// set colour and size
		s.setColour(colour);
		s.setSize(size);
		
		return s;
	
	}
	
	/**
	 * Read a file and return vector of string lines
	 * @param fileName
	 * @return vector of file lines
	 */
	public Vector getFile(String fileName) {
		
		// return vector
		Vector v = new Vector();
		// progress for star data
		int progress =97;
		
		try {
			// determine the size of the file
			int dsize = 0;
			InputStream is = getClass().getResourceAsStream(fileName);
			byte b = (byte)is.read();
			while (b != -1) {
				if(fileName.equals("/stardata.txt")&&dsize%20000==0) {
					// update progress for star data file...
					application.updateLoadingScreen(progress++);
				}
				dsize ++;
				b = (byte)is.read();
			}
			is.close();

			// create byte array to hold the file contents
			byte buf[] = new byte[dsize];		
	
			// read the contents into the byte array
			is = getClass().getResourceAsStream(fileName);
			is.read(buf);
			is.close();
			
			// convert the array into string
			String mytext = new String(buf);
			String thisLine;
			
			int index4 = 0, index5 = 0;
			
			while (index5!=-1&&index4!=-1) {
				index5 = mytext.indexOf('\n', index4);	
				if (index5 == -1) {
					// add the last line
					thisLine = mytext.substring(index4);
				} else {
					// add every line
					thisLine = mytext.substring(index4, index5);
				}
				
				// add line to the local array
				v.addElement(thisLine);	
				index4 = index5 + 1;
			}
			
			mytext   = null;
			thisLine = null;
		} catch (Exception e) {
			System.out.println("failed to load file ("+fileName+")." +e);
		}

		// return result
		return v;
	}	
}
