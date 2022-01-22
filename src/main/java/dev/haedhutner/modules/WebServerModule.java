package dev.haedhutner.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.haedhutner.config.ApplicationProperties;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;

public class WebServerModule extends AbstractModule {

    @Provides
    @Singleton
    public Javalin javalinServer(OpenApiOptions apiConfig, ApplicationProperties properties) {
        return Javalin.create(config -> config.registerPlugin(new OpenApiPlugin(apiConfig))).start(properties.port());
    }

}
