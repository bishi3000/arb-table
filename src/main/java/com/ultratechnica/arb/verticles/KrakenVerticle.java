package com.ultratechnica.arb.verticles;

import com.jayway.jsonpath.JsonPath;
import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.common.Exchanges;
import com.ultratechnica.arb.verticles.handlers.KrakenRequestHandler;
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
public class KrakenVerticle extends Verticle {



    @Override
    public void start() {

        System.out.println("Starting Kraken verticle...");

        // https://api.kraken.com/0/public/Ticker
        final HttpClient client = vertx.createHttpClient().setHost("api.kraken.com").setSSL(true).setPort(443);

        vertx.setPeriodic(20000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {

                HttpClientRequest request = client.post("/0/public/Ticker", getRequestHandler(vertx, Currencies.USD));
                request.setChunked(true);
                request.write("pair=XXBTZUSD");
                request.end();

                request = client.post("/0/public/Ticker", getRequestHandler(vertx, Currencies.EUR));
                request.setChunked(true);
                request.write("pair=XXBTZEUR");
                request.end();
            }
        });
    }

    private Handler<HttpClientResponse> getRequestHandler(Vertx vertx, Currencies currency) {
        return new KrakenRequestHandler(vertx, currency);
    }
}
