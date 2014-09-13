package resources;
/**
 * This class represents an XML element (name and value).
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
public class XMLElement {

	// private variables
	private String name;
	private String value;
	
	/**
	 * Create new Element Object
	 * @param name
	 * @param value
	 */
	public XMLElement(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Get Element Name
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get Element Value
	 * @return
	 */
	public String getValue() {
		return this.value;
	}
	
	public String toString() {
		return "["+this.name+"] : " + this.value;
	}
}
