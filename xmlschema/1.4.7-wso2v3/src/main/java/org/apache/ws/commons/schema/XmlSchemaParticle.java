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

/**
 * Base class for all particle types.
 */

public class XmlSchemaParticle extends XmlSchemaAnnotated {

    long maxOccurs = 1;
    String maxOccursString;
    long minOccurs = 1;
    String minOccursString;

    /**
     * Creates new XmlSchemaParticle
     * Particle types are usually interchangeable.
     * A local element declaration or reference to a global element
     * declaration (element), a compositor ( sequence, choice, or all),
     * a reference to a named content model group (group), or an element wildcard (any).
     */
    public XmlSchemaParticle() {
    }

    public void setMaxOccurs(long maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public long getMaxOccurs() {
        return maxOccurs;
    }

    public void setMinOccurs(long minOccurs) {
        this.minOccurs = minOccurs;
    }

    public long getMinOccurs() {
        return minOccurs;
    }
}
