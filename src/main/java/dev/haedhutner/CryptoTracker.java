package dev.haedhutner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.haedhutner.modules.AppPropertiesModule;
import dev.haedhutner.modules.LoggingModule;
import dev.haedhutner.modules.OpenApiModule;

public class CryptoTracker {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new LoggingModule(),
                new OpenApiModule(),
                new AppPropertiesModule()
        );

        injector.getInstance(CryptoTrackerWebServer.class).start();
    }

}
