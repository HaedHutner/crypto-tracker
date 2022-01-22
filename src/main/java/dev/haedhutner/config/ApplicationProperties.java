package dev.haedhutner.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:application.properties"
})
public interface ApplicationProperties extends Config {

    @Key("application.port")
    @DefaultValue("8080")
    int port();

    @Key("application.referenceCurrency")
    @DefaultValue("USDT")
    String referenceCurrency();

    @Key("binance.api.token")
    String binanceApiToken();

    @Key("binance.api.secret")
    String binanceApiSecret();

    @Key("binance.api.url")
    String binanceHost();

}
