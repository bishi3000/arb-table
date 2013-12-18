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
 * Time: 00:37
 */
public class KrakenRequestHandler extends AbstractRequestHandler {

    private static final JsonPath bidPricePathUSD = JsonPath.compile("$.result.XXBTZUSD.b[0]");

    private static final JsonPath askPricePathUSD = JsonPath.compile("$.result.XXBTZUSD.a[0]");

    private static final JsonPath lastClosedPricePathUSD = JsonPath.compile("$.result.XXBTZUSD.c[0]");

    private static final JsonPath bidPricePathEUR = JsonPath.compile("$.result.XXBTZEUR.b[0]");

    private static final JsonPath askPricePathEUR = JsonPath.compile("$.result.XXBTZEUR.a[0]");

    private static final JsonPath lastClosedPricePathEUR = JsonPath.compile("$.result.XXBTZEUR.c[0]");

    public KrakenRequestHandler(Vertx vertx, Currencies currency) {
        super(currency, vertx);
        setExchange(Exchanges.KRAKEN);
    }

    protected Handler<Buffer> getDataHandler(final JsonObject result) {

        return new AbstractDataHandler() {

            protected void populatePrices(String json) throws ParseException {

                if (currency == Currencies.USD) {

                    String bidPrice = bidPricePathUSD.read(json);
                    result.putNumber(BID_PRICE.getDisplayName(), new Double(bidPrice));

                    String askPrice = askPricePathUSD.read(json);
                    result.putNumber(ASK_PRICE.getDisplayName(), new Double(askPrice));

                    String lastClosedPrice = lastClosedPricePathUSD.read(json);
                    result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), new Double(lastClosedPrice));

                } else if (currency == Currencies.EUR) {

                    String bidPrice = bidPricePathEUR.read(json);
                    result.putNumber(BID_PRICE.getDisplayName(), new Double(bidPrice));

                    String askPrice = askPricePathEUR.read(json);
                    result.putNumber(ASK_PRICE.getDisplayName(), new Double(askPrice));

                    String lastClosedPrice = lastClosedPricePathEUR.read(json);
                    result.putNumber(LAST_CLOSED_PRICE.getDisplayName(), new Double(lastClosedPrice));
                }
            }
        };
    }
}
