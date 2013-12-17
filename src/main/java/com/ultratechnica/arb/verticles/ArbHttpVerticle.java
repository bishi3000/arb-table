package com.ultratechnica.arb.verticles;

import com.ultratechnica.arb.common.Currencies;
import com.ultratechnica.arb.common.Exchanges;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.*;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.platform.Verticle;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.ultratechnica.arb.common.DataNames.EUR_PRICE_DATA;
import static com.ultratechnica.arb.common.DataNames.USD_PRICE_DATA;
import static com.ultratechnica.arb.common.ResultFields.*;

/**
 * User: keithbishop
 * Date: 10/12/2013
 * Time: 11:18
 */
public class ArbHttpVerticle extends Verticle {

    private static final String WEB_ROOT = "/Users/keithbishop/Lasso/Arbitrage_new/arbitrage/src/main/resources/html/";

    @Override
    public void start() {

        System.out.println("Starting ArbHttpVerticle...");

        RouteMatcher rm = new RouteMatcher();

        rm.getWithRegEx("/.*\\.(html|css|js)", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest httpServerRequest) {

                System.out.println("received request for " + httpServerRequest.path());

                String path = httpServerRequest.path();

                if (path.endsWith(".html")) {
                    httpServerRequest.response().sendFile(WEB_ROOT + "arb-table.html");
                } else {
                    httpServerRequest.response().sendFile(WEB_ROOT + httpServerRequest.path());
                }
            }
        });

        rm.get("/arb-table-data/:currency", new Handler<HttpServerRequest>() {

            @Override
            public void handle(final HttpServerRequest httpServerRequest) {

                String c = httpServerRequest.params().get("currency");
                Currencies currency = Currencies.valueOf(c);

                System.out.println("received request for /arb-table-data, currency [" + c + "]");

                HttpServerResponse response = httpServerRequest.response();
                response.headers().set("Content-Type", "application/json");
                response.setChunked(true);

                JsonObject result = getPriceData(currency);
                response.write(result.encode());
                response.end();
            }
        });

        vertx.createHttpServer().requestHandler(rm).listen(8080);
    }

    private JsonObject getPriceData(Currencies currency) {

        ConcurrentSharedMap<String ,String> arbTable = getPriceDataMap(currency);

        JsonObject result = new JsonObject();

        if (arbTable == null || arbTable.isEmpty()) {
            result.putString("message", "no data to display");
        } else {

            JsonObject tableData = new JsonObject();
            result.putObject("results", tableData);

            Map<String, JsonObject> prices = new LinkedHashMap<>();

            String krakenPrice = arbTable.get(Exchanges.KRAKEN.getDisplayName());
            prices.put(Exchanges.KRAKEN.getDisplayName(), new JsonObject(krakenPrice));

            String mtGoxPrice = arbTable.get(Exchanges.MT_GOX.getDisplayName());
            prices.put(Exchanges.MT_GOX.getDisplayName(), new JsonObject(mtGoxPrice));

            String btcePrice = arbTable.get(Exchanges.BTC_E.getDisplayName());
            prices.put(Exchanges.BTC_E.getDisplayName(), new JsonObject(btcePrice));

            buildPriceTable(tableData, prices);
        }

        return result;
    }

    private void buildPriceTable(JsonObject tableData, Map<String, JsonObject> prices) {
        for (Map.Entry<String, JsonObject> buyEntry : prices.entrySet()) {

            JsonObject row = new JsonObject();
            Number bidPrice = buyEntry.getValue().getNumber(BID_PRICE.getDisplayName());

            for (Map.Entry<String, JsonObject> sellEntry : prices.entrySet()) {
                JsonObject cell = new JsonObject();

                Number askPrice = sellEntry.getValue().getNumber(ASK_PRICE.getDisplayName());

                double deviation = (askPrice.doubleValue() - bidPrice.doubleValue()) / (askPrice.doubleValue()/100);

                cell.putNumber(DEVIATION.getDisplayName(), deviation);
                cell.putNumber(BID_PRICE.getDisplayName(), bidPrice);
                cell.putNumber(ASK_PRICE.getDisplayName(), askPrice);

                row.putObject(sellEntry.getKey(), cell);
            }
            tableData.putObject(buyEntry.getKey(), row);
        }
    }

    private ConcurrentSharedMap<String ,String> getPriceDataMap(Currencies currency) {
        switch (currency) {
            case USD: return vertx.sharedData().getMap(USD_PRICE_DATA.name());
            case EUR: return vertx.sharedData().getMap(EUR_PRICE_DATA.name());
            default: return vertx.sharedData().getMap(USD_PRICE_DATA.name());
        }
    }
}
