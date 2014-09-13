package resources;
/**
 * This class will handle server communication.
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import ui.*;
import objects.*;
import system.Application;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class HTTPrequester implements Runnable {
	
	// is the HTTPrequest complete?
	private boolean complete = false;
	// which request is required?
	private int request = 0;
	// whats the request result?
	private String result = "";
	// whats the updating screen to call
	private Updating u;
	// whats the application?
	private Application app;
	// whats the users location
	private int location = 0;
	// phone radio signal details as strings
	//private String cellids, lacs, mccs, mncs;
	// phone radio signal details as integers
	//private int cellid, lac, mcc, mnc;
	// remote url's for easy modification
	private String url_SERVER  = "### removed ###";
	
	
	/**
	 * Thread run method to perform the required method
	 */
	public void run() {
		switch(this.request) {
			case 1:
				// get the updates
				try {
					getUpdates(this.location);
					// send notification to updates screen
					this.u.complete(true, "objects");
				} catch (Exception e) {
					// failed to collect from HTTP
					System.out.println("problem with http: " + e);
					this.u.complete(false, "objects");
				}				
			break;
			case 2:
				// list news
				try {
					listNews();
				} catch (Exception e) {
					// failed to collect from HTTP
					System.out.println("problem with http: " + e);
					this.u.complete(false, "news");
				}
			break;
			case 3:
				// show news article
				try {
					showNews(new String(""+location)); // location used as news ID
				} catch (Exception e) {
					// failed to collect from HTTP
					System.out.println("problem with http: " + e);
					this.u.complete(false, "news");
				}
			break;
			case 4:
				// show weather
				try {
					doWeather();
				} catch (Exception e) {
					// failed to collect from HTTP
					this.u.complete(false, "weather");
				}
			break;
		}
	}
	
	/**
	 * Get results from server
	 * @return
	 */
	public String getResult() {
		return this.result;
	}
	
	/**
	 * Has the request completed?
	 */
	public boolean isComplete() {
		return this.complete;
	}
	
	/**
	 * HTTPrequester construction
	 */
	public HTTPrequester(int request, int location, Updating u, Application app) {
		
		// assign the request, location, updating screen and application
		this.request  	 = request;
		this.location 	 = location;
		this.u        	 = u;
		this.app         = app;
		
		// retrieve radio signal details
	 	//cellids = System.getProperty("com.sonyericsson.net.cellid");
	    //lacs 	= System.getProperty("com.sonyericsson.net.lac");
	    //mccs 	= System.getProperty("com.sonyericsson.net.mcc");
	   // mncs 	= System.getProperty("com.sonyericsson.net.mnc");    
	    // set defaults
	   // cellid = lac = mcc = mnc = -1;	    
	    // convert to integer data types, if required. 16 base for hex
		//if(cellids!=null) 	{ cellid = Integer.parseInt(cellids.toString(), 16); }
		//if(lacs!=null) 		{ lac    = Integer.parseInt(lacs.toString(), 16); }
		//if(mccs!=null) 		{ mcc    = Integer.parseInt(mccs.toString()); }					
		//if(mncs!=null)		{ mnc    = Integer.parseInt(mncs.toString()); }
		
	}
	
	/**
	 * Build and parse the result of a HTTP request
	 * @param url - web address to access 
	 * @param params - optional site parameters
	 * @return the URL response
	 */
	public String getHTML(String url, String params) throws Exception {
		// default result is the empty string
		String result="";
		
        HttpConnection objConn = null;
        InputStream input      = null;
        StringBuffer sbuffer   = null;
        int MAX_LENGTH	  	   = 265*150;
        
	    try {
	    	
		    // build HTTP connection
		    objConn=(HttpConnection)Connector.open(url + params);
		    System.out.println(url+params);
		    input   = objConn.openInputStream();
		    sbuffer = new StringBuffer();
	        InputStreamReader r = new InputStreamReader(input);
	        int total=0;
	        int read =0;
	        
	        // continue to read in the response
	        while ((read=r.read())>=0 && total<= MAX_LENGTH) {
	            sbuffer.append((char)read);
	            total++;
	        }
	     
	        // save the output
	        result = sbuffer.toString();
           
	     } catch (Exception e) {
	    	 // failed to collect webpage, throw a new exception.
	    	 System.out.println("Failed to connect (" +url +") cause:"+ e);
	    	 throw new Exception();
	     }		
	     
	     // return the string result of the HTTP request
	     return result;
	}
	
	/**
	 * Get the users weather information
	 */
	public void doWeather() throws Exception {
		// build and submit a server request
		this.result = getHTML(url_SERVER, "?request=4&params="+this.app.getConfig().getLoc()+1); // +1 due to zero index
		// new XMLParser from server xml document
		XMLParser xp = new XMLParser(this.result);
		// define XML elements
		Vector v = new Vector();
		v.addElement("sunrise");
		v.addElement("sunset");
		v.addElement("day1");
		v.addElement("day2");
		v.addElement("day3");
		v.addElement("day4");
		v.addElement("day5");
		// parse the defined elements
		Vector items = xp.parse(v);
		// show news article via application
		this.app.showWeather(items);		
	}

	/**
	 * List news feed articles from server
	 */
	public void listNews() throws Exception {
		// build and submit a server request
		this.result = getHTML(url_SERVER, "?request=2&params="+null);
		// new XMLParser from server xml document
		XMLParser xp = new XMLParser(this.result);
		// define XML elements
		Vector v = new Vector();
		v.addElement("title");
		v.addElement("description");
		v.addElement("link");
		// pasrse the defined elements
		Vector items = xp.parse(v);
		System.out.println("OK");
		for(int i=0; i<items.size(); i++) {
			System.out.println(items.elementAt(i));
		}
		// show news list via application
		this.app.listNews(items);
	}
	
	/**
	 * Get a news article from the server
	 * @param link - ID of remote ews article
	 */
	public void showNews(String link) throws Exception {
		// build and submit a server request
		this.result = getHTML(url_SERVER, "?request=3&params="+link);
		// new XMLParser from server xml document
		XMLParser xp = new XMLParser(this.result);
		// define XML elements
		Vector v = new Vector();
		v.addElement("headline");
		v.addElement("body");	
		// parsse the defined elements
		Vector items = xp.parse(v);
		// show news article via application
		this.app.showNews(items);
	}
	
	/**
	 * Get updates from the server
	 * @param location id
	 * @return
	 */
	public void getUpdates(int location) throws Exception {
		// build and submit a server request
		this.result = getHTML(url_SERVER, "?request=1&params="+location);
		System.out.println(this.result);
		// new XMLParser from server xml document
		XMLParser xp = new XMLParser(this.result);		
		// define XML elements
		Vector v = new Vector();
		v.addElement("number");
		v.addElement("name");
		v.addElement("q");
		v.addElement("e");
		v.addElement("i");
		v.addElement("omega");
		v.addElement("w");
		v.addElement("pday");
		v.addElement("pmonth");
		v.addElement("pyear");
		v.addElement("a");
		// parsse the defined elements
		Vector items = xp.parse(v);
		// number of comets to evaluate
		int numComets = items.size()/10; // 10 elements per comet
		for(int j=0; j<numComets-1; j+=11) {
			// retrieve element values as the correct data type
			int number   = Integer.parseInt(((XMLElement)items.elementAt(j)).getValue());
			String name  = ((XMLElement)items.elementAt(j+1)).getValue();
			double q     = Double.parseDouble(((XMLElement)items.elementAt(j+2)).getValue());
			double e     = Double.parseDouble(((XMLElement)items.elementAt(j+3)).getValue());
			double i     = Double.parseDouble(((XMLElement)items.elementAt(j+4)).getValue());
			double omega = Double.parseDouble(((XMLElement)items.elementAt(j+5)).getValue());
			double w 	 = Double.parseDouble(((XMLElement)items.elementAt(j+6)).getValue());
			double d 	 = Double.parseDouble(((XMLElement)items.elementAt(j+7)).getValue());
			int m 		 = Integer.parseInt(((XMLElement)items.elementAt(j+8)).getValue());
			int y 		 = Integer.parseInt(((XMLElement)items.elementAt(j+9)).getValue());
			double a 	 = Double.parseDouble(((XMLElement)items.elementAt(j+10)).getValue());
			// create the new comet and add to the applications update vector
			Comet c = new Comet(name, number, q, e, i, omega, w, a, d, m, y);
    		app.getUpdatesVector().addElement(c);
			System.out.println(number + "|" + name + "|" + q + "|" + i + "|" + omega + "|" + w + "|" + d + "|" + m + "|" + y + "|" + a );
			
		}
	}

}
