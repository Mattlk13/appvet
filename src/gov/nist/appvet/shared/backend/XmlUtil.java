/* This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 United States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS".  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof including, but
 * not limited to, the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 */
package gov.nist.appvet.shared.backend;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides XML utility functions.
 * 
 * @author steveq@nist.gov
 */
public class XmlUtil {
	private static final Logger log = AppVetProperties.log;
	private Document document = null;

	public XmlUtil(File xmlFile) {
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		try {
			if (!xmlFile.exists()) {
				if (log == null) {
					System.err.println("File " + xmlFile.getName()
							+ " does not exist.");
				} else {
					log.error("File " + xmlFile.getName() + " does not exist.");
				}
			}
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			// Fortify Security Recommendation for Entity Expansion Injection
			docBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
			// Fortify Security Recommendation for External Entity Injection
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			
			docBuilder = docBuilderFactory.newDocumentBuilder();
			document = docBuilder.parse(xmlFile);
		} catch (final Exception e) {
			if (log == null) {
				e.printStackTrace();
			} else {
				log.error(e.toString());
			}
		} finally {
			docBuilder = null;
			docBuilderFactory = null;
		}
	}

	public boolean xpathExists(String nodePath) {
		if (nodePath == null) {
			return false;
		}
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node node = null;
		try {
			node = (Node) xPath.evaluate(nodePath,
					document.getDocumentElement(), XPathConstants.NODE);
			if (node == null) {
				return false;
			} else {
				return true;
			}
		} catch (final Exception e) {
			log.error(e.toString());
		} finally {
			node = null;
			xPath = null;
		}
		return false;
	}

	public String getXPathValue(String nodePath) {
		if (nodePath == null) {
			return null;
		}
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node node = null;
		try {
			node = (Node) xPath.evaluate(nodePath,
					document.getDocumentElement(), XPathConstants.NODE);
			if (node == null) {
				return null;
			}
			final String elementValue = node.getFirstChild().getNodeValue()
					.trim();
			if (elementValue != null && !elementValue.isEmpty()) {
				return elementValue;
			}
		} catch (final Exception e) {
			log.error(e.toString());
		} finally {
			node = null;
			xPath = null;
		}
		return null;
	}

	public ArrayList<String> getXPathValues(String nodePath) {
		final ArrayList<String> values = new ArrayList<String>();
		XPath xPath = null;
		NodeList nodes = null;
		Element element = null;
		String elementValue = null;
		try {
			xPath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) xPath.evaluate(nodePath,
					document.getDocumentElement(), XPathConstants.NODESET);
			if (nodes == null || nodes.getLength() == 0) {
				return null;
			}
			for (int i = 0; i < nodes.getLength(); ++i) {
				element = (Element) nodes.item(i);
				elementValue = element.getFirstChild().getNodeValue().trim();
				if (elementValue != null && !elementValue.isEmpty()) {
					values.add(elementValue);
				}
			}
		} catch (final XPathExpressionException e) {
			log.error(e.toString());
		} finally {
			elementValue = null;
			element = null;
			nodes = null;
			xPath = null;
		}
		return values;
	}
}