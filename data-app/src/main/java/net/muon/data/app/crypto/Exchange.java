package net.muon.data.app.crypto;

public enum Exchange
{
    BINANCE("binance"),
    KUCOIN("kucoin"),
    GATEIO("gateio"),
    GEMINI("gemini"),
    KRAKEN("kraken"),
    BITFINEX("bitfinex"),
    BITFLYER("bitflyer"),
    BITSTAMP("bitstamp"),
    FTX("ftx"),
    HITBTC("hitbtc"),
    HUOBI("huobi"),
    COINBASE("coinbase"),

    UNISWAP_V2("uniswap_v2"),
    SUSHISWAP("sushiswap");

    private final String id;

    Exchange(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
