package dev.haedhutner.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import dev.haedhutner.config.ApplicationProperties;
import org.aeonbits.owner.ConfigFactory;

import javax.inject.Singleton;

public class AppPropertiesModule extends AbstractModule {

    @Provides
    @Singleton
    private ApplicationProperties properties() {
        return ConfigFactory.create(ApplicationProperties.class);
    }
}
