package com.ultratechnica.arb.verticles;

import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.verticles.handlers.BtceRequestHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.platform.Verticle;

/**
 * User: keithbishop
 * Date: 09/12/2013
 * Time: 17:45
 */
public class BtceVerticle extends Verticle {

    @Override
    public void start() {

        System.out.println("Starting Btce verticle...");

        // https://btc-e.com/api/3/ticker/btc_usd
        final HttpClient client = vertx.createHttpClient().setHost("btc-e.com").setSSL(true).setPort(443);

        vertx.setPeriodic(20000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {

                HttpClientRequest request = client.get("/api/3/ticker/btc_usd", getRequestHandler(vertx, Currencies.USD));
                request.end();

                request = client.get("/api/3/ticker/btc_eur", getRequestHandler(vertx, Currencies.EUR));
                request.end();
            }
        });
    }

    private Handler<HttpClientResponse> getRequestHandler(Vertx vertx, Currencies currency) {
        return new BtceRequestHandler(vertx, currency);
    }
}
