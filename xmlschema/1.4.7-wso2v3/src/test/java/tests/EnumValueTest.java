package tests;

import junit.framework.TestCase;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchema;

/**
 * Created by IntelliJ IDEA.
 * User: ajith
 */
public class EnumValueTest extends TestCase {


    public void testValue() throws Exception{
        //create a DOM document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().
                parse(Resources.asURI("enum.xsd"));

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema s = schemaCol.read(doc.getDocumentElement());

        assertNotNull(s);
    }
}
