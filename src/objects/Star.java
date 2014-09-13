package objects;
/**
 * Representation of a Star (Celestial Object) within the system.
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import resources.Calculations;
import system.Application;
import javax.microedition.lcdui.Graphics;

public class Star extends CelestialObject {

	// colour
	private String spectrum;
	// Bayer Designation
	private int bd;
	
	/**
	 * Star constructor
	 * @param RA - right ascension
	 * @param DEC - declination
	 * @param spectrum - spectrum
	 * @param magnitude - magnitude
	 * @param bd - bayer designation
	 */
	public Star(int ID, String name, double RA, double DEC, String spectrum, int magnitude, int bd) { 

		// update abstract
		super.setID(ID);
		super.setName(name.trim());
		
		// update self
		this.RA 		= RA;//*(Math.PI/180);
		this.DEC 		= DEC;//*(Math.PI/180);
		this.spectrum 	= spectrum;
		this.magnitude 	= magnitude;
		this.bd 		= bd;		
	}


	/**
	 * Return the stars spectrum
	 * @return spectrum
	 */
	public String getSpectrum() {
		return this.spectrum;
	}

	
	/**
	 * Get the bayer designation of this star
	 * @return bayer designation
	 */
	public int getBayer() {
		return this.bd;
	}	

	/**
	 * Convert the right ascension and declination to altitude and azimuth
	 * @param app - calling Application
	 * @param c - Calculation object
	 */
	public void calculatePosition(Application app, Calculations c) {
		
		// convert to local horizon coordinates
		double[] r = c.ra_dec_to_alt_azmiuth(this.RA,this.DEC, app);
		
		// record results
		azimuth = r[0];
		alt     = r[1];

		// this star is visible if its above horizon
		if(alt>0) {
			app.incCounter();
			this.visible = true;
		}

	}
	
	/**
	 * Draw this star!
	 */
	public void draw(Graphics g, int circle, int colour) {
		int modifier = this.size/2;
		if(this.isHighlighted()) {
			// if highlighted: highlight in green
			g.setColor(0x1eff00);
			g.fillArc(this.xPos-modifier-2, this.yPos-modifier-2, this.size+4, this.size+4, 0, 360);
		} else {
			// if not highlighted, apply a dark glow
			if(this.magnitude<2) {
				g.setColor(0x989898);
				g.fillArc((this.xPos-modifier)-1, (this.yPos-modifier)-1, this.size+2, this.size+2, 0, 360);
			} else {
				modifier = 0;
			}
		}
		// draw the actual star
		g.setColor(this.colour);
		g.fillArc((this.xPos-modifier), (this.yPos-modifier), size, size, 0, 360);		
	}
	
	/**
	 * Set stars azimuth
	 * @param x - new azimuth
	 */
	public void setAzimuth(int x) {
		this.azimuth = x;
	}
	
	/**
	 * Set stars altitude
	 * @param x - new altitude
	 */
	public void setAlt(int x) {
		this.alt = x;
	}
	
	
}
