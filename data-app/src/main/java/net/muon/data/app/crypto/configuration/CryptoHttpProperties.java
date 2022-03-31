package net.muon.data.app.crypto.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("crypto.http")
@Validated
public class CryptoHttpProperties
{
    private BinanceHttpProperties binance;
    private GateioHttpProperties gateio;

    public BinanceHttpProperties getBinance()
    {
        return binance;
    }

    public void setBinance(BinanceHttpProperties binance)
    {
        this.binance = binance;
    }

    public GateioHttpProperties getGateio()
    {
        return gateio;
    }

    public void setGateio(GateioHttpProperties gateio)
    {
        this.gateio = gateio;
    }

    public static class BinanceHttpProperties extends AbstractHttpProperties {}

    public static class GateioHttpProperties extends AbstractHttpProperties {}
}
