/*
 * Created on 31.08.2008
 * (C) Copyright 2003-2008 Alexander Veit
 */


package tests;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * @author alex
 * $Revision: 726532 $
 */
public class WSCOMMONS377Test extends TestCase {

    public void testSchemaImport() throws Exception {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setNamespaceAware(true);

        String strUri = Resources.asURI("WSCOMMONS-377/importing.wsdl");
        Document doc = fac.newDocumentBuilder().parse(strUri);

        XmlSchemaCollection xsColl = new XmlSchemaCollection();
        xsColl.setBaseUri(Resources.TEST_RESOURCES + "/WSCOMMONS-377");

        NodeList nodesSchema = doc.getElementsByTagNameNS(Constants.URI_2001_SCHEMA_XSD, "schema");
        XmlSchema[] schemas = new XmlSchema[nodesSchema.getLength()];

        String systemIdBase = "urn:schemas";
        for (int i = 0; i < nodesSchema.getLength(); i++)
            schemas[i] = xsColl.read((Element)nodesSchema.item(i), systemIdBase + i);
    }
}
