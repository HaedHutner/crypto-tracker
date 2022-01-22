package dev.haedhutner.controller;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.binance.BinanceApi;
import dev.haedhutner.binance.dto.AccountInfoDTO;
import dev.haedhutner.controller.dto.AverageBuyPriceDTO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AverageBuyPriceController {

    private Javalin server;

    private BinanceApi binanceApi;

    private Gson gson;

    private Logger logger;

    @Inject
    public AverageBuyPriceController(Javalin server, BinanceApi api, Gson gson, Logger logger) {
        this.server = server;
        this.binanceApi = api;
        this.gson = gson;
        this.logger = logger;

        avgbuy();
    }

    public void avgbuy() {
        server.get(
                "/api/avgbuy",
                OpenApiBuilder.documented(
                        OpenApiBuilder.document().jsonArray("200", AverageBuyPriceDTO.class),
                        this::calculateAverageBuyPrice
                )
        );
    }

    public void calculateAverageBuyPrice(Context ctx) throws IOException {
        var result = new ArrayList<AverageBuyPriceDTO>();

        var portfolio = binanceApi.fetchPortfolio().balances.stream()
                .filter(b -> (b.locked + b.free) > 0.0f)
                .toList();

        portfolio.forEach(b -> {
            try {
                logger.info("START " + b.asset);
                var orders = binanceApi.fetchOrders(b.asset);

                if (orders.size() < 1) {
                    return;
                }

                orders = orders.stream()
                        .filter(o -> Objects.equals(o.status, "FILLED") && Objects.equals(o.side, "BUY"))
                        .peek(o -> {
                            if (Objects.equals(o.type, "MARKET")) {
                                try {
                                    o.price = binanceApi.fetchPriceAtTime(b.asset, o.time);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .sorted(Comparator.comparingLong(o -> o.time))
                        .collect(Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Collections.reverse(list);
                                    return list;
                                })
                        );


                var totalQty = b.free + b.locked;
                MutableDouble accumQty = new MutableDouble(0.0d);

                var firstElement = orders.get(0);

                orders = orders.stream().takeWhile(o -> {
                    accumQty.add(o.executedQty);
                    return accumQty.doubleValue() <= totalQty + (totalQty * 0.01);
                }).toList();

                if (orders.size() < 1) {
                    orders = List.of(firstElement);
                }

                double[] values = orders.stream().mapToDouble(o -> o.price).toArray();
                double[] weights = orders.stream().mapToDouble(o -> o.executedQty).toArray();

                var dto = new AverageBuyPriceDTO();
                dto.symbol = b.asset;
                dto.avgBuyPrice = new Mean().evaluate(values, weights);

                result.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("END " + b.asset);
        });

        ctx.result(gson.toJson(result));
    }
}
