package com.ultratechnica.arb.verticles;

import com.jayway.jsonpath.JsonPath;
import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.common.Exchanges;
import com.ultratechnica.arb.verticles.handlers.MtGoxRequestHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.platform.Verticle;

import java.text.ParseException;

import static com.ultratechnica.arb.common.DataNames.USD_PRICE_DATA;
import static com.ultratechnica.arb.common.ResultFields.*;

/**
 * User: keithbishop
 * Date: 09/12/2013
 * Time: 17:45
 */
public class MtGoxVerticle extends Verticle {

    @Override
    public void start() {

        System.out.println("Starting MtGox verticle...");

        // http://data.mtgox.com/api/2/BTCUSD/money/ticker_fast
        final HttpClient client = vertx.createHttpClient().setHost("data.mtgox.com").setSSL(false).setPort(80);

        vertx.setPeriodic(20000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {

                HttpClientRequest request = client.get("/api/2/BTCUSD/money/ticker_fast", getRequestHandler(vertx, Currencies.USD));
                request.end();

                request = client.get("/api/2/BTCEUR/money/ticker_fast", getRequestHandler(vertx, Currencies.EUR));
                request.end();
            }
        });
    }

    private Handler<HttpClientResponse> getRequestHandler(Vertx vertx, Currencies currency) {
        return new MtGoxRequestHandler(vertx, currency);
    }
}
