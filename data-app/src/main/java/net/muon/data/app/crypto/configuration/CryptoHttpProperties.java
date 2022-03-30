package net.muon.data.app.crypto.configuration;

import net.muon.data.binance.BinanceHttpSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("crypto.http")
public class CryptoHttpProperties
{
    private BinanceHttpSource binance;
    private GateioHttpSource gateio;

    public BinanceHttpSource getBinance()
    {
        return binance;
    }

    public void setBinance(BinanceHttpSource binance)
    {
        this.binance = binance;
    }

    public GateioHttpSource getGateio()
    {
        return gateio;
    }

    public void setGateio(GateioHttpSource gateio)
    {
        this.gateio = gateio;
    }

    public static class BinanceHttpProperties extends AbstractHttpProperties {}

    public static class GateioHttpSource extends AbstractHttpProperties {}
}
