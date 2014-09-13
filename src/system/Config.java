package system;
/**
 * This class holds the users current configuration
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import java.util.*;
import javax.microedition.rms.RecordStore; 

public class Config {
	
	// define static application name
	public static final String APP_NAME = "wee-welkin v1.3";	
	
	// total number of stars
	public static int TOTAL_OBJECTS = 170+854; // +  854: LINES +  170: brightest stars
	public static int STARTING_STAR = 231; // polaris
	
	// verbose mode?
	private boolean verbose = true;
	
	// define possible projections
	public static String[] projections = {
		"Equal Area",
		"Sterographic" //,
		//"Horizon"
	};	
	
	// define possible colour options
	public static String[] colours = {
		"Normal",
		"Colours",
		"Red Eye Mode"
	};
	
	// define greek symbols for bayer designation
	public static String greekSymbol[] = 
	    {"Alp", "Bet", "Gam", "Del", "Eps", "Zet", "Eta", 
		 "The", "Iot", "Kap", "Lam", "Mu" , "Nu", "Xi", 
		 "Omi", "Pi", "Rho", "Sig", "Tau", "Ups", "Phi"
		 , "Chi", "Psi", "Ome"};
	
	// configuration variables
	private double longitude,latitude;
	private int loc=-1;
	private String country, town;	
	private Date datetime;
	private Calendar cal;
	private Calendar cal2; // timezone / daylight savings calendar
	private String yahooLocID;
	private int projection;
	private int colour=0;
	private int reCalc=10; // ten minutes (default) 
	
	// show the grid in the skyview?
	private boolean showGrid = true;	
	// show bayer designations
	private boolean showBayer = false;
	// show atmosphere
	private boolean atmos = true;
	// do we have a configuration to work with?
	private boolean configOK = false;
	// has the configuration been updated during this session?
	private boolean configUpdates = false;

	
	/**
	 * Configuration constructor
	 */
	public Config(Application app) {
		// create a new calendar
		datetime = new Date();
		cal      = Calendar.getInstance();
			
		// set the time
		cal.setTime(datetime);
		
		// set cal2 to be correctly adjusted
		cal2 = Calendar.getInstance();
		long timeOffset = cal.getTimeZone().getRawOffset();
		long DSTOffset  = 0;

		// Apply DST if used in this zone and currently exists
        if(cal.getTimeZone().useDaylightTime()) {
			DSTOffset = cal.getTimeZone().getOffset(1,
								                cal.get(Calendar.YEAR),
								                cal.get(Calendar.MONTH),
								                cal.get(Calendar.DAY_OF_MONTH),
								                cal.get(Calendar.DAY_OF_WEEK), 0);
			timeOffset+=DSTOffset;
        }

		


		// adjust cal2 by the complete offset
		datetime.setTime(cal.getTime().getTime()-timeOffset);
		cal2.setTime(datetime);
		
		if(verbose) {
			System.out.println("TIME: " + datetime.getTime());
			System.out.println("TIMEZONE: " + cal.getTimeZone().getID());
			System.out.println("TIMEZONE OFFSET: " + timeOffset);
			System.out.println("DST OFFSET: " + DSTOffset);
		}
		
		// set details to unknown
		longitude =  0;
		latitude  =  0;
		country   =  "";
		town      =  "";		
		
		// load configuration from record set
		try {
			// open Configuration record store
			RecordStore rs = RecordStore.openRecordStore("Config", false);
			byte[] rdata   = new byte[1]; 
			int len;
			// loop through each record
			for (int i = 1; i <= rs.getNumRecords(); i++){
				
		        if (rs.getRecordSize(i) > rdata.length) {
		        	rdata = new byte[rs.getRecordSize(i)];
		        }
		       
		        len = rs.getRecord(i, rdata, 0);
		        String data =  new String(rdata, 0, len);
		        if(verbose) {
			        System.out.println("Record #" + i + ": " + new String(rdata, 0, len));
			        System.out.println("------------------------------");
		        }

		        // get location index
		        if(data.substring(0, 1).equals("*")) {
		        	this.loc = Integer.parseInt(data.substring(1, data.length()));
		        }
		        // get country
		        if(data.substring(0, 1).equals("@")) {
		        	this.country = data.substring(1, data.length());
		        }
		        // get city
		        if(data.substring(0, 1).equals("#")) {
		        	this.town = data.substring(1, data.length());
		        }
		        // get longitude
		        if(data.substring(0, 1).equals("%")) {
		        	this.longitude = Double.parseDouble(data.substring(1, data.length()));
		        }
		        // get latitude
		        if(data.substring(0, 1).equals("$")) {
		        	this.latitude = Double.parseDouble(data.substring(1, data.length()));
		        }		 
		        // get projection
		        if(data.substring(0, 1).equals("&")) {
		        	this.projection = Integer.parseInt(data.substring(1, data.length()));
		        }
		        // get colour
		        if(data.substring(0, 1).equals("~")) {
		        	this.colour = Integer.parseInt(data.substring(1, data.length()));
		        }
		        // get showGrid
		        if(data.substring(0, 1).equals("£")) {
		        	if(data.substring(1, data.length()).toLowerCase().equals("true")) {
		        		this.showGrid = true;
		        	} else {
		        		this.showGrid = false;
		        	}
		        }	
		        // get recalculation period
		        if(data.substring(0, 1).equals("!")) {
		        	this.reCalc = Integer.parseInt(data.substring(1, data.length()));
		        }
		        // get Bayer option
		        if(data.substring(0, 1).equals("-")) {
		        	if(data.substring(1, data.length()).toLowerCase().equals("true")) {
		        		this.showBayer = true;
		        	} else {
		        		this.showBayer = false;
		        	}
		        }
		        // get Atmosphere option
		        if(data.substring(0, 1).equals("=")) {
		        	if(data.substring(1, data.length()).toLowerCase().equals("true")) {
		        		this.atmos = true;
		        	} else {
		        		this.atmos = false;
		        	}
		        }  
		        
		     }
			rs.closeRecordStore();
		} catch (Exception e) {
			// this is the first load.
			app.setFirstLoad();
			if(verbose) {
				System.out.println("(expected first load) failed to load config :(" +e);
			}
		}

		if(longitude==0) {
			// no configuration available
			configOK = false;
		} else {
			// configuration is OK :)
			configOK = true;
		}
				
		// date time is always loaded from the system - asummed correct - only able to amend at runtime
		datetime = new Date();
	}
	
	/**
	 * Write/Save the current configuration!
	 * J2ME in a nutshell page 213 
	 * & http://www.java2s.com/Code/Java/J2ME/Readandwritetotherecordstore.htm
	 */
	public void writeConfig() {
		if(verbose) {
			System.out.println("writing config.");
		}
		
		try {
			// attempt to delete old record store.
			RecordStore.deleteRecordStore("Config");
		} catch (Exception e) {
			// ignored, expected on first save.
		}
		
		// define byte array for holding variable values
		byte[] data;
		
		try {
			
			// record configuration using a record store
			RecordStore rs = RecordStore.openRecordStore("Config", true);
			
			// create entries for all required data, each starting with a unique symbol
			
			// COUNTRY
			data = ("@"+country).getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// TOWN
			data = ("#"+town).getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// LOC ID
			data = ("*"+loc).getBytes();		
			rs.addRecord(data, 0, data.length);			
			
			// LONGITUDE
			String lon = "%"+this.longitude;
			data = lon.getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// LATITUDE
			String lat = "$"+this.latitude;
			data = lat.getBytes();		
			rs.addRecord(data, 0, data.length);		
			
			// PROJECTION
			String proj = "&"+this.projection;
			data = proj.getBytes();		
			rs.addRecord(data, 0, data.length);	
			
			// COLOUR
			String colour = "~"+this.colour;
			data = colour.getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// SHOW GRID
			String grid = "£"+this.showGrid;
			data = grid.getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// RECALCULATION INTERVAL
			String recalc = "!"+this.reCalc;
			data = recalc.getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// BAYER DESIGNATIONS
			String bayer = "-"+this.showBayer;
			data = bayer.getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// ATMOSPHERE
			String at = "="+this.atmos;
			data = at.getBytes();		
			rs.addRecord(data, 0, data.length);
			
			// close the record store
			rs.closeRecordStore();
			
		} catch (Exception e) {
			System.out.println("failed to write config" +e);
		}
		if(verbose) {
			System.out.println("writing finished.");
		}

		// DEBUGGING - loops through all existing stored records ///////////
		try {
			RecordStore rs = RecordStore.openRecordStore("Config", true);
			byte[] recData = new byte[1]; 
			int len;
			
			for (int i = 1; i <= rs.getNumRecords(); i++){
		        if (rs.getRecordSize(i) > recData.length)
		          recData = new byte[rs.getRecordSize(i)];
		       
		        len = rs.getRecord(i, recData, 0);
		        if(verbose) {
		        	System.out.println("Record #" + i + ": " + new String(recData, 0, len));
		        	System.out.println("------------------------------");
		        }
		     }
			rs.closeRecordStore();
		} catch (Exception e) {
			System.out.println("failed to load config :( " +e);
		}
		/////////////////////////////////////////////////////////////////////////
		
		
	}
	
	/**
	 * Determine if we have a configuration to work from
	 * @return true if OK, false otherwise
	 */
	public boolean configOK() {
		return this.configOK;
	}
	
	/**
	 * Set the configuration as now present
	 */
	public void configNowOK() {
		this.configOK = true;
	}
	
	/**
	 * Get Longitude
	 * @return users longitude
	 */
	public double getLong() {
		return this.longitude;
	}
	
	/**
	 * Set the current Longitude
	 * @param l new longitude value
	 */
	public void setLong(double l) {
		this.configUpdates = true;
		this.longitude = l;
	}
	
	/**
	 * Get Latitude
	 * @return users Latitude
	 */
	public double getLat() {
		return this.latitude;
	}		
	
	/**
	 * Set the current Latitude
	 * @param l new latitude value
	 */	
	public void setLat(double l) {
		this.configUpdates = true;
		this.latitude = l;
	}
	
	/**
	 * Get Country
	 * @return users country
	 */
	public String getCountry() {
		return this.country;
	}
	
	/**
	 * Set the current country
	 * @param c new Country value
	 */
	public void setCountry(String c) {
		this.configUpdates = true;
		this.country = c;
	}
	
	/**
	 * Get Town
	 * @return users town
	 */
	public String getTown() {
		return this.town;
	}
	
	/**
	 * Set the current town
	 * @param t new town value
	 */
	public void setTown(String t) {
		this.configUpdates = true;
		this.town = t;
	}
	
	/**
	 * Set the projection type
	 * @param p
	 */
	public void setProjection(int p) {
		this.configUpdates = true;
		this.projection = p;
	}
	
	/**
	 * Get the current projection index
	 * @return projection array index
	 */
	public int getProjection() {
		return this.projection;
	}
	
	/**
	 * Set the location index
	 * @param loc
	 * @return
	 */
	public void setLoc(int loc) {
		this.loc = loc;
	}
	
	/**
	 * Get the location index
	 * @return
	 */
	public int getLoc() {
		return this.loc;
	}
	
	/**
	 * Set the display grid variable
	 * @param b
	 */
	public void setDisplayGrid(boolean b) {
		this.showGrid = b;
	}
	
	/**
	 * Get the current state of the showGrid variabel
	 * @return showGrid
	 */
	public boolean getDisplayGrid() {
		return this.showGrid;
	}
	
	/**
	 * Set the bayer designation option
	 * @param b
	 */
	public void setBayer(boolean b) {
		this.showBayer = b;
	}
	
	/**
	 * Get the bayer designation option
	 * @return bayer option
	 */
	public boolean getBayer() {
		return this.showBayer;
	}
	
	/**
	 * Set the atmosphere option
	 * @param b
	 */
	public void setAtmos(boolean b) {
		this.atmos = b;
	}
	
	/**
	 * Get the atmosphere option
	 * @return atmosphere option
	 */
	public boolean getAtmos() {
		return this.atmos;
	}	
	
	/**
	 * Set the recalculation interval
	 * @param r - new interval
	 */
	public void setReCalc(int r) {
		this.reCalc = r;
	}
	
	/**
	 * Get the recalculation interval
	 * @return - recalc
	 */
	public int getReCalc() {
		return this.reCalc;
	}
	
	/**
	 * Set the colour option
	 * @param c
	 */
	public void setColour(int c) {
		if(verbose) {
			System.out.println("new colour = " + c);
		}
		this.configUpdates = true;
		this.colour = c;
	}

	/**
	 * Get the current colour index
	 * @return
	 */
	public int getColour() {
		return this.colour;
	}
	
	/**
	 * Get the Yahoo Location ID
	 * @return yahoo location id
	 */
	public String getYahooID() {
		return this.yahooLocID;
	}
	
	/**
	 * Set the current yahoo location ID
	 * @param y new yahoo location ID value
	 */
	public void setYahooID(String y) {
		this.configUpdates = true;
		this.yahooLocID = y;
	}
	
	/**
	 * Has the configuration been updated?
	 * @return true if configuration has been updated
	 */
	public boolean updated() {
		return this.configUpdates;
	}
	
	/**
	 * Get date time
	 * @return users date/time
	 */
	public Calendar getDateTime() {

		// force date //////////////////////////////////
		/**
		cal.set( Calendar.MONTH, Calendar.APRIL );
		cal.set( Calendar.DAY_OF_MONTH, 21 );
		cal.set( Calendar.YEAR, 1987 );		
		cal.set( Calendar.HOUR_OF_DAY, 21 );
		cal.set( Calendar.MINUTE, 21 );
		 **/
		// return the calendar
		return cal;
	}

	/**
	 * Get date time (with timezone / daylight savings)
	 * @return users date/time
	 */
	public Calendar getDateTime2() {

		// force date //////////////////////////////////
		/**
		cal.set( Calendar.MONTH, Calendar.APRIL );
		cal.set( Calendar.DAY_OF_MONTH, 21 );
		cal.set( Calendar.YEAR, 1987 );		
		cal.set( Calendar.HOUR_OF_DAY, 21 );
		cal.set( Calendar.MINUTE, 21 );
		 **/
		// return the calendar
		return cal2;
	}	
	
	/**
	 * Get current system date (year)
	 * @return - year
	 */
	public int getYear(boolean b) {
		int y = cal.get(Calendar.YEAR);
		if(b) {
			y = cal2.get(Calendar.YEAR);
		}
		return y;
	}
	
	/**
	 * Get current system date (month)
	 * @return - month
	 */
	public int getMonth(boolean b) {
		int m = cal.get(Calendar.MONTH)+1;
		if(b) {
			m = cal2.get(Calendar.MONTH)+1;
		}		
		return m;
	}
	
	/**
	 * Get current system date (day)
	 * @return - day
	 */
	public int getDay(boolean b) {
		int d = cal.get(Calendar.DAY_OF_MONTH);
		if(b) {
			d = cal2.get(Calendar.DAY_OF_MONTH);
		}			
		return d;
	}

	/**
	 * Get current system date (hour)
	 * @return - hour
	 */
	public int getHour(boolean b) {
		int h = cal.get(Calendar.HOUR_OF_DAY);
		if(b) {
			h = cal2.get(Calendar.HOUR_OF_DAY);
		}				
		return h;
	}

	/**
	 * Get current system date (minute)
	 * @return - minute
	 */
	public int getMin(boolean b) {
		int m = cal.get(Calendar.MINUTE);
		if(b) {
			m = cal2.get(Calendar.MINUTE);
		}				
		return m;
	}
	
	/**
	 * Update the system time by the recalc period
	 */
	public void incTime() {
		this.cal.set(Calendar.MINUTE, 
						this.cal.get(Calendar.MINUTE)+this.reCalc);
	}
	
	/**
	 * Set the current date and time of the system
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public void setDateTime(int year, int month, int day, int hour, int minute) {
		
		// update the datetime calendar
		cal.set( Calendar.MONTH, month-1 );
		cal.set( Calendar.DAY_OF_MONTH, day );
		cal.set( Calendar.YEAR, year );		
		cal.set( Calendar.HOUR_OF_DAY, hour );
		cal.set( Calendar.MINUTE, minute );
		
		// update time based on GMT offset
		cal2 = Calendar.getInstance();
		long timeOffset = cal.getTimeZone().getRawOffset();
		long DSTOffset  = 0;
		
		if(verbose) {
			System.out.println("TIME: " + datetime.getTime());
			System.out.println("TIMEZONE: " + cal.getTimeZone().getID());
			System.out.println("TIMEZONE OFFSET: " + timeOffset);
		}
		
		// Apply DST if used in this zone and currently exists
        if(cal.getTimeZone().useDaylightTime()) {
			DSTOffset = cal.getTimeZone().getOffset(1,
								                cal.get(Calendar.YEAR),
								                cal.get(Calendar.MONTH),
								                cal.get(Calendar.DAY_OF_MONTH),
								                cal.get(Calendar.DAY_OF_WEEK), 0);
			timeOffset+=DSTOffset;
        }		
			
        if(verbose) {
        	System.out.println("DST OFFSET: " + DSTOffset);
        }
		
		// adjust cal2 by the complete offset
		datetime.setTime(cal.getTime().getTime()-timeOffset);
		cal2.setTime(datetime);
	}

}
