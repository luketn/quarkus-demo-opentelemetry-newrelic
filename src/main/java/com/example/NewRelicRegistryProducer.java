package com.example;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import io.quarkus.arc.Priority;
import org.jboss.logging.Logger;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.interceptor.Interceptor;

@Singleton
public class NewRelicRegistryProducer {
    private static Logger log = Logger.getLogger(NewRelicRegistryProducer.class);

    /**
     * This producer is added as a bean by the Processor IFF the default registry
     * instance has been enabled.
     */
    @Produces
    @Singleton
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public NewRelicRegistry registry() {
        String new_relic_license_key = System.getenv("NEW_RELIC_LICENSE_KEY");
        if (new_relic_license_key == null || new_relic_license_key.isBlank()) {
            log.warn("NEW_RELIC_LICENSE_KEY is not set, so New Relic metrics will not be sent.");
            return null;
        }
        return NewRelicRegistry.builder(new NewRelicRegistryConfig() {
                    @Override
                    public String get(String key) {
                        {
                            if (key.equals("newrelic.step")) {
                                System.out.println("key " + key);

                            }
                        }
                        return switch (key) {
                            case "newrelic.apiKey" -> new_relic_license_key;
                            case "newrelic.uri" -> "https://metric-api.eu.newrelic.com/metric/v1";
                            case "newrelic.step" -> "1m";
                            default -> null;
                        };
                    }

                    @Override
                    public boolean useLicenseKey() {
                        return true;
                    }

                    @Override
                    public boolean enableAuditMode() {
                        return true;
                    }
                })
                .commonAttributes(new Attributes()
                        .put("service.name", "quarkus-demo")
                        .put("service.version", "1.0.0")
                        .put("service.instance.id", "1")
                        .put("deployment.environment", "dev")
                )
                .build();
    }
}
