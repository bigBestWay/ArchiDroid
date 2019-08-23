package parser;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	// List to hold ICC object
	private Set<ICCObject> iccObjSet = null;
	private ICCObject iccObj = null;
	private StringBuilder data = null;

	public Set<ICCObject> getIccObjSet() {
		return iccObjSet;
	}
	public void setIccObjSet(Set<ICCObject> iccObjSet) {
		this.iccObjSet = iccObjSet;
	}
	
	boolean bSource = false;
	boolean bTarget = false;
	//	boolean bMethod = false;
	//	boolean bWidget = false;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		data.append(new String(ch, start, length));
		//System.out.println("Data - > " + data.toString());
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub
		//super.endElement(arg0, arg1, arg2);

		String value = data.toString();
		if(value.contains("$")) {
			value = value.substring(0,value.indexOf("$"));
		}

		if (bSource) {
			iccObj.setSource(value);;
			bSource = false;
		} else if (bTarget) {
			iccObj.setTarget(value);
			bTarget = false;
		}
		if (qName.equalsIgnoreCase("ICC")) {
			// add ICC object to list
			iccObjSet.add(iccObj);
		}
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub

		if (qName.equalsIgnoreCase("ICC")) {
			// create a new ICC and put it in List
			// initialize ICC object 
			iccObj = new ICCObject();
			// initialize list
			if(iccObjSet == null) {
				iccObjSet = new LinkedHashSet<ICCObject>();
			}
		} else if (qName.equalsIgnoreCase("source")) {
			// set boolean values for fields, will be used in setting icc variables
			bSource = true;
		} else if (qName.equalsIgnoreCase("target")) {
			bTarget = true;
		} 
		// create the data container
		data = new StringBuilder();
	}
}

