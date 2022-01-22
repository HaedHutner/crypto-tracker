package dev.haedhutner.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.impl.SimpleLoggerFactory;


public class LoggingModule extends AbstractModule {
    @Provides
    @Singleton
    private Logger logger() {
        return new SimpleLoggerFactory().getLogger("Crypto Tracker");
    }
}
