package net.muon.data.app.crypto.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("crypto.ws")
@Validated
public class CryptoWsProperties
{
    private Integer skipPricesAfterMillis;
    private BinanceWsProperties binance;
    private KucoinWsProperties kucoin;
    private GateioWsProperties gateio;
    private GeminiWsProperties gemini;
    private KrakenWsProperties kraken;
    private BitfinexWsProperties bitfinex;
    private BitflyerWsProperties bitflyer;
    private BitstampWsProperties bitstamp;
    private FtxWsProperties ftx;
    private HitbtcWsProperties hitbtc;
    private HuobiWsProperties huobi;
    private CoinbaseWsProperties coinbase;

    public Integer getSkipPricesAfterMillis()
    {
        return skipPricesAfterMillis;
    }

    public void setSkipPricesAfterMillis(Integer skipPricesAfterMillis)
    {
        this.skipPricesAfterMillis = skipPricesAfterMillis;
    }

    public BinanceWsProperties getBinance()
    {
        return binance;
    }

    public void setBinance(BinanceWsProperties binance)
    {
        this.binance = binance;
    }

    public KucoinWsProperties getKucoin()
    {
        return kucoin;
    }

    public void setKucoin(KucoinWsProperties kucoin)
    {
        this.kucoin = kucoin;
    }

    public GateioWsProperties getGateio()
    {
        return gateio;
    }

    public void setGateio(GateioWsProperties gateio)
    {
        this.gateio = gateio;
    }

    public GeminiWsProperties getGemini()
    {
        return gemini;
    }

    public void setGemini(GeminiWsProperties gemini)
    {
        this.gemini = gemini;
    }

    public KrakenWsProperties getKraken()
    {
        return kraken;
    }

    public void setKraken(KrakenWsProperties kraken)
    {
        this.kraken = kraken;
    }

    public BitfinexWsProperties getBitfinex()
    {
        return bitfinex;
    }

    public void setBitfinex(BitfinexWsProperties bitfinex)
    {
        this.bitfinex = bitfinex;
    }

    public BitflyerWsProperties getBitflyer()
    {
        return bitflyer;
    }

    public void setBitflyer(BitflyerWsProperties bitflyer)
    {
        this.bitflyer = bitflyer;
    }

    public BitstampWsProperties getBitstamp()
    {
        return bitstamp;
    }

    public void setBitstamp(BitstampWsProperties bitstamp)
    {
        this.bitstamp = bitstamp;
    }

    public FtxWsProperties getFtx()
    {
        return ftx;
    }

    public void setFtx(FtxWsProperties ftx)
    {
        this.ftx = ftx;
    }

    public HitbtcWsProperties getHitbtc()
    {
        return hitbtc;
    }

    public void setHitbtc(HitbtcWsProperties hitbtc)
    {
        this.hitbtc = hitbtc;
    }

    public HuobiWsProperties getHuobi()
    {
        return huobi;
    }

    public void setHuobi(HuobiWsProperties huobi)
    {
        this.huobi = huobi;
    }

    public CoinbaseWsProperties getCoinbase() {
        return coinbase;
    }

    public void setCoinbase(CoinbaseWsProperties coinbase) {
        this.coinbase = coinbase;
    }


    public static class BinanceWsProperties extends AbstractXchangeWsProperties {}

    public static class KucoinWsProperties extends AbstractWsProperties {}

    public static class GateioWsProperties extends AbstractWsProperties {}

    public static class GeminiWsProperties extends AbstractWsProperties {}

    public static class KrakenWsProperties extends AbstractXchangeWsProperties {}

    public static class BitfinexWsProperties extends AbstractXchangeWsProperties {}

    public static class BitflyerWsProperties extends AbstractXchangeWsProperties {}

    public static class BitstampWsProperties extends AbstractXchangeWsProperties {}

    public static class FtxWsProperties extends AbstractXchangeWsProperties {}

    public static class HitbtcWsProperties extends AbstractXchangeWsProperties {}

    public static class HuobiWsProperties extends AbstractXchangeWsProperties {}

    public static class CoinbaseWsProperties extends AbstractXchangeWsProperties {}

    public static abstract class AbstractXchangeWsProperties extends AbstractWsProperties
    {
        private String apiKey;
        private String secret;

        public String getApiKey()
        {
            return apiKey;
        }

        public void setApiKey(String apiKey)
        {
            this.apiKey = apiKey;
        }

        public String getSecret()
        {
            return secret;
        }

        public void setSecret(String secret)
        {
            this.secret = secret;
        }
    }
}
