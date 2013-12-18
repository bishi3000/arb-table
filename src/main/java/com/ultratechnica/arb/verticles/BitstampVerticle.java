package com.ultratechnica.arb.verticles;

import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.verticles.handlers.BitstampRequestHandler;
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
public class BitstampVerticle extends Verticle {

    @Override
    public void start() {

        System.out.println("Starting BitStamp verticle...");

        // https://www.bitstamp.net/api/ticker/
        final HttpClient client = vertx.createHttpClient().setHost("www.bitstamp.net").setSSL(true).setPort(443);

        vertx.setPeriodic(20000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {

                HttpClientRequest request = client.get("/api/ticker/", getRequestHandler(vertx, Currencies.USD));
                request.end();

            }
        });
    }

    private Handler<HttpClientResponse> getRequestHandler(Vertx vertx, Currencies currency) {
        return new BitstampRequestHandler(vertx, currency);
    }
}
