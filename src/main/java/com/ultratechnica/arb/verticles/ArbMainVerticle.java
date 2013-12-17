package com.ultratechnica.arb.verticles;

import org.vertx.java.platform.Verticle;

/**
 * User: keithbishop
 * Date: 11/12/2013
 * Time: 01:44
 */
public class ArbMainVerticle extends Verticle {

    @Override
    public void start() {

        System.out.println("Starting Main Verticle");
        container.deployVerticle("com.ultratechnica.arb.verticles.KrakenVerticle");
        container.deployVerticle("com.ultratechnica.arb.verticles.MtGoxVerticle");
        container.deployVerticle("com.ultratechnica.arb.verticles.BtceVerticle");
        container.deployVerticle("com.ultratechnica.arb.verticles.ArbHttpVerticle");
    }

    @Override
    public void stop() {

        System.out.println("Stopping Main Verticle");
        container.undeployVerticle("com.ultratechnica.arb.verticles.KrakenVerticle");
        container.undeployVerticle("com.ultratechnica.arb.verticles.MtGoxVerticle");
        container.undeployVerticle("com.ultratechnica.arb.verticles.BtceVerticle");
        container.undeployVerticle("com.ultratechnica.arb.verticles.ArbHttpVerticle");
    }
}
