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
import static com.ultratechnica.arb.common.ResultFields.*;

/**
 * User: keithbishop
 * Date: 15/12/2013
 * Time: 23:18
 */
public class BitstampRequestHandler extends AbstractRequestHandler {

    private static final JsonPath bidPricePathUSD = JsonPath.compile("$.last");

    private static final JsonPath askPricePathUSD = JsonPath.compile("$.ask");

    private static final JsonPath lastClosedPricePathUSD = JsonPath.compile("$.bid");

    public BitstampRequestHandler(Vertx vertx, Currencies currency) {
        super(currency, vertx);
        setExchange(Exchanges.BIT_STAMP);
    }

    @Override
    protected Handler<Buffer> getDataHandler(final JsonObject result) {

        return new AbstractDataHandler() {

            protected void populatePrices(String json) throws ParseException {

                if (currency == Currencies.USD) {

                    String bidPrice = bidPricePathUSD.read(json);
                    result.putNumber(BID_PRICE.getDisplayName(), new Double(bidPrice));


                    String askPrice = askPricePathUSD.read(json);
                    result.putNumber(ASK_PRICE.getDisplayName(), new Double(askPrice));

                    String lastClosed = lastClosedPricePathUSD.read(json);
                    result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), new Double(lastClosed));

                }
            }
        };
    }
}
