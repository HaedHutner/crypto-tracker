package dev.haedhutner.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;

import javax.inject.Singleton;

public class OpenApiModule extends AbstractModule {
    @Provides
    @Singleton
    private OpenApiOptions openApiConfig() {
        return new OpenApiOptions(
                new Info()
                        .version("1.0")
                        .description("Crypto Tracker API")
        )
                .swagger(new SwaggerOptions("/swagger").title("Swagger UI"))
                .path("/swagger-docs");
    }
}
