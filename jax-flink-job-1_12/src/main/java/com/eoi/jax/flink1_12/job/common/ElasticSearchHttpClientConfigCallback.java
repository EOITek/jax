/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.flink1_12.job.common;

import cn.hutool.core.util.StrUtil;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClientBuilder;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import javax.net.ssl.SSLContext;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ElasticSearchHttpClientConfigCallback implements RestClientBuilder.HttpClientConfigCallback {

    private static final String SUN_KRB5_LOGIN_MODULE = "com.sun.security.auth.module.Krb5LoginModule";
    private static final String CRED_CONF_NAME = "ESClientLoginConf";
    private static final Oid SPNEGO_OID = getSpnegoOid();

    private static Oid getSpnegoOid() {
        Oid oid = null;
        try {
            oid = new Oid("1.3.6.1.5.5.2");
        } catch (GSSException e) {
            e.printStackTrace();
        }
        return oid;
    }

    private String keytabPath;
    private String userPrincipalName;
    private SSLContext sc;
    private LoginContext loginContext;
    private String basicUsername;
    private String basicPassword;

    public ElasticSearchHttpClientConfigCallback(String keytabPath, String userPrincipalName, String basicUsername, String basicPassword) {
        this.keytabPath = keytabPath;
        this.userPrincipalName = userPrincipalName;
        this.basicUsername = basicUsername;
        this.basicPassword = basicPassword;
    }

    public synchronized LoginContext login() throws PrivilegedActionException {
        if (this.loginContext == null) {
            AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
                final Subject subject = new Subject(false,
                        Collections.singleton(new KerberosPrincipal(userPrincipalName)),
                        Collections.emptySet(), Collections.emptySet());
                Configuration conf = new KeytabJaasConf(userPrincipalName, keytabPath, false);
                loginContext = new LoginContext(CRED_CONF_NAME, subject, null, conf);
                loginContext.login();
                return null;
            });
        }
        return loginContext;
    }


    public static <T> T doAsPrivilegedWrapper(final Subject subject, final PrivilegedExceptionAction<T> action, final AccessControlContext acc)
            throws PrivilegedActionException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<T>) () -> Subject.doAsPrivileged(subject, action, acc));
        } catch (PrivilegedActionException pae) {
            if (pae.getCause() instanceof PrivilegedActionException) {
                throw (PrivilegedActionException) pae.getCause();
            }
            throw pae;
        }
    }

    private void setupSpnegoAuthSchemeSupport(HttpAsyncClientBuilder httpAsyncClientBuilder) {
        final Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory()).build();
        final GSSManager gssManager = GSSManager.getInstance();

        try {
            GSSName gssName = gssManager.createName(userPrincipalName, GSSName.NT_USER_NAME);
            login();
            AccessControlContext acc = AccessController.getContext();
            final GSSCredential credential = doAsPrivilegedWrapper(loginContext.getSubject(), () -> gssManager.createCredential(gssName,
                    GSSCredential.DEFAULT_LIFETIME, SPNEGO_OID, GSSCredential.INITIATE_ONLY), acc);

            KerberosCredentialsProvider credentialsProvider = new KerberosCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.SPNEGO), new KerberosCredentials(credential));
            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

        } catch (GSSException | PrivilegedActionException e) {
            e.printStackTrace();
        }
        httpAsyncClientBuilder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
    }

    @Override
    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {

        if (sc == null) {
            try {
                javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
                javax.net.ssl.TrustManager tm = new MITM();
                trustAllCerts[0] = tm;
                sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        httpClientBuilder.setSSLContext(sc);
        httpClientBuilder.setSSLHostnameVerifier((s, sslSession) -> true);

        if (StrUtil.isNotBlank(userPrincipalName) && StrUtil.isNotBlank(keytabPath)) {
            setupSpnegoAuthSchemeSupport(httpClientBuilder);
        } else if (StrUtil.isNotBlank(basicUsername) && StrUtil.isNotEmpty(basicPassword)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(basicUsername, basicPassword));
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }
        return httpClientBuilder;
    }

    private static class KeytabJaasConf extends Configuration {

        private String userPrincipalName;
        private String keytabFilePath;
        private boolean enableDebugLogs;

        public KeytabJaasConf(String userPrincipalName, String keytabFilePath, boolean enableDebugLogs) {
            this.userPrincipalName = userPrincipalName;
            this.enableDebugLogs = enableDebugLogs;
            this.keytabFilePath = keytabFilePath;
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            final Map<String, String> options = new HashMap<>();
            options.put("principal", userPrincipalName);
            options.put("refreshKrb5Config", Boolean.TRUE.toString());
            options.put("isInitiator", Boolean.TRUE.toString());
            options.put("storeKey", Boolean.TRUE.toString());
            options.put("renewTGT", Boolean.FALSE.toString());
            options.put("debug", Boolean.toString(enableDebugLogs));
            options.put("useKeyTab", Boolean.TRUE.toString());
            options.put("keyTab", keytabFilePath);
            options.put("doNotPrompt", Boolean.TRUE.toString());
            return new AppConfigurationEntry[]{new AppConfigurationEntry(SUN_KRB5_LOGIN_MODULE,
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, Collections.unmodifiableMap(options))};
        }
    }

    private static class KerberosCredentialsProvider implements CredentialsProvider {

        private AuthScope authScope;
        private Credentials credentials;

        @Override
        public void setCredentials(AuthScope authscope, Credentials credentials) {
            if (authscope.getScheme().regionMatches(true, 0, AuthSchemes.SPNEGO, 0, AuthSchemes.SPNEGO.length()) == false) {
                throw new IllegalArgumentException("Only " + AuthSchemes.SPNEGO + " auth scheme is supported in AuthScope");
            }
            this.authScope = authscope;
            this.credentials = credentials;
        }

        @Override
        public Credentials getCredentials(AuthScope authscope) {
            assert this.authScope != null && authscope != null;
            return authscope.match(this.authScope) > -1 ? this.credentials : null;
        }

        @Override
        public void clear() {
            this.authScope = null;
            this.credentials = null;
        }
    }

    private static class MITM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
