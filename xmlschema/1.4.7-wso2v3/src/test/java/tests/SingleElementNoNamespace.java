/**
 * 
 */
package tests;

import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;

import junit.framework.TestCase;


public class SingleElementNoNamespace extends TestCase {
	
	public void testReadOneElement() throws Exception {

		InputStream is = 
			SingleElementNoNamespace.class.
				getClassLoader().getResourceAsStream("singleElementNoNamespace.xsd");
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		XmlSchema schema = schemaCol.read(new StreamSource(is), null);
		XmlSchemaObjectTable objectTable = schema.getElements();
		assertEquals(1, objectTable.getCount());
	}
}
