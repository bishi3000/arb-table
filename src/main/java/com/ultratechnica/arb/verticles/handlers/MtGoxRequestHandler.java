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
 * Date: 16/12/2013
 * Time: 00:20
 */
public class MtGoxRequestHandler implements Handler<HttpClientResponse> {

    private static final JsonPath bidPricePath = JsonPath.compile("$.data.buy.value");

    private static final JsonPath askPricePath = JsonPath.compile("$.data.sell.value");

    private static final JsonPath lastClosedPricePath = JsonPath.compile("$.data.last.value");

    private final Currencies currency;

    private Vertx vertx;

    public MtGoxRequestHandler(Vertx vertx, Currencies currency) {
        this.vertx = vertx;
        this.currency = currency;
    }

    @Override
    public void handle(HttpClientResponse httpClientResponse) {

        System.out.println("Retrieved response from MtGox...");

        final JsonObject result = new JsonObject();

        httpClientResponse.dataHandler(getDataHandler(result));
        httpClientResponse.endHandler(getEndHandler(result));
    }

    private VoidHandler getEndHandler(final JsonObject result) {

        final ConcurrentSharedMap<String, String> priceData = getPriceData();

        return new VoidHandler() {
            @Override
            protected void handle() {
                priceData.put(Exchanges.MT_GOX.getDisplayName(), result.encode());
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

                    populatePrices(json, result);

                } catch (ParseException e) {
                    System.out.println("Unable to parse response");
                }
            }
        };
    }

    private void populatePrices(String json, JsonObject result) throws ParseException {

        String bidPrice = bidPricePath.read(json);
        result.putNumber(BID_PRICE.getDisplayName(), new Double(bidPrice));

        String askPrice = askPricePath.read(json);
        result.putNumber(ASK_PRICE.getDisplayName(), new Double(askPrice));

        String lastClosedPrice = lastClosedPricePath.read(json);
        result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), new Double(lastClosedPrice));
    }
}
