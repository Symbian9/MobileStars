package objects;
/**
 * This class represents the planet Mercury (Celestial Object).
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
import javax.microedition.lcdui.Graphics;
import resources.*;
import system.Application;
import system.Config;

public class Mercury extends CelestialObject {
	
	// verbose mode
	private boolean verbose = false;
	
	// Mercurys sun
	private Sun sun;
	
	
	/**
	 * Calculate the altitude and azimuth position of this object
	 * Meeus chapter:31 for the orbital elements and 33: for ecliptic orbit
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

   	    // get T
	    double T  = c.T(config.getYear(true), config.getMonth(true), Cday);
	    
	    // expressed as polynomials of the form:
	    // a0 + a1 T + a2 T2 + a3 T3
	    
	    // mean longitude of the planet
	    double L = c.ploynominal(252.250906, +149472.6746358, -0.00000536, +0.000000002, T);
	    	
	    // semimajor axis of the orbit
	    double a = 0.387098310;
	    	
	    // eccentricity of the orbit
	    double e = c.ploynominal(0.20563175, +0.000020407, -0.0000000283, -0.00000000018, T);
	    	
	    // inclination on the plane of the ecliptic
	    double i = c.ploynominal(7.004986, -0.0059516, +0.00000080, +0.000000043, T);
	    	
	    // longitude of the ascending node
	    double omega = c.ploynominal(48.330893, -0.1254227, -0.00008833, -0.000000200, T);
	    	
	    // longitude of the perihelion
	    double pi = c.ploynominal(77.456119, +0.1588643, -0.00001342, -0.000000007, T);
	    
	    // calculate M and w
	    L = c.normalise(L);
	    double M = L - pi;	    	
	    double w = pi - omega;
	    
	    /**
	     * Test Data -
	     * for comet Encke for 1009 October 6.0 - Meeus p.232

	    a = 2.2091404;
	    e = 0.8502196;
	    i = 11.94524;
	    omega = 334.75006;
	    w = 186.23352;
	    M = -22.54502;
	    */
	   
	    if(verbose) {
		    System.out.println("T: " + T);
		    System.out.println("L: " + L);
		    System.out.println("a: " + a);
		    System.out.println("e: " + e);
		    System.out.println("i: " + i);
		    System.out.println("omega: " + omega);
		    System.out.println("pi: " + pi);
		    System.out.println("M: " + M);
		    System.out.println("w: " + w);	    
	    }
	    
	    // calculate RA / DEC 
	    double[] r1 = c.orbital_to_ra_dec(app, sun, a, e, Math.toRadians(i), Math.toRadians(w), Math.toRadians(omega), Math.toRadians(M), c.T_(config.getYear(true), config.getMonth(true), Cday), false);
		RA = Math.toDegrees(r1[0]);
		DEC = Math.toDegrees(r1[1]);	    

	    // calculate ALT / AZ
	    double[] r = c.ra_dec_to_alt_azmiuth(RA, DEC, app);

		// set the altitude and azimuth of mercury!
		this.alt     = r[1];
		this.azimuth = r[0];

		if(verbose) {
			System.out.println("[MERCURY] RA:"+RA + " DEC:" + DEC);
			System.out.println("[MERCURY] ALT:"+r[1] + " AZI:" + r[0]);
		}
		
		// set as invisible if altitude < 1 : not above the horrizon
		if(this.alt<1) {
			this.visible = false;
			this.onScreen(false);
		} else {
			this.visible = true;
			this.onScreen(true);
		}	    
	    
	}
	
	/**
	 * Create a new Planet Mercury!
	 * @param the Sun
	 */
	public Mercury(Sun sun) {
		this.sun = sun;
		this.colour = 0xff75cb;
		this.size = 6;
		this.name = "Mercury";
		this.onscreen = true;
	}	
	
	/**
	 * Draw Mercury!
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
