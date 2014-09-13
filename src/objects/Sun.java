package objects;
/**
 * This class represents the Sun (Celestial Object).
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
import javax.microedition.lcdui.Graphics;
import resources.*;
import system.Application;
import system.Config;

public class Sun extends CelestialObject {
	
	// verbose mode
	private boolean verbose = false;	
	
	// private variables
	private double geo_long = 0.0;
	private double R = 0.0;
	
	/**
	 * Get the sun's geo longitude
	 * @return
	 */
	public double getGeoLong() {
		return this.geo_long;
	}
	
	/**
	 * Get suns radius vector
	 * @return
	 */
	public double getR() {
		return this.R;
	}
	
	/**
	 * Calculate the altitude and azimuth position of this object
	 * taken from: Meeus p.165
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
	    
	    // get eccentricity of the Earth's orbit
	    double e = 0.016708634 - (0.000042037*T) - (0.0000001267*(T*T));
	    
	    // get mean longitude of the sun
	    double Lo = 280.46646 + 36000.76983 * T + 0.0003032 * (T * T);
	    Lo = Math.toRadians(Lo);
	    
	    // get mean anonmaly of the sun
	    double M = 357.52911 + 35999.05029 * T - 0.00015370 * (T * T);
	    M = Math.toRadians(M);
	    
	    // sun's equation of the center
	    double C = + (1.914602 - (0.004817 * T) - (0.000014 * (T * T))) * Math.sin(M)
	               + (0.019993 - (0.000101 * T)) * Math.sin(2*M)
	               + 0.000289 * Math.sin(3*M);
	    C = Math.toRadians(C);
	    
	    // sun's true longitude    
	    double tl = Lo + C;
	    
		// calculate the obliquity of the ecliptic: Meeus (22.2) p 147
		double eo = 23 + 26/60.0 + 21.488/3600
				  - 46.8150/3600 * T
				  - 0.00059/3600 * (T * T)
				  + 0.001813/3600 * (T * T * T);
		eo = Math.toRadians(eo);
		
		// assign private vars
		geo_long = c.normalise(Math.toDegrees(tl));
		R = (1.000001018 * (1 - (e*e))) / (1 + (e * Math.cos(M+C)));
		
		if(verbose) {
			System.out.println("==================================");
			System.out.println("JDE: " + c.julianDate(config.getYear(true), config.getMonth(true), Cday) + " *");
			System.out.println("T: " + T + " *");
			System.out.println("Lo: " +Math.toDegrees(Lo) + " *");
			System.out.println("M: " +c.normalise(Math.toDegrees(M)) + " *");
			System.out.println("e: " +e + " *");
			System.out.println("C: " +Math.toDegrees(C) + " *");
			System.out.println("eo: " +Math.toDegrees(eo));
			System.out.println("tl: " + tl);
			System.out.println("Geo Long: " + geo_long  + " *");
			System.out.println("R: " + R  + " *");
			System.out.println("==================================");
		}
		
		// calculate sun's right acension and declination
		double RA  = Float11.atan2(Math.cos(eo) * Math.sin(tl), Math.cos(tl));
		double DEC = Float11.asin(Math.sin(eo) * Math.sin(tl));
		
		RA = c.normalise(Math.toDegrees(RA));
		DEC = Math.toDegrees(DEC);
		
		double[] r = c.ra_dec_to_alt_azmiuth(RA, DEC, app);
		
		if(verbose) {
			System.out.println("[SUN] RA:"+RA + " DEC:" + DEC);
			System.out.println("[SUN] ALT:"+r[1] + " AZI:" + r[0]);
		}
		
		// set the altitude and azimuth of the sun!
		this.alt     = r[1];
		this.azimuth = r[0];
		
		// set the sun as invisible if altitude < 1 : not above the horrizon
		if(this.alt<1) {
			this.visible = false;
			this.onScreen(false);
		} else {
			this.visible = true;
			this.onScreen(true);
		}
	}
	
	/**
	 * Create a new Sun!
	 */
	public Sun() {
		this.colour = 0xfff82f;
		this.size = 10;
		this.name = "The Sun";
		this.onscreen = true;
	}
	/**
	 * Draw the sun!
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
