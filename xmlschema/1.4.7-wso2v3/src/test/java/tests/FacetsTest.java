/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package tests;

import junit.framework.TestCase;
import org.apache.ws.commons.schema.*;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FacetsTest extends TestCase {

    /**
     * This method will test for the length facet.
     *
     * @throws Exception Any exception encountered
     */
    public void testLengthFacet() throws Exception {

        /*
        <simpleType name="zipCode">
          <restriction base="string">
            <length value="5"/>
            <pattern value="\d{5}"/>
          </restriction>
        </simpleType>
        <element name="myZipCode" type="tns:zipCode"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myZipCode");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myZipCode", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myZipCode"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "zipCode"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "string"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(2, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaLengthFacet.class.getName());
        s.add(XmlSchemaPatternFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaLengthFacet) {
                assertEquals("5", ((XmlSchemaLengthFacet)o).getValue());
                assertEquals(false, ((XmlSchemaLengthFacet)o).isFixed());
                String toStr = ((XmlSchemaLengthFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"length\", but did contain: " + toStr,
                           toStr.indexOf("length value=\"5\"") != -1);
            } else if (o instanceof XmlSchemaPatternFacet) {
                assertEquals("\\d{5}", ((XmlSchemaPatternFacet)o).getValue());
                assertEquals(false, ((XmlSchemaPatternFacet)o).isFixed());
                String toStr = ((XmlSchemaPatternFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"pattern\", but did contain: " + toStr,
                           toStr.indexOf("pattern value=\"\\d{5}\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This method will test for the pattern facet.
     *
     * @throws Exception Any Exception encountered
     */
    public void testPatternFacet() throws Exception {

        /*
        <simpleType name="creditCardNumber">
          <restriction base="integer">
            <pattern value="\d{15}"/>
          </restriction>
        </simpleType>
        <element name="myCreditCardNumber" type="tns:creditCardNumber"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myCreditCardNumber");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myCreditCardNumber", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myCreditCardNumber"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "creditCardNumber"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(1, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaPatternFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaPatternFacet) {
                assertEquals("\\d{15}", ((XmlSchemaPatternFacet)o).getValue());
                assertEquals(false, ((XmlSchemaPatternFacet)o).isFixed());
                String toStr = ((XmlSchemaPatternFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"pattern\", but did contain: " + toStr,
                           toStr.indexOf("pattern value=\"\\d{15}\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     *  This method will test the total digits facet.
     *
     * @throws Exception Any exception encountered
     */
    public void testTotalDigitsFacet() throws Exception {

        /*
        <simpleType name="age">
          <restriction base="decimal">
            <totalDigits value="3"/>
          </restriction>
        </simpleType>
        <element name="myAge" type="tns:age"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myAge");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myAge", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myAge"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "age"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "decimal"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(1, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaTotalDigitsFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaTotalDigitsFacet) {
                assertEquals("3", ((XmlSchemaTotalDigitsFacet)o).getValue());
                assertEquals(false, ((XmlSchemaTotalDigitsFacet)o).isFixed());
                String toStr = ((XmlSchemaTotalDigitsFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"totalDigits\", but did contain: " + toStr,
                           toStr.indexOf("totalDigits value=\"3\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This method will test the Min and Max Inclusive facets.
     *
     * @throws Exception Any Exception encountered
     */
    public void testMinMaxInclusiveFacets() throws Exception {

        /*
        <simpleType name="distance">
          <restriction base="integer">
            <maxInclusive value="100" fixed="true"/>
            <minInclusive value="0"/>
          </restriction>
        </simpleType>
        <element name="myDistance" type="tns:distance"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myDistance");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myDistance", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myDistance"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "distance"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(2, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaMaxInclusiveFacet.class.getName());
        s.add(XmlSchemaMinInclusiveFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaMaxInclusiveFacet) {
                assertEquals("100", ((XmlSchemaMaxInclusiveFacet)o).getValue());
                assertEquals(true, ((XmlSchemaMaxInclusiveFacet)o).isFixed());
                String toStr = ((XmlSchemaMaxInclusiveFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"maxInclusive\", but did contain: " + toStr,
                           toStr.indexOf("maxInclusive value=\"100\"") != -1);
            } else if (o instanceof XmlSchemaMinInclusiveFacet) {
                assertEquals("0", ((XmlSchemaMinInclusiveFacet)o).getValue());
                assertEquals(false, ((XmlSchemaMinInclusiveFacet)o).isFixed());
                String toStr = ((XmlSchemaMinInclusiveFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"minInclusive\", but did contain: " + toStr,
                           toStr.indexOf("minInclusive value=\"0\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This method will test the Min and Max Exclusive facets.
     *
     * @throws Exception Any Exception encountered
     */
    public void testMinMaxExlusiveFacets() throws Exception {

        /*
        <simpleType name="weight">
          <restriction base="integer">
            <maxExclusive value="200"/>
            <minExclusive value="1"/>
          </restriction>
        </simpleType>
        <element name="myWeight" type="tns:weight"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myWeight");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myWeight", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myWeight"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "weight"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(2, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaMaxExclusiveFacet.class.getName());
        s.add(XmlSchemaMinExclusiveFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaMaxExclusiveFacet) {
                assertEquals("200", ((XmlSchemaMaxExclusiveFacet)o).getValue());
                assertEquals(false, ((XmlSchemaMaxExclusiveFacet)o).isFixed());
                String toStr = ((XmlSchemaMaxExclusiveFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"maxExclusive\", but did contain: " + toStr,
                           toStr.indexOf("maxExclusive value=\"200\"") != -1);
            } else if (o instanceof XmlSchemaMinExclusiveFacet) {
                assertEquals("1", ((XmlSchemaMinExclusiveFacet)o).getValue());
                assertEquals(false, ((XmlSchemaMinExclusiveFacet)o).isFixed());
                String toStr = ((XmlSchemaMinExclusiveFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"minExclusive\", but did contain: " + toStr,
                           toStr.indexOf("minExclusive value=\"1\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This will test the whiteSpace facet.
     *
     * @throws Exception Any Exception encountered
     */
    public void testWhiteSpaceFacet() throws Exception {

        /*
        <simpleType name="noWhiteSpace">
          <restriction base="integer">
            <whiteSpace value="collapse"/>
          </restriction>
        </simpleType>
        <element name="myWhiteSpace" type="tns:noWhiteSpace"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myWhiteSpace");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myWhiteSpace", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myWhiteSpace"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "noWhiteSpace"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "normalizedString"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(1, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaWhiteSpaceFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaWhiteSpaceFacet) {
                assertEquals("collapse", ((XmlSchemaWhiteSpaceFacet)o).getValue());
                assertEquals(false, ((XmlSchemaWhiteSpaceFacet)o).isFixed());
                String toStr = ((XmlSchemaWhiteSpaceFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"minExclusive\", but did contain: " + toStr,
                           toStr.indexOf("whiteSpace value=\"collapse\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This will test the fractionDigits facet.
     *
     * @throws Exception Any Exception encountered
     */
    public void testFractionDigitsFacet() throws Exception {

        /*
        <simpleType name="height">
          <restriction base="decimal">
            <totalDigits value="3"/>
            <fractionDigits value="2"/>
          </restriction>
        </simpleType>
        <element name="myHeight" type="tns:height"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myHeight");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myHeight", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myHeight"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "height"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "decimal"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(2, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaFractionDigitsFacet.class.getName());
        s.add(XmlSchemaTotalDigitsFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaFractionDigitsFacet) {
                assertEquals("2", ((XmlSchemaFractionDigitsFacet)o).getValue());
                assertEquals(false, ((XmlSchemaFractionDigitsFacet)o).isFixed());
                String toStr = ((XmlSchemaFractionDigitsFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"fractionDigits\", but did contain: " + toStr,
                           toStr.indexOf("fractionDigits value=\"2\"") != -1);
            } else if (o instanceof XmlSchemaTotalDigitsFacet) {
                assertEquals("3", ((XmlSchemaTotalDigitsFacet)o).getValue());
                assertEquals(false, ((XmlSchemaTotalDigitsFacet)o).isFixed());
                String toStr = ((XmlSchemaTotalDigitsFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"totalDigits\", but did contain: " + toStr,
                           toStr.indexOf("totalDigits value=\"3\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This method will test the Min and Max Length facets.
     *
     * @throws Exception Any Exception encountered
     */
    public void testMinMaxLengthFacets() throws Exception {

        /*
        <simpleType name="yardLength">
          <restriction base="nonNegativeInteger">
            <minLength value="45"/>
            <maxLength value="205"/>
          </restriction>
        </simpleType>
        <element name="myYardLength" type="tns:yardLength"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "myYardLength");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("myYardLength", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "myYardLength"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "yardLength"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(2, collection.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaMinLengthFacet.class.getName());
        s.add(XmlSchemaMaxLengthFacet.class.getName());
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            Object o = i.next();
            assertTrue(s.remove(o.getClass().getName()));
            if (o instanceof XmlSchemaMinLengthFacet) {
                assertEquals("45", ((XmlSchemaMinLengthFacet)o).getValue());
                assertEquals(false, ((XmlSchemaMinLengthFacet)o).isFixed());
                String toStr = ((XmlSchemaMinLengthFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"minExclusive\", but did contain: " + toStr,
                           toStr.indexOf("minLength value=\"45\"") != -1);
            } else if (o instanceof XmlSchemaMaxLengthFacet) {
                assertEquals("205", ((XmlSchemaMaxLengthFacet)o).getValue());
                assertEquals(false, ((XmlSchemaMaxLengthFacet)o).isFixed());
                String toStr = ((XmlSchemaMaxLengthFacet)o).toString("xsd", 1);
                assertTrue("The toString(String, int) method did not contain "
                           + "\"maxLength\", but did contain: " + toStr,
                           toStr.indexOf("maxLength value=\"205\"") != -1);
            } else {
                fail("Unexpected object encountered: " + o.getClass().getName());
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

    /**
     * This method will test the enumeration facet.
     *
     * @throws Exception Any Exception encountered
     */
    public void testEnumerationFacet() throws Exception {
        
        /*
        <simpleType name="layoutComponentType">
          <restriction base="string">
            <enumeration value="Field"/>
            <enumeration value="Separator"/>
          </restriction>
        </simpleType>
        <element name="layoutComponent" type="tns:layoutComponentType"/>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "layoutComponent");
        InputStream is = new FileInputStream(Resources.asURI("facets.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("layoutComponent", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "layoutComponent"),
                     elem.getQName());
        assertEquals(new QName("http://soapinterop.org/types", "layoutComponentType"),
                     elem.getSchemaTypeName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        
        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "string"),
                     r.getBaseTypeName());
        
        XmlSchemaSimpleType xsst = r.getBaseType();
        assertNull(xsst);

        XmlSchemaObjectCollection collection = r.getFacets();
        assertEquals(2, collection.getCount());

        Set s = new HashSet();
        s.add("Field");
        s.add("Separator");
        for (Iterator i  = collection.getIterator(); i.hasNext(); ) {
            XmlSchemaEnumerationFacet xsef = (XmlSchemaEnumerationFacet)i.next();
            String value = (String)xsef.getValue();
            assertTrue("Atempted to remove an enumeration with the value of "
                       + "\"" + value + "\", but the value was not in the set.",
                       s.remove(value));
            String toStr = xsef.toString("xsd", 1);
            if (value.equals("Field")) {
                assertTrue("The toString(String, int) method did not contain "
                           + "\"enumeration\", but did contain: " + toStr,
                           toStr.indexOf("enumeration value=\"Field\"") != -1);
            } else if (value.equals("Separator")) {
                assertTrue("The toString(String, int) method did not contain "
                           + "\"enumeration\", but did contain: " + toStr,
                           toStr.indexOf("enumeration value=\"Separator\"") != -1);
            }
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}
