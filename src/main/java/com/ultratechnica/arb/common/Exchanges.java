package com.ultratechnica.arb.common;

/**
 * User: keithbishop
 * Date: 10/12/2013
 * Time: 18:33
 */
public enum Exchanges {

    KRAKEN("Kraken"),
    MT_GOX("MtGox"),
    BTC_E("BTC-e")
    ;

    private String displayName;

    Exchanges(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
