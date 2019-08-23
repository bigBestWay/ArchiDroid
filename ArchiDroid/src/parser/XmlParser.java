package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


import models.ComponentTransition;
import utils.Utilities;

/**
 * @author - Tanjina Islam
 *
 * @date - 26-05-2019
 */

/*
 * XmlParser class is to parse the component transition graph(in .xml format) of Amandroid 
 * to retrieve the relevant information only(i.e. Source Component and Target Component of an ICC)
 */
public class XmlParser {

	private final static Logger logger = LoggerFactory.getLogger(XmlParser.class);
	private final static String TAG = "[" + XmlParser.class.getSimpleName() + "]";

	private static XmlParser instance = null;
	private SAXParserFactory factory;
	private SAXParser saxParser;
	private XMLHandler handler;

	public SAXParser getSaxParser() {
		if(saxParser == null)
			try {
				saxParser = factory.newSAXParser();
			} catch (ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.toString());
			}
		return saxParser;
	}
	public void setSaxParser(SAXParser saxParser) {
		this.saxParser = saxParser;
	}
	public XMLHandler getHandler() {
		if(handler == null)
			handler = new XMLHandler();
		return handler;
	}
	public void setHandler(XMLHandler handler) {
		this.handler = handler;
	}
	public SAXParserFactory getFactory() {
		if(factory == null)
			factory = SAXParserFactory.newInstance();
		return factory;
	}
	public void setFactory(SAXParserFactory factory) {
		this.factory = factory;
	}
	private XmlParser() {

	}
	public static XmlParser getInstance() {
		if(instance == null) {
			instance = new XmlParser();
		}
		return instance;
	}
	
	/*
	 * parseXml method is to parse an XML file 
	 * to retrieve the caller component and callee component of an ICC method 
	 * and store them in a ComponentTransition List
	 */
	public Set<ComponentTransition> parseXml_Set(String filePath) {
		factory = getFactory();
		handler = getHandler();
		saxParser = getSaxParser();

		Set<ComponentTransition> componentTransitionList = new LinkedHashSet<ComponentTransition>();

		try {
			saxParser.parse(new File(filePath), handler);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.toString());
			return componentTransitionList;
		}

		//Get ICC Component list
		Set<ICCObject> iccObjList = handler.getIccObjSet();
		//retrieve icc component information
		for(ICCObject iccObj : iccObjList) {

			String callerComp = iccObj.getSource();
			String calleeComp = iccObj.getTarget();
			ComponentTransition componentTransition = new ComponentTransition(callerComp, null, calleeComp);
			componentTransition.setLinkType(Utilities.LinkType.ICC);
			componentTransition.setStyle(null);
//			logger.info(TAG + " Source Comp -> " + callerComp);
//			logger.info(TAG + " Target Comp -> " + calleeComp);

			componentTransitionList.add(componentTransition);
		}
		return componentTransitionList;
	}
}
