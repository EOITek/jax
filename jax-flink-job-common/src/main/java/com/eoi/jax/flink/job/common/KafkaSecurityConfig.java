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

package com.eoi.jax.flink.job.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.GSSAPI_MECHANISM;
import static org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_KERBEROS_SERVICE_NAME;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;
import static org.apache.kafka.common.config.SslConfigs.DEFAULT_SSL_ENABLED_PROTOCOLS;
import static org.apache.kafka.common.config.SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM;
import static org.apache.kafka.common.config.SslConfigs.DEFAULT_SSL_KEYSTORE_TYPE;
import static org.apache.kafka.common.config.SslConfigs.DEFAULT_SSL_TRUSTSTORE_TYPE;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEYSTORE_TYPE_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEY_PASSWORD_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG;

public class KafkaSecurityConfig implements Serializable {
    public static final String KEY_PLAIN_MECHANISM = "PLAIN";

    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setProperty(Properties properties, Map<String, Object> map, String key, Object defaultValue) {
        properties.putIfAbsent(key, map.getOrDefault(key, defaultValue));
    }

    public void authConfig(Properties properties, Map<String, Object> map) {
        String securityProtocol = map.getOrDefault(SECURITY_PROTOCOL_CONFIG,"").toString();
        if (!securityProtocol.isEmpty()) {
            properties.put(SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }

        /*
            是否开启SASL认证
            其他Kerberos配置需要flink-conf.yaml配置：
            https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/connectors/kafka.html#enabling-kerberos-authentication-for-versions-09-and-above-only
        */
        Boolean enableAuth = securityProtocol.contains("SASL");
        if (Boolean.TRUE.equals(enableAuth)) {
            setProperty(properties, map, SASL_MECHANISM, GSSAPI_MECHANISM);
            setProperty(properties, map, SASL_JAAS_CONFIG, "com.sun.security.auth.module.Krb5LoginModule required useTicketCache=true;");
            if (GSSAPI_MECHANISM.equals(map.getOrDefault(SASL_MECHANISM,GSSAPI_MECHANISM).toString())) {
                setProperty(properties, map, SASL_KERBEROS_SERVICE_NAME, "kafka");
            }
        }

        /*
            是否开启SSL
            https://kafka.apache.org/documentation/#security_configclients
        */
        Boolean enableSSL = securityProtocol.contains("SSL");
        if (Boolean.TRUE.equals(enableSSL)) {
            if (map.containsKey(SSL_TRUSTSTORE_LOCATION_CONFIG) && map.containsKey(SSL_TRUSTSTORE_PASSWORD_CONFIG)) {
                setProperty(properties, map, SSL_TRUSTSTORE_LOCATION_CONFIG, "");
                setProperty(properties, map, SSL_TRUSTSTORE_PASSWORD_CONFIG, "");
                setProperty(properties, map, SSL_TRUSTSTORE_TYPE_CONFIG, DEFAULT_SSL_TRUSTSTORE_TYPE);
            }
            if (map.containsKey(SSL_KEY_PASSWORD_CONFIG) && map.containsKey(SSL_KEYSTORE_LOCATION_CONFIG) && map.containsKey(SSL_KEYSTORE_PASSWORD_CONFIG)) {
                setProperty(properties, map, SSL_KEY_PASSWORD_CONFIG, "");
                setProperty(properties, map, SSL_KEYSTORE_LOCATION_CONFIG, "");
                setProperty(properties, map, SSL_KEYSTORE_PASSWORD_CONFIG, "");
                setProperty(properties, map, SSL_KEYSTORE_TYPE_CONFIG, DEFAULT_SSL_KEYSTORE_TYPE);
            }
            setProperty(properties, map, SSL_ENABLED_PROTOCOLS_CONFIG, DEFAULT_SSL_ENABLED_PROTOCOLS);

            // Server host name verification may be disabled by setting ssl.endpoint.identification.algorithm to an empty string
            // https://kafka.apache.org/documentation/#security_confighostname
            if (map.containsKey(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG)) {
                String sslEndpointIdentificationAlgorithm = DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM;
                if (map.get(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG) == null) {
                    sslEndpointIdentificationAlgorithm = "";
                } else {
                    sslEndpointIdentificationAlgorithm = map.get(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG).toString();
                }
                properties.put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, sslEndpointIdentificationAlgorithm);
            }
        }
        this.properties = properties;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> map = new HashMap<>();
        for (Object key : properties.keySet()) {
            map.put(key.toString(), properties.get(key));
        }
        return map;
    }

}
