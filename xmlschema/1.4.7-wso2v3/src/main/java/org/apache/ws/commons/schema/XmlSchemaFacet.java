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

package org.apache.ws.commons.schema;

import org.w3c.dom.Element;

/**
 * Abstract class for all facets that are used when simple types are
 * derived by restriction.
 */

public abstract class XmlSchemaFacet extends XmlSchemaAnnotated {

    /**
     * Creates new XmlSchemaFacet
     */


    protected XmlSchemaFacet() {
    }

    protected XmlSchemaFacet(Object value, boolean fixed) {
        this.value = value;
        this.fixed = fixed;
    }

    boolean fixed;

    Object value;

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static XmlSchemaFacet construct(Element el) {
        String name = el.getLocalName();
        boolean fixed = false;
        if (el.getAttribute("fixed").equals("true")) {
            fixed = true;
        }
        XmlSchemaFacet facet;
        if (name.equals("enumeration")) {
            facet = new XmlSchemaEnumerationFacet();
        } else if (name.equals("fractionDigits")) {
            facet = new XmlSchemaFractionDigitsFacet();
        } else if (name.equals("length")) {
            facet = new XmlSchemaLengthFacet();
        } else if (name.equals("maxExclusive")) {
            facet = new XmlSchemaMaxExclusiveFacet();
        } else if (name.equals("maxInclusive")) {
            facet = new XmlSchemaMaxInclusiveFacet();
        } else if (name.equals("maxLength")) {
            facet = new XmlSchemaMaxLengthFacet();
        } else if (name.equals("minLength")) {
            facet = new XmlSchemaMinLengthFacet();
        } else if (name.equals("minExclusive")) {
            facet = new XmlSchemaMinExclusiveFacet();
        } else if (name.equals("minInclusive")) {
            facet = new XmlSchemaMinInclusiveFacet();
        } else if (name.equals("pattern")) {
            facet = new XmlSchemaPatternFacet();
        } else if (name.equals("totalDigits")) {
            facet = new XmlSchemaTotalDigitsFacet();
        } else if (name.equals("whiteSpace")) {
            facet = new XmlSchemaWhiteSpaceFacet();
        } else {
            throw new XmlSchemaException("Incorrect facet with name \""
                                         + name + "\" found.");
        }
        if (el.hasAttribute("id"))facet.setId(el.getAttribute("id"));
        facet.setFixed(fixed);
        facet.setValue(el.getAttribute("value"));
        return facet;
    }
}
