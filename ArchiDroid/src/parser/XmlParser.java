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
	// OLD
//	public void parseXML() {
//		factory = getFactory();
//		handler = getHandler();
//		saxParser = getSaxParser();
//
//		try {
//			saxParser.parse(new File(Utilities.FILE_PATH_AMANDROID_2), handler);
//		} catch (SAXException | IOException e) {
//			// TODO Auto-generated catch block
//			logger.error(e.toString());
//			//e.printStackTrace();
//		}
//
//		//Get ICC Component list
//		List<ICCObject> iccObjList = handler.getICCObjList();
//		//print icc component information
//		for(ICCObject iccObj : iccObjList) {
//			System.out.println("Source -> " + iccObj.getSource());
//			//System.out.println("Source Method -> " + iccObj.getMethod());
//			System.out.println("Target -> " + iccObj.getTarget());
//			System.out.println();
//		}
//
//	}
	
	/*
	 * parseXml method is to parse an XML file 
	 * to retrieve the caller component and callee component of an ICC method 
	 * and store them in a ComponentTransition List
	 */
	// Check with Set
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
			logger.info(TAG + " Source Comp -> " + callerComp);
			logger.info(TAG + " Target Comp -> " + calleeComp);

			componentTransitionList.add(componentTransition);
		}
		return componentTransitionList;
	}
	
	// Working with List
	public List<ComponentTransition> parseXml(String filePath) {
		factory = getFactory();
		handler = getHandler();
		saxParser = getSaxParser();

		List<ComponentTransition> componentTransitionList = new ArrayList<>();

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
			logger.info(TAG + " Source Comp -> " + callerComp);
			logger.info(TAG + " Target Comp -> " + calleeComp);

			componentTransitionList.add(componentTransition);
		}
		return componentTransitionList;
	}
	/*
	 * parseXml method is to parse an XML file 
	 * to retrieve the caller component and callee component of an ICC method 
	 * and store them in a ComponentTransition List
	 */
	// OLD
//	public List<ComponentTransition> parseXml() {
//		factory = getFactory();
//		handler = getHandler();
//		saxParser = getSaxParser();
//
//		List<ComponentTransition> componentTransitionList = new ArrayList<>();
//
//		try {
//			saxParser.parse(new File(Utilities.FILE_PATH_AMANDROID_2), handler);
//		} catch (SAXException | IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			logger.error(e.toString());
//			return componentTransitionList;
//		}
//
//		//Get ICC Component list
//		List<ICCObject> iccObjList = handler.getICCObjList();
//		//retrieve icc component information
//		for(ICCObject iccObj : iccObjList) {
//
//			String callerComp = iccObj.getSource();
//			String calleeComp = iccObj.getTarget();
//			ComponentTransition componentTransition = new ComponentTransition(callerComp, null, calleeComp);
//
//			logger.info(TAG + " Source Comp -> " + callerComp);
//			logger.info(TAG + " Target Comp -> " + calleeComp);
//
//			componentTransitionList.add(componentTransition);
//		}
//		return componentTransitionList;
//	}
	
	// OLD
//	public Set<ComponentTransition> parseXml_Set() {
//		factory = getFactory();
//		handler = getHandler();
//		saxParser = getSaxParser();
//
//		Set<ComponentTransition> componentTransitionSet = new LinkedHashSet<>();
//
//		try {
//			saxParser.parse(new File(Utilities.FILE_PATH_AMANDROID_2), handler);
//		} catch (SAXException | IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			logger.error(e.toString());
//			return componentTransitionSet;
//		}
//
//		//Get ICC Component list
//		//List<ICCObject> iccObjList = handler.getICCObjList();
//		Set<ICCObject> iccObjSet = handler.getIccObjSet();
//		//retrieve icc component information
//		for(ICCObject iccObj : iccObjSet) {
//
//			String callerComp = iccObj.getSource();
//			String calleeComp = iccObj.getTarget();
//			ComponentTransition componentTransition = new ComponentTransition(callerComp, null, calleeComp);
//
//			logger.info(TAG + " Source Comp -> " + callerComp);
//			logger.info(TAG + " Target Comp -> " + calleeComp);
//
//			componentTransitionSet.add(componentTransition);
//		}
//		return componentTransitionSet;
//	}
}
