package com.ultratechnica.arb.verticles.handlers;

import com.jayway.jsonpath.JsonPath;
import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.common.Exchanges;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

import java.text.ParseException;

import static com.ultratechnica.arb.common.DataNames.EUR_PRICE_DATA;
import static com.ultratechnica.arb.common.DataNames.USD_PRICE_DATA;
import static com.ultratechnica.arb.common.ResultFields.ASK_PRICE;
import static com.ultratechnica.arb.common.ResultFields.BID_PRICE;
import static com.ultratechnica.arb.common.ResultFields.LAST_CLOSED_PRICE;

/**
 * User: keithbishop
 * Date: 15/12/2013
 * Time: 23:18
 */
public class BtceRequestHandler implements Handler<HttpClientResponse> {

    private static final JsonPath bidPricePathUSD = JsonPath.compile("$.btc_usd.last");

    private static final JsonPath askPricePathUSD = JsonPath.compile("$.btc_usd.buy");

    private static final JsonPath lastClosedPricePathUSD = JsonPath.compile("$.btc_usd.sell");

    private static final JsonPath bidPricePathEUR = JsonPath.compile("$.btc_eur.last");

    private static final JsonPath askPricePathEUR = JsonPath.compile("$.btc_eur.buy");

    private static final JsonPath lastClosedPricePathEUR = JsonPath.compile("$.btc_eur.sell");

    private final Currencies currency;

    private Vertx vertx;

    public BtceRequestHandler(Vertx vertx, Currencies currency) {
        this.currency = currency;
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpClientResponse httpClientResponse) {
        System.out.println("Retrieved response from BTC-e...");

        final JsonObject result = new JsonObject();

        httpClientResponse.dataHandler(getDataHandler(result));
        httpClientResponse.endHandler(getEndHandler(result));
    }

    private VoidHandler getEndHandler(final JsonObject result) {

        final ConcurrentSharedMap<String, String> priceData = getPriceData();

        return new VoidHandler() {
            @Override
            protected void handle() {
                priceData.put(Exchanges.BTC_E.getDisplayName(), result.encode());
            }
        };
    }

    private ConcurrentSharedMap<String, String> getPriceData() {

        switch (currency) {
            case EUR: return vertx.sharedData().getMap(EUR_PRICE_DATA.name());
            case USD: return vertx.sharedData().getMap(USD_PRICE_DATA.name());
            default: return vertx.sharedData().getMap(USD_PRICE_DATA.name());
        }
    }

    private Handler<Buffer> getDataHandler(final JsonObject result) {

        return new Handler<Buffer>() {
            @Override
            public void handle(Buffer buffer) {

                try {
                    String json = buffer.toString();

                    populatePrices(json);

                } catch (ParseException e) {
                    System.out.println("Unable to parse response");
                }
            }

            private void populatePrices(String json) throws ParseException {

                if (currency == Currencies.USD) {

                    result.putNumber(BID_PRICE.getDisplayName(), bidPricePathUSD.<Double>read(json));
                    result.putNumber(ASK_PRICE.getDisplayName(), askPricePathUSD.<Double>read(json));
                    result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), lastClosedPricePathUSD.<Double>read(json));

                } else if (currency == Currencies.EUR) {

                    result.putNumber(BID_PRICE.getDisplayName(), bidPricePathEUR.<Double>read(json));
                    result.putNumber(ASK_PRICE.getDisplayName(), askPricePathEUR.<Double>read(json));
                    result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), lastClosedPricePathEUR.<Double>read(json));
                }
            }
        };
    }
}
