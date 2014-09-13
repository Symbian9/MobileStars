package objects;
/**
 * This class represents a Comet (Celestial Object).
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.Graphics;
import resources.Calculations;
import system.Application;
import java.util.*;
import system.Config;

public class Comet extends CelestialObject {
	
	private boolean verbose = false;
	
	// comet data
	private Sun sun;

	private double q;
	private double i;
	private double omega;
	private double w;
	private double a;
	private double d;
	private double e;
	private int m;
	private int y;
	
	/**
	 * Calculate the altitude and azimuth position of this object
	 */
	public void calculatePosition(Application app, Calculations c) {

		// get the configuration
		Config config = app.getConfig();
				
	    // calculate day with time as decimal
	    double Cmin = config.getMin(true);
	    	   Cmin = Cmin/1440;
	    double Cday = config.getHour(true);
	    	   Cday = Cday/24;
	    	   Cday = Cday + config.getDay(true);
	    	   Cday = Cday + Cmin;		

		// calculate comet position
		//double n = 0.9856076686 / (a * Math.sqrt(a));
		// date difference between now and perihelion
		long now = app.getConfig().getDateTime2().getTime().getTime();
		String sd = ""+d;

		// get integer from day double
		int day  = Integer.parseInt(sd.substring(0, sd.indexOf(".")));

		// get integer hours from day double
		String h = "0"+sd.substring(sd.indexOf("."), sd.length());
			//System.out.println("h: " + h);
		double hoursd = Double.parseDouble("0"+h.substring(h.indexOf("."), h.length()))*24;
		String hourss = ""+hoursd;
		int hours = Integer.parseInt(""+hourss.substring(0, hourss.indexOf(".")));

		// get integer mins from day double
		double minsd = Double.parseDouble("0."+h.substring(h.indexOf(".")+1, h.length()))/60;
		String minss = ""+minsd;
		int mins = Integer.parseInt(""+minss.substring(0, minss.indexOf(".")));

		// create new calendar to house the parsed date
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.MONTH, this.m-1 ); // zero indexed
		cal.set( Calendar.DAY_OF_MONTH, day );
		cal.set( Calendar.YEAR, this.y );		
		cal.set( Calendar.HOUR_OF_DAY, hours );
		cal.set( Calendar.MINUTE, mins );
		long cnow = cal.getTime().getTime();

		// calculate difference
		long dif = now-cnow;

		// millis per day : http://www.java2s.com/Code/Java/J2ME/KVMCalendar.htm
		long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
		double M = Double.parseDouble(""+dif / MILLIS_PER_DAY);

	    // calculate RA / DEC 
	    double[] r1 = c.orbital_to_ra_dec(app, sun, a, e, Math.toRadians(i), Math.toRadians(w), Math.toRadians(omega), Math.toRadians(M), c.T_(config.getYear(true), config.getMonth(true), Cday), true);
		RA = Math.toDegrees(r1[0]);
		DEC = Math.toDegrees(r1[1]);

	    // calculate ALT / AZ
	    double[] r = c.ra_dec_to_alt_azmiuth(RA, DEC, app);

		// set the altitude and azimuth of venus!
		this.alt     = r[1];
		this.azimuth = r[0];	
		
		if(alt>0) {
			
			if(verbose) {
				System.out.println("[COMET] " + this.name);
				System.out.println(day+"/"+m+"/"+y);
			    System.out.println("a: " + a);
			    System.out.println("q: " + q);
			    System.out.println("e: " + e);
			    System.out.println("i: " + i);
			    System.out.println("omega: " + omega);
			    System.out.println("M: " + M);
			    System.out.println("w: " + w);
			    System.out.println("RA: " + RA);
			    System.out.println("DEC: " + DEC);			    
			    System.out.println("alt: " + alt);
			    System.out.println("az: " + azimuth);
			}				
			//System.out.println("[COMET] " + this.name + " ALT: " + this.alt + " AZMI: " + this.azimuth);
			this.visible = true;
		} else {
			this.visible = false;
		}

	}	
	
	/**
	 * Set this comets sun
	 * @param sun
	 */
	public void setSun(Sun sun) {
		this.sun = sun;
	}
	
	/**
	 * Create a new Comet!
	 */
	public Comet(String name, int number, double q, double e, double i, double omega, double w, double a, double d, int m, int y) {
		// assign comet data.
		this.colour = 0xF34D43;
		this.size = 6;
		this.name = name;
		// this.number = number;
		this.q = q;
		this.e = e;
		this.i = i;
		this.omega = omega;
		this.w = w;
		this.a = a;
		this.e = e;
		this.d = d;
		this.m = m;
		this.y = y;
	}

	/**
	 * Draw this Comet!
	 */
	public void draw(Graphics g, int circle, int colour) {
		int modifier = this.size/2;
		if(this.isHighlighted()) {
			// if highlighted: highlight in green
			g.setColor(0x1eff00);
			g.fillArc(this.xPos-modifier-2, this.yPos-modifier-2, this.size+4, this.size+4, 0, 360);
		}		
		g.setColor(this.colour);
		g.fillArc(this.xPos-modifier, this.yPos-modifier, this.size, this.size, 0, 360);
	}
}
