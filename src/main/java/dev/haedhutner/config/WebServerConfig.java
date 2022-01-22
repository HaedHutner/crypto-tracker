package dev.haedhutner.config;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebServerConfig {

    @Inject
    private OpenApiOptions apiConfig;

    @Inject
    private Configuration properties;

    public Javalin createServer() {
        return Javalin.create(config -> config.registerPlugin(new OpenApiPlugin(apiConfig))).start(properties.getInt("application.port"));
    }

}
