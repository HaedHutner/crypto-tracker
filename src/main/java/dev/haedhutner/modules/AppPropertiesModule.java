package dev.haedhutner.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.impl.SimpleLoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;

public class AppPropertiesModule extends AbstractModule {

    @Provides
    @Singleton
    private Configuration properties() {
        var is = getClass().getClassLoader().getResourceAsStream("application.properties");

        var params = new Parameters();
        var builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(
                params.properties().setFileName("application.properties")
        );

        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            new SimpleLoggerFactory().getLogger(getClass().getName()).error("Could not load application properties. Using defaults.");
            return defaults();
        }
    }

    private Configuration defaults() {
        var defaultProps = new PropertiesConfiguration();

        defaultProps.addProperty("application.port", 8080);

        return defaultProps;
    }
}
