package net.muon.data.app.crypto.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("crypto.ws")
public class CryptoWsProperties
{
    private Integer skipPricesAfterMillis;
    private BinanceWsProperties binance;
    private KucoinWsProperties kucoin;
    private GateioWsProperties gateio;
    private GeminiWsProperties gemini;
    private KrakenWsProperties kraken;

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

    public static class BinanceWsProperties extends AbstractWsProperties
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

    public static class KucoinWsProperties extends AbstractWsProperties {}

    public static class GateioWsProperties extends AbstractWsProperties {}

    public static class GeminiWsProperties extends AbstractWsProperties {}

    public static class KrakenWsProperties extends AbstractWsProperties
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
