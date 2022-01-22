package dev.haedhutner.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class OkHttpModule extends AbstractModule {

    @Provides
    @Singleton
    public OkHttpClient httpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

}
