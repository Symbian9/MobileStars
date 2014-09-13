package objects;
/**
 * This class represents the planet Saturn (Celestial Object).
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
import javax.microedition.lcdui.Graphics;
import resources.*;
import system.Application;
import system.Config;

public class Saturn extends CelestialObject {
	
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
	    double L = c.ploynominal(50.077444, +1222.1138488, +0.00021004, -0.000000046, T);
	    	
	    // semimajor axis of the orbit
	    double a = 9.554909192 + (-0.0000021390*T) + (-0.000000004*(T*T));
	    	
	    // eccentricity of the orbit
	    double e = c.ploynominal(0.05554814, -0.000346641, -0.0000006436, +0.00000000340, T);
	    	
	    // inclination on the plane of the ecliptic
	    double i = c.ploynominal(2.488879, +0.0025514, -0.00004906, +0.000000017, T);
	    	
	    // longitude of the ascending node
	    double omega = c.ploynominal(113.665503, -0.2566722, -0.00018399, +0.000000480, T);
	    	
	    // longitude of the perihelion
	    double pi = c.ploynominal(93.057237, +0.5665415, +0.00052850, +0.000004912, T);
	    
	    // calculate M and w
	    L = c.normalise(L);
	    double M = L - pi;	    	
	    double w = pi - omega;
	    
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

		// set the altitude and azimuth of Jupiter!
		this.alt     = r[1];
		this.azimuth = r[0];

		if(verbose) {
			System.out.println("[SATURN] RA:"+RA + " DEC:" + DEC);
			System.out.println("[SATURN] ALT:"+r[1] + " AZI:" + r[0]);
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
	 * Create a new Planet Saturn!
	 * @param the Sun
	 */
	public Saturn(Sun sun) {
		this.sun = sun;
		this.colour = 0xf6b452;
		this.size = 6;
		this.name = "Saturn";
		this.onscreen = true;
	}	
	
	/**
	 * Draw saturn!
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
