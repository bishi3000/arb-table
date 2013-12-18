package com.ultratechnica.arb.verticles.handlers;

import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.common.Exchanges;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

import static com.ultratechnica.arb.common.DataNames.EUR_PRICE_DATA;
import static com.ultratechnica.arb.common.DataNames.USD_PRICE_DATA;

/**
 * User: keithbishop
 * Date: 18/12/2013
 * Time: 15:44
 */
public abstract class AbstractRequestHandler implements Handler<HttpClientResponse> {

    private Exchanges exchange;

    protected final Currencies currency;

    protected Vertx vertx;

    public AbstractRequestHandler(Currencies currency, Vertx vertx) {
        this.currency = currency;
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpClientResponse httpClientResponse) {
        System.out.println("Retrieved response from [" + getExchange().getDisplayName() + "]");

        final JsonObject result = new JsonObject();

        httpClientResponse.dataHandler(getDataHandler(result));
        httpClientResponse.endHandler(getEndHandler(result));
    }

    protected abstract Handler<Buffer> getDataHandler(final JsonObject result);

    protected VoidHandler getEndHandler(final JsonObject result) {

        final ConcurrentSharedMap<String, String> priceData = getPriceData();

        return new VoidHandler() {
            @Override
            protected void handle() {
                priceData.put(getExchange().getDisplayName(), result.encode());
            }
        };
    }

    protected ConcurrentSharedMap<String, String> getPriceData() {

        switch (currency) {
            case EUR: return vertx.sharedData().getMap(EUR_PRICE_DATA.name());
            case USD: return vertx.sharedData().getMap(USD_PRICE_DATA.name());
            default: return vertx.sharedData().getMap(USD_PRICE_DATA.name());
        }
    }

    public Exchanges getExchange() {
        return exchange;
    }

    public void setExchange(Exchanges exchange) {
        this.exchange = exchange;
    }
}
