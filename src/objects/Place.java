package objects;
/**
 * Representation of a place, consisting of a string name, longitude and latitude.
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
public class Place {

	// place variables
	private String country,city;
	private double longitude;
	private double latitude;
	
	
	/**
	 * Constructor for a new Place
	 * @param name
	 * @param longitude
	 * @param latitude
	 */
	public Place(String country, String city, double longitude, double latitude) {
		this.country = country;
		this.city = city;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * Get the country name
	 * @return String country name
	 */ 
	public String getCountry() {
		return this.country;
	}
	
	/**
	 * Get the city name
	 * @return String city name
	 */ 
	public String getCity() {
		return this.city;
	}	
	
	/**
	 * Get the place's longitude
	 * @return double longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}
	
	/**
	 * Get the place's latitude
	 * @return double latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}	
	
}
