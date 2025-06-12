/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com)
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.tiles;

import org.apache.tiles.definition.UrlDefinitionsFactory;
import org.apache.tiles.context.TilesRequestContext;

/**
 * This factory is created extending UrlDefinitionsFactory, which is the default
 * Tiles2 definition handler. Due to jndi issue with locale reading, we are extending
 * to Carbon specific needs
 */
public class CarbonUrlDefinitionsFactory extends UrlDefinitionsFactory {

    /**
     * In Carbon main definition will be main.layout and this will be processed in
     * deployment time. Thus, we can safely assume that the context is processed by this time
     * We are ignoring the Locale processing, which could be delelt with the time.
     */
    public boolean isContextProcessed(TilesRequestContext tilesContext) {
        return true;
    }

}

