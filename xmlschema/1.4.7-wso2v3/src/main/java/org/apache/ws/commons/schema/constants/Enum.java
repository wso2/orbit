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

package org.apache.ws.commons.schema.constants;

public abstract class Enum {

    public static String NULL = "NULL";

    protected Enum(String value) {
        setValue(value);
    }

    protected Enum() {
        this(NULL);
    }

    protected abstract String[] getValues();

    protected String value = NULL;

    public void setValue(String value) {
        if (value.equals(Enum.NULL))
            this.value = Enum.NULL;
        else {
            //the value can be a list of space seperated items
            String possibleValues[] = getValues();
            String[] valuesToBeTested = value.split("\\s");
            for (int i = 0; i < valuesToBeTested.length; i++) {
                for (int j = 0; j < possibleValues.length; j++) {
                    if (possibleValues[j].equals(valuesToBeTested[i])) {
                        break;
                    }
                    if (i == possibleValues.length - 1)
                        throw new EnumValueException("Bad Enumeration value '" + value + "'");
                }
            }

            //when we reach here we have tested all the values to be correct (applicable)
             this.value = value;


        }
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }

    public boolean equals(Object what) {
        return what.getClass().equals(this.getClass()) &&
                ((Enum) what).getValue().equals(this.getValue());
    }

    public static class EnumValueException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public EnumValueException(String mesg) {
            super(mesg);
        }
    }

    protected static final int index(String value, String values[]) {
        for (int i = 0; i < values.length; i++) {
            if (value.equals(values[i]))
                return i;
        }
        return -1;
    }
}


