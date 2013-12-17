package com.ultratechnica.arb.common;

/**
 * User: keithbishop
 * Date: 10/12/2013
 * Time: 21:14
 */
public enum ResultFields {

    BID_PRICE("bidPrice"),
    ASK_PRICE("askPrice"),
    LAST_CLOSED_PRICE("lastClosedPrice"),
    DEVIATION("deviation");

    private String displayName;

    ResultFields(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
