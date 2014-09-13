package objects;
/**
 * This class represents the Moon (Celestial Object)
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import javax.microedition.lcdui.Graphics;
import resources.Calculations;
import system.Application;
import system.Config;

public class Moon extends CelestialObject {
	
	// verbose mode
	private boolean verbose = false;		

	/**
	 * Calculate the altitude and azimuth position of this object
	 * taken from: Meeus p.337
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

	    // get JDE
	    double JDE = c.julianDate(config.getYear(true), config.getMonth(true), Cday);
	    	   
   	    // get T
	    double T  = c.T(config.getYear(true), config.getMonth(true), Cday);
	    
	    // moons mean longitude
	    double li = 218.3164477 + 481267.88123421 * T 
	    		  - 0.0015786 * (T * T) + (T * T * T) / 538841 - (T * T * T * T) / 65194000;
	    li = Math.toRadians(li);
	    
	    // mean elongation of the moon
	    double D = 297.8501921 + 445267.1114034 * T
	             - 0.0018819 * (T * T) + (T * T * T) / 545868 - (T * T * T * T) / 113065000;
	    D = Math.toRadians(D);

	    // sun's mean anomaly
	    double M = 357.52911 + 35999.05029 * T - 0.00015370 * (T * T);
	    M = Math.toRadians(M);
	    
	    // Moon's mean anomaly
	    double Mi = 134.9633964 + 477198.8675055 * T
	              + 0.0087414 * (T * T) + (T * T * T) / 69699 - (T * T * T * T) /14712000;
	    Mi = Math.toRadians(Mi);
	    
	    // Moon's argument of latitude (mean distance of the Moon from its ascending node)
	    double F = 93.2720950 + 483202.0175233 * T 
	             - 0.0036539 * (T * T) - (T * T * T) / 3526000 + (T * T * T * T) / 863310000;
	    F = Math.toRadians(F);
	    
	    // there further arguments
	    double A1 = 119.75 + 131.849 * T;
	    A1 = Math.toRadians(A1);
	    double A2 = 53.09 + 479264.290 * T;
	    A2 = Math.toRadians(A2);
	    double A3 = 313.45 + 481266.484 * T;
	    A3 = Math.toRadians(A3);
	    
	    // caclulate E
	    double E = 1 - 0.002516 * T - 0.0000074 * (T * T);
	    
	    // calculate sigma L
	    double sl = getSigmaL(E, D, M, Mi, F);
	    // additive values for sigma L
	    sl += 3958 * Math.sin(A1);
	    sl += 1962 * Math.sin(li - F);
	    sl += 318 * Math.sin(A2);

	    // calculate sigma R
	    double sr = getSigmaR(E, D, M, Mi, F);

	    // calculate sigma B
		double sb = getSigmaB(E, D, M, Mi, F);
		// additive values for sigma B
		sb += -2235 * Math.sin(li);
		sb += 382 * Math.sin(A3);
		sb += 175 * Math.sin(A1 - F);
		sb += 175 * Math.sin(A1 + F);
		sb += 127 * Math.sin(li - Mi);
		sb += -115 * Math.sin(li + Mi);

		// ecliptical longitude
		double longitude = c.normalise(Math.toDegrees(li)) + sl/1000000;
		
		// ecliptical latitude
		double latitude = sb/1000000;
		
		// delta
		double delta = 385000.56 + sr / 1000;
		
		// nutation in longitude
		double omega = 125.04452 - 1934.136261 * T + (T*T) + (T*T*T);
		double L = 280.4665 + 36000.7698 * T;
		double LI = 218.3165 + 481267.8813 * T;
		double psi = -17.20/3600 * Math.sin(omega) - 1.32/3600 * Math.sin(2*L) - 0.23/3600 * Math.sin(2*LI) + 0.21/3600 * Math.sin(2*omega);
		double epsilon = +9.20/3600 * Math.cos(omega) + 0.57/3600 * Math.cos(2*L) + 0.10/3600 * Math.cos(2*LI) - 0.0/3600 * Math.cos(2*omega);
		
		// calculate the obliquity of the ecliptic: Meeus (22.2) p 147
		double eo = 23 + 26/60.0 + 21.488/3600
		  - 46.8150/3600 * T
		  - 0.00059/3600 * (T * T)
		  + 0.001813/3600 * (T * T * T);
		
		// true obliquity
		epsilon += eo;
		
		// apparent longitude
		longitude += psi;
		
		// Convert from ecliptical longitude/latitude to RA/DECLINATION
		double[] e = c.ecliptical_to_equatorial(longitude, latitude, epsilon);		
		this.RA = Math.toDegrees(e[0]);   /// ???
		this.DEC = Math.toDegrees(e[1]);   //// ????
		
		// convert to alt / az
		double[] r = c.ra_dec_to_alt_azmiuth(RA, DEC, app);
		
		if(verbose) {
			System.out.println("[MOON] RA:"+RA + " DEC:" + DEC);
			System.out.println("[MOON] ALT:"+r[1] + " AZI:" + r[0]);
		}
		
		// set the altitude and azimuth of the sun!
		this.alt     = r[1];
		this.azimuth = r[0];		
		
		if(verbose) {
			System.out.println("JDE: " + JDE);
			System.out.println("T: " + T);
			System.out.println("L': "+ c.normalise(Math.toDegrees(li)));
			System.out.println("D: " + c.normalise(Math.toDegrees(D)));
			System.out.println("M: " + c.normalise(Math.toDegrees(M)));
			System.out.println("M': " + c.normalise(Math.toDegrees(Mi)));
			System.out.println("F: " + c.normalise(Math.toDegrees(F)));
			System.out.println("A1: " + c.normalise(Math.toDegrees(A1)));
			System.out.println("A2: " + c.normalise(Math.toDegrees(A2)));
			System.out.println("A3: " + c.normalise(Math.toDegrees(A3)));
			System.out.println("E: " + E);
			System.out.println("Sigma l: " + sl);
			System.out.println("Sigma b: " + sb);
			System.out.println("Sigma r: " + sr);
			System.out.println("lat: " + latitude);
			System.out.println("long: " + longitude);
			System.out.println("delta: " + delta);
			System.out.println("psi: " + psi);
			System.out.println("epsilon: " + epsilon);
			System.out.println("RA: " + RA);
			System.out.println("DECLIN: " + DEC);
			System.out.println("Moon plotted!");
		}
		
		// set the moon as invisible if altitude < 1 : not above the horrizon
		if(this.alt<1) {
			this.visible = false;
			this.onScreen(false);
		} else {
			this.visible = true;
			this.onScreen(true);
		}
	}	
	
	/**
	 * calculates the sum for Sigma R - meeus p. 338-340
	 * @param E
	 * @return sum Sr
	 */
	public double getSigmaR(double E, double D, double M, double Mi, double F) {
		
		double sum = 0.0;
		
		sum += -20905355 * Math.cos(Mi);
		sum += -3699111 * Math.cos(2*D - Mi);
		sum += -2955968 * Math.cos(2*D);
		sum += -569925 * Math.cos(2*Mi);
		sum += 48888 * E * Math.cos(M);
		sum += -3149 * Math.cos(2*F);
		sum += 246158 * Math.cos(2*D - 2*Mi);
		sum += -152138 * E * Math.cos(2*D - M - Mi);
		sum += -170733 * Math.cos(2*D + Mi);
		sum += -204586 * E * Math.cos(2*D - M);
		sum += -129620 * E * Math.cos(M - Mi);
		sum += 108743 * Math.cos(D);
		sum += 104755 * E * Math.cos(M + Mi);
		sum += 10321 * Math.cos(2*D - 2*F);
		sum += Math.cos(Mi + 2*F);
		sum += 79661 * Math.cos(Mi - 2*F);
		sum += -34782 * Math.cos(4*D - Mi);
		sum += -23210 * Math.cos(3*Mi);
		sum += -21636 * Math.cos(4*D - 2*Mi);
		sum += 24208 * E * Math.cos(2*D + M - Mi);
		sum += 30824 * E * Math.cos(2*D + M);
		sum += -8379 * Math.cos(D - Mi);
		sum += -16675 * E * Math.cos(D + M);
		sum += -12831 * E * Math.cos(2*D - M + Mi);
		sum += -10445 * Math.cos(2*D + 2*Mi);
		sum += -11650 * Math.cos(4*D);
		sum += 14403 * Math.cos(2*D - 3*Mi);
		sum += -7003 * E * Math.cos(M - 2*Mi);
		sum += Math.cos(2*D - Mi + 2*F);
		sum += 10056 * E * Math.cos(2*D - M - 2*Mi);
		sum += 6322 * Math.cos(D + Mi);
		sum += -9884 * (E*E) * Math.cos(2*D - 2*M);
		sum += 5751 * E * Math.cos(M + 2*Mi);
		sum += (E*E) * Math.cos(2*M);
		sum += -4950 * (E*E) * Math.cos(2*D - 2*M -Mi);
		sum += 4130 * Math.cos(2*D + Mi - 2*F);
		sum += Math.cos(2*D + 2*F);
		sum += -3958 * E * Math.cos(4*D - M - Mi);
		sum += Math.cos(2*Mi + 2*F);
		sum += 3258 * Math.cos(3*D - Mi);
		sum += 2616 * E * Math.cos(2*D + M + Mi);
		sum += -1897 * E * Math.cos(4*D - M - 2*Mi);
		sum += -2117 * (E*E) * Math.cos(2*M - Mi);
		sum += 2354 * (E*E) * Math.cos(2*D + 2*M - Mi);
		sum += E * Math.cos(2*D + M - 2*Mi);
		sum += E * Math.cos(2*D - M - 2*F);
		sum += -1423 * Math.cos(4*D + Mi);
		sum += -1117 * Math.cos(4*Mi);
		sum += -1571 * E * Math.cos(4*D - M);
		sum += -1739 * Math.cos(D - 2*Mi);
		sum += E * Math.cos(2*D + M - 2*F);
		sum += -4421 * Math.cos(2*Mi - 2*F);
		sum += E * Math.cos(D + M + Mi);
		sum += Math.cos(3*D - 2*Mi);
		sum += Math.cos(4*D - 3*Mi);
		sum += E * Math.cos(2*D - M + 2*Mi);
		sum += 1165 * (E*E) * Math.cos(2*M + Mi);
		sum += E * Math.cos(D + M - Mi);
		sum += Math.cos(2*D + 3*Mi);
		sum += 8752 * Math.cos(2*D - Mi - 2*F);
		
		return sum;
	}
	
	/**
	 * calculates the sum for Sigma B - meeus p. 341
	 * @param E
	 * @return sum Sr
	 */
	public double getSigmaB(double E, double D, double M, double Mi, double F) {
		
		double sum = 0.0;
		
		sum += 5128122 * Math.sin(F);
		sum += 280602 * Math.sin(Mi + F);
		sum += 277693 * Math.sin(Mi - F);
		sum += 173237 * Math.sin(2*D - F);
		sum += 55413 * Math.sin(2*D - Mi + F);
		sum += 46271 * Math.sin(2*D - Mi - F);
		sum += 32573 * Math.sin(2*D + F);
		sum += 17198 * Math.sin(2*Mi + F);
		sum += 9266 * Math.sin(2*D + Mi - F);
		sum += 8822 * Math.sin(2*Mi - F);
		sum += 8216 * E * Math.sin(2*D - M - F);
		sum += 4324 * Math.sin(2*D - 2*Mi - F);
		sum += 4200 * Math.sin(2*D + Mi + F);
		sum += -3359 * E * Math.sin(2*D + M - F);
		sum += 2463 * E * Math.sin(2*D - M - Mi + F);
		sum += 2211 * E * Math.sin(2*D - M + F);
		sum += 2065 * E * Math.sin(2*D - M - Mi - F);
		sum += -1870 * E * Math.sin(M - Mi - F);
		sum += 1828 * Math.sin(D*4 - Mi - F);
		sum += -1794 * E * Math.sin(M + F);
		sum += -1749 * Math.sin(3*F);
		sum += -1565 * E * Math.sin(M - Mi + F);
		sum += -1491 * Math.sin(D + F);
		sum += -1475 * E * Math.sin(M + Mi + F);
		sum += -1410 * E * Math.sin(M + Mi - F);
		sum += -1344 * E * Math.sin(M - F);
		sum += -1335 * Math.sin(D - F);
		sum += 1107 * Math.sin(3*Mi + F);
		sum += 1021 * Math.sin(4*D - F);
		sum += 833 * Math.sin(4*D - Mi + F);
		sum += 777 * Math.sin(Mi - 3*F);
		sum += 671 * Math.sin(4*D - 2*Mi + F);
		sum += 607 * Math.sin(2*D - 3*F);
		sum += 596 * Math.sin(2*D + 2*Mi - F);
		sum += 491 * E * Math.sin(2*D - M + Mi - F);
		sum += -451 * Math.sin(2*D - 2*Mi + F);
		sum += 439 * Math.sin(3*Mi - F);
		sum += 422 * Math.sin(2*D + 2*Mi + F);
		sum += 421 * Math.sin(2*D - 3*Mi - F);
		sum += -366 * E * Math.sin(2*D + M - Mi + F);
		sum += -351 * E * Math.sin(2*D + M + F);
		sum += 331 * Math.sin(4*D + F);
		sum += 315 * E * Math.sin(2*D - M + Mi + F);
		sum += 302 * (E*E) * Math.sin(2*D - 2*M - F);
		sum += -283 * Math.sin(Mi + 3*F);
		sum += -229 * E * Math.sin(2*D + M + Mi - F);
		sum += 223 * E * Math.sin(D + M - F);
		sum += 223 * E * Math.sin(D + M + F);
		sum += -220 * E * Math.sin(M - 2*Mi - F);
		sum += -220 * E * Math.sin(2*D + M - Mi - F);
		sum += -185 * Math.sin(D + Mi + F);
		sum += 181 * E * Math.sin(2*D - M - 2*Mi - F);
		sum += -177 * E * Math.sin(M + 2*Mi + F);
		sum += 176 * Math.sin(4*D - 2*Mi - F);
		sum += 166 * E * Math.sin(4*D - M - Mi - F);
		sum += -164 * Math.sin(D + Mi - F);
		sum += 132 * Math.sin(4*D + Mi - F);
		sum += -119 * Math.sin(D - Mi - F);
		sum += 115 * E * Math.sin (4*D - M - F);
		sum += 107 * (E*E) * Math.sin(2*D - 2*M + F);
	
		return sum;
	}
	
	/**
	 * calculates the sum for Sigma l - meeus p. 338-340
	 * @param E
	 * @return sum Sl
	 */
	public double getSigmaL(double E, double D, double M, double Mi, double F) {
		
		double sum = 0.0;
		
		sum += 6288774 * Math.sin(Mi);
		sum += 1274027 * Math.sin(2*D - Mi);
		sum += 658314 * Math.sin(2*D);
		sum += 213618 * Math.sin(2*Mi);
		sum += -185116 * E * Math.sin(M);
		sum += -114332 * Math.sin(2*F);
		sum += 58793 * Math.sin(2*D - 2*Mi);
		sum += 57066 * E * Math.sin(2*D - M - Mi);
		sum += 53322 * Math.sin(2*D + Mi);
		sum += 45758 * E * Math.sin(2*D - M);
		sum += -40923 * E * Math.sin(M - Mi);
		sum += -34720 * Math.sin(D);
		sum += -30383 * E * Math.sin(M + Mi);
		sum += 15327 * Math.sin(2*D - 2*F);
		sum += -12528 * Math.sin(Mi + 2*F);
		sum += 10980 * Math.sin(Mi - 2*F);
		sum += 10675 * Math.sin(4*D - Mi);
		sum += 10034 * Math.sin(3*Mi);
		sum += 8548 * Math.sin(4*D - 2*Mi);
		sum += -7888 * E * Math.sin(2*D + M - Mi);
		sum += -6766 * E * Math.sin(2*D + M);
		sum += -5163 * Math.sin(D - Mi);
		sum += 4987 * E * Math.sin(D + M);
		sum += 4036 * E * Math.sin(2*D - M + Mi);
		sum += 3994 * Math.sin(2*D + 2*Mi);
		sum += 3861 * Math.sin(4*D);
		sum += 3665 * Math.sin(2*D - 3*Mi);
		sum += -2689 * E * Math.sin(M - 2*Mi);
		sum += -2602 * Math.sin(2*D - Mi + 2*F);
		sum += 2390 * E * Math.sin(2*D - M - 2*Mi);
		sum += -2348 * Math.sin(D + Mi);
		sum += 2236 * (E*E) * Math.sin(2*D - 2*M);
		sum += -2120 * E * Math.sin(M + 2*Mi);
		sum += -2069 * (E*E) * Math.sin(2*M);
		sum += 2048 * (E*E) * Math.sin(2*D - 2*M - Mi);
		sum += -1773 * Math.sin(2*D + Mi - 2*F);
		sum += -1595 * Math.sin(2*D + 2*F);
		sum += 1215 * E * Math.sin(4*D - M - Mi);
		sum += -1110 * Math.sin(2*Mi + 2*F);
		sum += -892 * Math.sin(3*D - Mi);
		sum += -810 * E * Math.sin(2*D + M + Mi);
		sum += 759 * E * Math.sin(4*D - M - 2*Mi);
		sum += -713 * (E*E) * Math.sin(2*M - Mi);
		sum += -700 * (E*E) * Math.sin(2*D + 2*M - Mi);
		sum += 691 * E * Math.sin(2*D + M - 2*Mi);
		sum += 596 * E * Math.sin(2*D - M - 2*F);
		sum += 549 * Math.sin(4*D + Mi);
		sum += 537 * Math.sin(4*Mi);
		sum += 520 * E * Math.sin(4*D - M);
		sum += -487 * Math.sin(D - 2*Mi);
		sum += -399 * E * Math.sin(2*D + M - 2*F);
		sum += -381 * Math.sin(2*Mi - 2*F);
		sum += 351 * E * Math.sin(D + M + Mi);
		sum += -340 * Math.sin(3*D - 2*Mi);
		sum += 330 * Math.sin(4*D - 3*Mi);
		sum += 327 * E * Math.sin(2*D - M + 2*Mi);
		sum += -323 * (E*E) * Math.sin(2*M + Mi);
		sum += 299 * E * Math.sin(D + M - Mi);
		sum += 294 * Math.sin(2*D + 3*Mi);
		sum += Math.sin(2*D - Mi - 2*F);

		return sum;
	}
	
	
	/**
	 * Create a new Moon!
	 */
	public Moon() {
		this.colour = 0xcccccc;
		this.size = 10;
		this.name = "The Moon";
		this.onscreen = true;
	}

	/**
	 * Draw the Moon!
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
 