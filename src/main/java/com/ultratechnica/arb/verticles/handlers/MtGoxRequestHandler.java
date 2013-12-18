package com.ultratechnica.arb.verticles.handlers;

import com.jayway.jsonpath.JsonPath;
import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.common.Exchanges;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;

import java.text.ParseException;

import static com.ultratechnica.arb.common.ResultFields.*;

/**
 * User: keithbishop
 * Date: 16/12/2013
 * Time: 00:20
 */
public class MtGoxRequestHandler extends AbstractRequestHandler {

    private static final JsonPath bidPricePath = JsonPath.compile("$.data.buy.value");

    private static final JsonPath askPricePath = JsonPath.compile("$.data.sell.value");

    private static final JsonPath lastClosedPricePath = JsonPath.compile("$.data.last.value");

    public MtGoxRequestHandler(Vertx vertx, Currencies currency) {
        super(currency, vertx);
        setExchange(Exchanges.MT_GOX);
    }

    protected Handler<Buffer> getDataHandler(final JsonObject result) {

        return new AbstractDataHandler() {

            protected void populatePrices(String json) throws ParseException {

                String bidPrice = bidPricePath.read(json);
                result.putNumber(BID_PRICE.getDisplayName(), new Double(bidPrice));

                String askPrice = askPricePath.read(json);
                result.putNumber(ASK_PRICE.getDisplayName(), new Double(askPrice));

                String lastClosedPrice = lastClosedPricePath.read(json);
                result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), new Double(lastClosedPrice));
            }
        };
    }
}
