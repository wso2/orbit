/*
* Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.h2.osgi.utils;

import java.util.Map;

public class H2Utils {
    public static void resetToDefaultParameters(Map h2Properties) {
        String carbonHome = CarbonUtils.getCarbonHome();
        String[] defaultParameters = new String[] { H2Constants.PARA_BASE_DIR };
        String[] defaultParametersValues = new String[] { carbonHome };
        for (int i = 0; i < defaultParameters.length; i++) {
            String para = defaultParameters[i];
            String value = defaultParametersValues[i];
            h2Properties.put(para, value);
        }
    }
}
