package resources;
/**
 * This class performs all the standard astronomical calculations from Meeus.
 * @author Simon Fleming (sf58@sussex.ac.uk) 
 */
import java.util.Calendar;
import system.Application;
import system.Config;
import objects.Sun;

public class Calculations {
	
	// verbose mode
	private boolean verbose = false;
	
	/**
	 * Calculate the Julian Date for a given Y-M-D 
	 * taken directly from Astronomical Algorithms p.61
	 * @param y - year
	 * @param m - month
	 * @param d - day
	 * @return - the Julian Date for given date.
	 */
	public double julianDate(int y, int m, double d) {		
		/* save parameters */
		int 	Y = y;
		int 	M = m;
		double 	D = d;
		
		/* Adjust by timezone and daylight savings */

		/* functions on M */
		if(M>2) { 
			/* [ If M > 2, leave Y and M unchanged ] */
		} else if(M==1|M==2) {
			/* [ If M = 1 or 2, replace Y by Y -1. and M by M + 12 ] 
			 * In other words, if the date is in January or February,
			 * it is considered to be in the 13th or 14th month of the preceding year.
			 */
			Y = Y - 1;
			M = M + 12;
		}
		
		int A = (int)(Y/100);
		int B = 2 - A + (int)(A/4);

		/* Caculate the Julian Date */
		double JD = (int)(365.25*(Y+4716)) + (int)(30.6001*(M+1)) + D + B - 1524.5; // definitely bracketing!!
		if(verbose) {
			System.out.println("JD" + JD);
		}
		/* return the Julian Date */
		return JD;
	}		
	
	/**
	 * Calculate T
	 * @param y - year 
	 * @param m - month
	 * @param d - day
	 * @return - T
	 */
	public double T(int y, int m,  double d) {		
		return (julianDate(y,m,d)-2451545.0)/36525;
	}
	
	/**
	 * Calculate T_
	 * @param y - year 
	 * @param m - month
	 * @param d - day
	 * @return - T_
	 */
	public double T_(int y, int m,  double d) {		
		return (julianDate(y,m,d)-2451545.0)/365250;
	}
	

	/**
	 * Calculate the mean sidereal time at Greenwich for given date
	 * taken directly from Astronomical Algorithms p.87
	 * @param y - year
	 * @param m - month
	 * @param d - day
	 * @return - MST
	 */
	public double meanSiderealTime(int y, int m,  double d) {
		/* Adjust by timezone and daylight savings */
		
		double T = T(y, m, d);
		double MST = 280.46061837 + 360.98564736629 * (julianDate(y,m,d) - 2451545.0) 
					 + 0.000387933 * (T*T) - (T*T*T)/38710000;
   
		/* return the mean sidereal time in degrees ... by adding a convienient multiple of 360 */
		if(verbose) {
			System.out.println("MST: " +this.normalise(MST));
		}		
		return MST;
	}	
	
	/**
	 * Calculate the hour angle - taken directly from Meeus
	 * @param app - calling Application to gain access to Configuration
	 * @param RA  - RA of interest
	 * @return - HA for given RA
	 */
	public double hourAngle(Application app, double RA) {
		// collect configuration
		Config c = app.getConfig();
	    Calendar cal = c.getDateTime();
	    
	    // calculate day with time as decimal
	    double Cmin = cal.get(Calendar.MINUTE);
	    	   Cmin = Cmin/1440;
	    	   
	    double Cday = cal.get(Calendar.HOUR_OF_DAY);
	    	   Cday = Cday/24;
	    	   Cday = Cday + cal.get(Calendar.DAY_OF_MONTH);
	    	   Cday = Cday + Cmin;
	    	   
	    // month zero-based: jan =0 (http://www.programmerbase.com/17/j2me-calendar-issues-170336.shtm)
	    int m = cal.get(Calendar.MONTH)+1; 
	    
		// get meanSiderealTime for current date/time
		double MST = meanSiderealTime(cal.get(Calendar.YEAR), 
									  m,  
									  Cday); // Cday includes time of day

		// return hour angle, all as degrees
		double L = c.getLong();  // degrees
		double h = MST - L - Math.toDegrees(RA); //  degrees
		if(verbose) {
			System.out.println("H" + this.normalise(h));
		}		
		return h;
	}		
	
	/**
	 * Keep a number with range 0-360
	 * http://mathforum.org/library/drmath/view/60599.html
	 * @param t value to normalise
	 * @return normalised value between 0-360
	 */
	public double normalise(double t) {
		if(t<0) {
			// continually add multiples of 360
			return t + 360*Math.ceil(Math.abs(t/360));
		} else if(t>360) {
			// continually subtract multiples of 360
			double m  = 360*Math.floor(Math.abs(t/360));
			return t - m;
		} else {
			return t;
		}
	}
		
	/**
	 * Convert from ecliptical longitude/latitude to RA/DECLINATION. meeus p.93 
	 * @param lambda - longitude
	 * @param beta - latitude
	 * @param epsilon - true obliquity
	 * @return[0] - RA
	 * @return[1] - DECLINATION
	 */
	public double[] ecliptical_to_equatorial(double lambda1, double beta1, double epsilon1) {
		
		// return values
		double[] r = {0.0, 0.0};
		
		double lambda = Math.toRadians(lambda1);
		double beta = Math.toRadians(beta1);
		double epsilon = Math.toRadians(epsilon1);
		
		double RA = Float11.atan2(Math.sin(lambda) * Math.cos(epsilon) - Math.tan(beta) * Math.sin(epsilon), Math.cos(lambda));
		double sinDEC = Math.sin(beta) * Math.cos(epsilon) + Math.cos(beta) * Math.sin(epsilon) * Math.sin(lambda);
		double DEC = Float11.asin(sinDEC);
		
		r[0] = RA;
		r[1] = DEC;
		
		// return results
		return r;		
		
	}
	               
	
	
	/**
	 * Convert RA/DECLINATION to ALTITUDE/AZIMUTH.  meeus p.93 
	 * @param RA - right ascension
	 * @param DEC - declination
	 * @param App - calling application
	 * @return[0] - AZIMUTH
	 * @return[1] - ALTITUDE
	 */
	public double[] ra_dec_to_alt_azmiuth(double RA, double DEC, Application app) {
		
		// return values
		double[] r = {0.0, 0.0};
		
		// get configuration reference
		Config c1 = app.getConfig();	

		// RA/DEC to Radians for trig functions.
		RA = Math.toRadians(RA);
		DEC = Math.toRadians(DEC);
		
		// latitude / longitude to Radians for trig functions.
		double lat = Math.toRadians(c1.getLat());
		
		// get hour angle - IMPORTANT for trig functions!!!!
		double H = Math.toRadians(hourAngle(app, RA));		
		
		// calculate azimuth // note the correct quadrant from Meeus for aTan2.
		double A = Math.sin(H);
		double B = Math.cos(H) * Math.sin(lat) - Math.tan(DEC)*Math.cos(lat);			
		double sinazimuth = Float11.atan2(A, B);		
		r[0] = normalise(Math.toDegrees(sinazimuth)+180);

		// calculate altitude
		double tanalt = (Math.sin(lat)*Math.sin(DEC)) + (Math.cos(lat) * Math.cos(DEC) * Math.cos(H));		
		r[1] = Math.toDegrees(Float11.asin(tanalt));	
		
		// return results
		return r;

	}

	/**
	 * Calculating polynominals required for orbital element data
	 * @param a0
	 * @param a1
	 * @param a2
	 * @param a3
	 * @param T
	 * @return 
	 */
	public double ploynominal(double a0, double a1, double a2, double a3, double T) {
		return a0 + (a1*T) + (a2*(T*T)) + (a3*(T*T*T));
	}
	
	
	/**
	 * Convert orbital elements data to RA/DEC - used for all planet positions
	 * Meeus p.172 for geocentric rectangular coorinates X,Y,Z of the sun for J2000
	 * Meeus p.226 for second method of elliptical motion calculation
	 * @param a
	 * @param e
	 * @param i
	 * @param w
	 * @param omega
	 * @return[0] - RA
	 * @return[1] - DEC
	 */
	public double[] orbital_to_ra_dec(Application app, Sun sun, double a_, double e, double i, double w, double omega, double M_, double T, boolean flag) {
		
		// return values
		double[] r = {0.0, 0.0};
		
		// calculate n and M
		double n = 0.9856076686 / (a_ * Math.sqrt(a_));
		double M = M_ * n;
		
		// geocentric rectangular coorinates X,Y,Z of the sun for J2000
		double geolongj2000 = Math.toRadians(sun.getGeoLong()-0.01397*(app.getConfig().getYear(false)-2000));
		double Radius = sun.getR();
		
		// sun's geocentric latitude J2000.0 B: for each term: A cos (B +Ct)
		double B0 = 280 * Math.cos(3.199+(84334.662*T));
			  B0 += 102 * Math.cos(5.422+(5507.553*T));
			  B0 += 80  * Math.cos(3.88+(5223.69*T));
			  B0 += 44  * Math.cos(3.70+(2352.87*T));
			  B0 += 32  * Math.cos(4.00+(1577.34*T));
		double B1 = 227778 * Math.cos(3.413766+(6283.075850*T));
			  B1 += 3806 * Math.cos(3.3706+(12566.1517*T));
			  B1 += 3620 * Math.cos(0+(0*T));
			  B1 += 72 * Math.cos(3.33+(18849.23*T));
			  B1 += 8 * Math.cos(3.89+(5507.55*T));
			  B1 += 8 * Math.cos(1.79+(5223.69*T));
			  B1 += 6 * Math.cos(5.20+(2352.87*T));
	    double B2 = 9721 * Math.cos(5.1519+(6283.07585*T));
	    	  B2 += 233 * Math.cos(3.1416+(0*T));
	    	  B2 += 134 * Math.cos(0.644+(12566.152*T));
	    	  B2 += 7 * Math.cos(1.07+(18849.23*T));
	    double B3 = 276 * Math.cos(0.595+(6283.076*T));
	    	  B3 += 17 * Math.cos(3.14+(0*T));
	    	  B3 += 4 * Math.cos(0.12+(12566.15*T));
	    double B4 = 6 * Math.cos(2.27+(6283.08*T));
	    	  B4 += 1 * Math.cos(0+(0*T));
	    double B_ = (B0 + (B1*T) + (B2*(T*T)) + (B3*(T*T*T)) + (B4*(T*T*T*T))) / Float11.pow(10, 8);
	           B_ *= -1; // negate B_
	           
		// meeus 26.2 
		double X1 = Radius * Math.cos(B_) * Math.cos(geolongj2000);  
		double Y1 = Radius * Math.cos(B_) * Math.sin(geolongj2000);
		double Z1 = Radius * Math.sin(B_);
		
		// 26.3
		double X = X1 + (0.000000440360*Y1) - (0.000000190919*Z1); 
		double Y = (-0.000000479966*X1) + (0.917482137087 *Y1) - (0.397776982902*Z1);
		double Z = (0.397776982902*Y1) + (0.917482137087 * Z1);

		if(verbose) {
			System.out.println("==================================");
			System.out.println("Sun:");		
			System.out.println("long:" +Math.toDegrees(geolongj2000));
			System.out.println("T_:" +T);
			System.out.println("R:" +sun.getR());
			System.out.println("B:" +B_);
			System.out.println("X: " + X);
			System.out.println("Y: " + Y);
			System.out.println("Z: " + Z);
			System.out.println("==================================");
		}
		
		// obliquity of the ecliptic j2000.0 equinox ( meeus p.228
		double e_ = Math.toRadians(
					 23 // 23°26'21".448 
		 	       + (26/60)
		 	       + (21.448/3600));
		if(verbose) {
			System.out.println("sin e = " +Math.sin(e_));
			System.out.println("cos e = " +Math.cos(e_));
		}
		double sine = 0.397777156;
		double cose = 0.917482062;
		
		// then claculate
		double F = Math.cos(omega);
		double G = Math.sin(omega) * cose;
		double H = Math.sin(omega) * sine;
		double P = -Math.sin(omega) * Math.cos(i);
		double Q = Math.cos(omega) * Math.cos(i) * cose - Math.sin(i) * sine;
		double R = Math.cos(omega) * Math.cos(i) * sine + Math.sin(i) * cose;
		
		if(verbose) {
			System.out.println("==================================");
			System.out.println("F: " + F);
			System.out.println("G: " + G);
			System.out.println("H: " + H);
			System.out.println("P: " + P);
			System.out.println("Q: " + Q);
			System.out.println("R: " + R);
			System.out.println("==================================");
		}
		
		// check for relations, should both equal [1]
		double check1 = (F*F) + (G*G) + (H*H);
		double check2 = (P*P) + (Q*Q) + (R*R);
		if(verbose) {
			System.out.println("check1: " + check1);
			System.out.println("check2: " + check2);
		}
		
		// quantities a,b,c,A,B,C given by
		double A = Float11.atan2(F, P);
		double B = Float11.atan2(G, Q);
		double C = Float11.atan2(H, R);
		double a = Math.abs(Math.sqrt((F*F)+(P*P)));
		double b = Math.abs(Math.sqrt((G*G)+(Q*Q)));
		double c = Math.abs(Math.sqrt((H*H)+(R*R)));
		
		if(verbose) {
			System.out.println("==================================");
			System.out.println("A: " + normalise(Math.toDegrees(A)));
			System.out.println("B: " + normalise(Math.toDegrees(B)));
			System.out.println("C: " + normalise(Math.toDegrees(C)));
			System.out.println("a: " + a);
			System.out.println("b: " + b);
			System.out.println("c: " + c);
			System.out.println("==================================");				
			System.out.println("==================================");
			System.out.println("n: " + n);
			System.out.println("M: " + Math.toDegrees(M));
			System.out.println("==================================");
		}
		
		// eccentric anomaly E - fourth method ** BAD! too inaccurate.
		double E = Float11.atan2(Math.sin(M), Math.cos(M)-e); // Keplers equation solved here?! hardly... :(		

		// eccentric anomaly E : solving the equation of kepler!
		if(flag) {
			// Its M for comet! M_ for planets.
			E = kepler(M, e); 
		} else {
			E = kepler(M_, e); 
		}
		
		// true anomaly M
		double v = 2 * Float11.atan(Math.sqrt( (1+e) / (1-e) ) * Math.tan(E / 2)); 
			   
		// radius vector
		double r_ = a_ * (1 - e * Math.cos(E));
		
		// heliocentric rectangular equatorial coordinates of the body given by:
		double x = r_ * a * Math.sin(A + w + v);
		double y = r_ * b * Math.sin(B + w + v);
		double z = r_ * c * Math.sin(C + w + v);
		
		if(verbose) {
			System.out.println("==================================");
			System.out.println("E: " + Math.toDegrees(E));
			System.out.println("v: " + Math.toDegrees(v));
			System.out.println("r: " + r_);
			System.out.println("x: " + x);
			System.out.println("y: " + y);
			System.out.println("z: " + z);
			System.out.println("==================================");			
		}
			
		// additional definitions
		double X_ = X + x;
		double Y_ = Y + y;
		double Z_ = Z + z;
		
		if(verbose) {
			System.out.println("==================================");
			System.out.println("X_: " + X_);
			System.out.println("Y_: " + Y_);
			System.out.println("Z_: " + Z_);
			System.out.println("==================================");
		}
		
		// geocentric right ascension and declination
		r[0] = Float11.atan2(Y_, X_);
		if(r[0]<0) r[0] += Math.toRadians(360);
		r[1] = Float11.atan2(Z_, Math.sqrt((X_*X_) + (Y_*Y_)));
		
		if(verbose) {
			System.out.println("==================================");
			System.out.println("RA: " + Math.toDegrees(r[0]));
			System.out.println("DEC: " + Math.toDegrees(r[1]));
			System.out.println("==================================");
		}
		
		// return RA[0]/DEC[1]
		return r;
	}

	
	/**
	 * Solve Keplers equation: first method meeus p.196
	 * First Solution. chosing 32 iterations.
	 * @param M
	 * @param eo
	 * @return
	 */
	public double kepler(double M, double e) {
		double E = M;
		for(int i=0; i<32; i++) {
			E = M + e * Math.sin(E);
		}
		return E;
	}


}
