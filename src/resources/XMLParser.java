package resources;
/**
 * taken directly and adapted from:
 * [http://www.devx.com/wireless/Article/28404/0/page/4]
 */
import java.util.Vector;

public class XMLParser {
	private String rawXML;
	private int index;
	
	/**
	 * Create a new XML Parser
	 * @param rawXML
	 */
	public XMLParser(String rawXML) {
		this.rawXML = rawXML;
		this.index = 0;
	}		
	
	/**
	 * Parse a XML document adding add tags a return vector
	 * @param tags
	 * @return
	 */
	public Vector parse(Vector tags) {
		Vector v = new Vector();
		int vi = 0;
		if(rawXML =="")
			return v;
			
		//extract the news
		while(index < rawXML.length()) {
			//extract the tag
			int prevIndex = index;
			String value = extractTag(index, (String)tags.elementAt(vi));
			XMLElement element = new XMLElement((String)tags.elementAt(vi++), value);
			v.addElement(element);
			System.out.println(index + " | " +element);
			if(vi==tags.size()) {
				// reset tags index
				vi = 0;
			}
			// dont allow loop back
			if(index<prevIndex) {
				index = rawXML.length()+1;
				v.removeElementAt(v.size()-1);
			}
		}
		return v;
	}
	
	/**
	 * Extract a tag
	 * @param beginIndex
	 * @param type
	 * @return
	 */
	private String extractTag(int beginIndex, String type) {
		String startTag = "<" + type + ">";
		String endTag = "</" + type + ">";
		//find the index of startTag in rawNews, starting from beginIndex
		int begin = rawXML.indexOf(startTag, beginIndex);
		//move 3 chars ahead to point to the begin of the actual text (title or descr.)
		begin += startTag.length();
		//find the index of endTag in rawNews, starting from begin
		int end = rawXML.indexOf(endTag, begin);
		//update index
		index = end + endTag.length();
		// return the actual text representing a title or a description
		return rawXML.substring(begin, end);
		
	}	
	
}
