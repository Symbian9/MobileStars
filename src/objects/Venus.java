package objects;
/**
 * This class represents the planet Venus (Celestial Object).
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import javax.microedition.lcdui.Graphics;
import resources.*;
import system.Application;
import system.Config;

public class Venus extends CelestialObject {
	
	// verbose mode
	private boolean verbose = false;

	// Venus' sun
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
	    double L = c.ploynominal(181.979801, +58517.8156760, +0.00000165, -0.000000002, T);
	    	
	    // semimajor axis of the orbit
	    double a = 0.723329820;
	    	
	    // eccentricity of the orbit
	    double e = c.ploynominal(0.00677192, -0.000047765, +0.0000000981, +0.00000000046, T);
	    	
	    // inclination on the plane of the ecliptic
	    double i = c.ploynominal(3.394662, -0.0008568, -0.00003244, +0.000000009, T);
	    	
	    // longitude of the ascending node
	    double omega = c.ploynominal(76.679920, -0.2780134, -0.00014257, -0.000000164, T);
	    	
	    // longitude of the perihelion
	    double pi = c.ploynominal(131.563703, +0.0048746, -0.00138467, -0.000005695, T);
	    
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

		// set the altitude and azimuth of venus!
		this.alt     = r[1];
		this.azimuth = r[0];

		if(verbose) {
			System.out.println("[VENUS] RA:"+RA + " DEC:" + DEC);
			System.out.println("[VENUS] ALT:"+r[1] + " AZI:" + r[0]);
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
	 * Create a new Planet Venus!
	 * @param the Sun
	 */
	public Venus(Sun sun) {
		this.sun = sun;
		this.colour = 0xedc685;
		this.size = 6;
		this.name = "Venus";
		this.onscreen = true;
	}	
	
	/**
	 * Draw Venus!
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
