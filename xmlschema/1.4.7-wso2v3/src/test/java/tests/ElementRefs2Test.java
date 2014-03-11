package tests;

import junit.framework.TestCase;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.apache.ws.commons.schema.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class ElementRefs2Test extends TestCase {
    
    public void testElementRefs() throws Exception {
        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                "attTests");
        InputStream is = new FileInputStream(Resources.asURI("elementreferences2.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);

        assertNotNull(elem);

        XmlSchemaComplexType cmplxType = (XmlSchemaComplexType)elem.getSchemaType();
        XmlSchemaObjectCollection items = ((XmlSchemaSequence)cmplxType.getParticle()).getItems();

        Iterator it = items.getIterator();
        while (it.hasNext()) {
            XmlSchemaElement innerElement =  (XmlSchemaElement)it.next();
            assertNotNull(innerElement.getRefName());
        }

        // test writing
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        schema.write(bos);

        //read this as a plain DOM and inspect our reference in question
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));

        //find the element with name="atttest" and test its type attribute
        //to see whether it includes a colon.
        NodeList elementList = document.getElementsByTagName("element");
        for(int i=0;i < elementList.getLength();i++){
            Node n = elementList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE &&
                    ((Element)n).hasAttribute("type")){
                assertTrue(((Element)n).getAttribute("type").indexOf(':') < 0);
            }
        }


    }

}
