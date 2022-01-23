package dev.haedhutner.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class GsonModule extends AbstractModule {

    @Provides
    @Singleton
    public Gson gson() {
        return new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();
    }

}
