package objects;
/** 
 * This class is used to house the details of a constellation.
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import java.util.Vector;

public class Constellation {
	
	// variables for the constellation name
	private String name_full;
	private String name_abr;
	// constellation lines
	private Vector lines;
	// visibility
	private int p=-1;
	private String url_info;
	private String further_info;
	private boolean highlighted = false;
	private boolean visible = false;
	
	
	/**
	 * Constellation constructor
	 * @param name_full - the full name
	 * @param name_abr - the abbreviated name
	 * @param url_info - a URL
	 * @param further_info - further text information
	 * @param the points for the constellation lines (redundent)
	 */
	public Constellation(String name_full, String name_abr, String url_info, String further_info, int p) {
		// record constellation data
		this.name_full = name_full;
		this.name_abr = name_abr;
		this.url_info = url_info;
		this.further_info = further_info;
		this.p = p;
	}
	
	/** 
	 * Get the full name.
	 * @return the full string name
	 */
	public String getFullName() {
		return this.name_full;
	}
	
	/**
	 * Get this constellations further info
	 * @return
	 */
	public String getFurtherInfo() {
		return this.further_info;
	}
	
	/**
	 * Get the abbreviated name.
	 * @return the abbreviated name of this constellation
	 */
	public String getAbrName() {
		return this.name_abr;
	}
	
	/**
	 * Is this constellation currently highlighted?
	 * @return
	 */
	public boolean isHighlighted() {
		return this.highlighted;
	}
	
	/**
	 * Adjust the highlight value for this constellation
	 * @param b new value for highlight
	 */
	public void highlight(boolean b) {
		this.highlighted = b;
	}
	
	/**
	 * Is this constellation current visible?
	 * @return
	 */
	public boolean isVisible() {
		return this.visible;
	}
	
	/**
	 * Adjust the visible value for this constellation
	 * @param b new value for visible
	 */
	public void visible(boolean b) {
		this.visible = b;
	}
	
	/**
	 * Get the constellation line instructions ptr
	 * @return points object ptr
	 */
	public int getPoints() {
		return this.p;
	}
	
	/**
	 * Set the constellation figure lines
	 * @param l = array of line
	 */
	public void setLines(Vector l) {
		this.lines = l;
	}
	
	/**
	 * Get the constellation figure lines
	 * @return
	 */
	public Vector getLines() {
		return this.lines;
	}
	
	/**
	 * Get Constellations URL info string
	 * @return
	 */
	public String getURLInfo() {
		return this.url_info;
	}
}
