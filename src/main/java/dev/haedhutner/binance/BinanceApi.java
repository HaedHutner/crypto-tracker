package dev.haedhutner.binance;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import dev.haedhutner.binance.dto.AccountInfoDTO;
import dev.haedhutner.binance.dto.OrderDTO;
import dev.haedhutner.config.ApplicationProperties;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BinanceApi {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Inject
    private ApplicationProperties appProps;

    @Inject
    private OkHttpClient httpClient;

    @Inject
    private Logger logger;

    @Inject
    private Gson gson;

    public AccountInfoDTO fetchPortfolio() throws IOException {
        var req = binanceRequestNoBody("GET", "/api/v3/account", null);
        var response = httpClient.newCall(req).execute();

        return gson.fromJson(Objects.requireNonNull(response.body()).string(), AccountInfoDTO.class);
    }

    public List<OrderDTO> fetchOrders(String asset) throws IOException {
        if (appProps.referenceCurrency().equals(asset)) {
            return new ArrayList<>();
        }

        var req = binanceRequestNoBody("GET", "/api/v3/allOrders", new HashMap<>() {{
            put("symbol", asset + appProps.referenceCurrency());
        }});
        var resp = httpClient.newCall(req).execute();

        return gson.fromJson(Objects.requireNonNull(resp.body()).string(), new TypeToken<ArrayList<OrderDTO>>() { }.getType());
    }

    public double fetchPriceAtTime(String asset, long time) throws IOException {
        if (appProps.referenceCurrency().equals(asset)) {
            return 1.0d;
        }

        var endTime = Instant.ofEpochMilli(time).plus(5, ChronoUnit.MINUTES).toEpochMilli();

        var req = binanceRequestNoAuth("GET", "/api/v3/klines", new HashMap<>() {{
            put("symbol", asset + appProps.referenceCurrency());
            put("interval", "1m");
            put("startTime", String.valueOf(time));
            put("endTime", String.valueOf(endTime));
            put("limit", String.valueOf(1));
        }});

        var resp = httpClient.newCall(req).execute();

        ArrayList<ArrayList<Double>> result = gson.fromJson(Objects.requireNonNull(resp.body()).string(), new TypeToken<ArrayList<ArrayList<Double>>> () {}.getType());

        if (result.get(0) != null) {
            return result.get(0).get(4);
        } else {
            return 0.0d;
        }
    }

    private Request binanceRequestNoAuth(String method, String endpoint, Map<String,String> queryParams) {
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }

        var req = new Request.Builder();

        var url = HttpUrl.parse(appProps.binanceHost() + endpoint).newBuilder();
        queryParams.forEach(url::setQueryParameter);

        req.url(url.build());
        req.method(method, null);

        return req.build();
    }

    private Request binanceRequestNoBody(String method, String endpoint, Map<String,String> queryParams) {
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }

        var req = new Request.Builder();

        req.addHeader("X-MBX-APIKEY", appProps.binanceApiToken());

        var url = HttpUrl.parse(appProps.binanceHost() + endpoint).newBuilder();

        if (!queryParams.containsKey("timestamp")) {
            queryParams.put("timestamp", String.valueOf(Instant.now().toEpochMilli()));
        }

        queryParams.forEach(url::setQueryParameter);

        try {
            url.setQueryParameter("signature", generateSignature(queryParams));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        req.url(url.build());
        req.method(method, null);

        return req.build();
    }

    private String generateSignature(Map<String, String> queryParams) throws NoSuchAlgorithmException, InvalidKeyException {
        String queryParamString = queryParams.entrySet().stream()
                .map(e -> "%s=%s".formatted(e.getKey(), URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));

        var sha256 = Mac.getInstance("HmacSHA256");
        var key = new SecretKeySpec(appProps.binanceApiSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        sha256.init(key);

        return Hex.encodeHexString(sha256.doFinal(queryParamString.getBytes(StandardCharsets.UTF_8)));
    }
}
