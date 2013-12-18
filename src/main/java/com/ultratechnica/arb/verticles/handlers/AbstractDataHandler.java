package com.ultratechnica.arb.verticles.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;

import java.text.ParseException;

/**
 * User: keithbishop
 * Date: 18/12/2013
 * Time: 16:19
 */
public abstract class AbstractDataHandler implements Handler<Buffer> {

    @Override
    public void handle(Buffer buffer) {

        try {
            String json = buffer.toString();

            populatePrices(json);

        } catch (ParseException e) {
            System.out.println("Unable to parse response");
        }
    }

    protected abstract void populatePrices(String json) throws ParseException;

}
