/**
 * Copyright (c) 2026, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.oltu.ssl;

import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

/**
 * Builds a trust-only SSLContext for outbound Oltu HTTPS calls so that a JVM-wide
 * {@code javax.net.ssl.keyStore*} configuration does not cause a client certificate to be
 * presented on a connection that only intends to authenticate via OAuth {@code client_secret}
 * (avoids an unintended TLS-client-auth + {@code client_secret} dual authentication).
 * <p>
 * Scopes SSL to the connection only — does not call {@link SSLContext#setDefault} and does not
 * modify {@link javax.net.ssl.HttpsURLConnection}'s static default SSLSocketFactory, so it has no
 * effect on any other connection in the process.
 */
public final class TrustOnlySslUtils {

    private static volatile SSLContext trustOnlySslContext;

    private TrustOnlySslUtils() {
    }

    /**
     * If {@code connection} is HTTPS, install a trust-only {@link javax.net.ssl.SSLSocketFactory}
     * on this connection instance so no client KeyManager / client certificate is used.
     * <p>
     * Fails closed: if the trust-only {@link SSLContext} cannot be built, this throws rather than
     * falling back to the JVM default SSLSocketFactory, since that default is exactly the state
     * this method exists to avoid (it may carry a client certificate).
     *
     * @param connection opened URL connection (may be plain HTTP)
     * @throws SSLException if the trust-only SSLContext could not be built
     */
    public static void applyTrustOnlySslIfHttps(HttpURLConnection connection) throws SSLException {

        if (!(connection instanceof HttpsURLConnection)) {
            return;
        }
        try {
            ((HttpsURLConnection) connection).setSSLSocketFactory(getTrustOnlySslContext().getSocketFactory());
        } catch (GeneralSecurityException e) {
            throw new SSLException("Unable to build trust-only SSLContext for outbound HTTPS call; "
                    + "refusing to fall back to the JVM default SSL context", e);
        }
    }

    private static SSLContext getTrustOnlySslContext() throws GeneralSecurityException {

        SSLContext cached = trustOnlySslContext;
        if (cached != null) {
            return cached;
        }
        synchronized (TrustOnlySslUtils.class) {
            if (trustOnlySslContext == null) {
                TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                // null KeyStore → default trust material (javax.net.ssl.trustStore* / cacerts)
                trustManagerFactory.init((KeyStore) null);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                // null KeyManagers → no client certificate
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
                trustOnlySslContext = sslContext;
            }
            return trustOnlySslContext;
        }
    }
}
