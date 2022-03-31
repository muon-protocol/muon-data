package net.muon.data.app.crypto.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.app.crypto.Exchange;
import net.muon.data.binance.BinanceHttpSource;
import net.muon.data.gateio.GateioHttpSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class CryptoHttpConfiguration
{
    @Bean
    @ConditionalOnProperty(name = "crypto.http.binance.enabled", havingValue = "true")
    public BinanceHttpSource binanceHttpSource()
    {
        return new BinanceHttpSource(Exchange.BINANCE.getId());
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.http.gateio.enabled", havingValue = "true")
    public GateioHttpSource gateioHttpSource(HttpClient httpClient, ObjectMapper objectMapper)
    {
        return new GateioHttpSource(Exchange.GATEIO.getId(), httpClient, objectMapper);
    }
}