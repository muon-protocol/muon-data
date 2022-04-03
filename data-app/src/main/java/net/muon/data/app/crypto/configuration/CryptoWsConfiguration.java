package net.muon.data.app.crypto.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.app.crypto.Exchange;
import net.muon.data.binance.BinanceWsSource;
import net.muon.data.bitfinex.BitfinexWsSource;
import net.muon.data.bitflyer.BitflyerWsSource;
import net.muon.data.bitstamp.BitstampWsSource;
import net.muon.data.coinbase.CoinbaseWsSource;
import net.muon.data.ftx.FtxWsSource;
import net.muon.data.gateio.GateioWsSource;
import net.muon.data.gemini.GeminiWsSource;
import net.muon.data.hitbtc.HitbtcWsSource;
import net.muon.data.huobi.HuobiWsSource;
import net.muon.data.kraken.KrakenWsSource;
import net.muon.data.kucoin.KucoinWsSource;
import org.apache.ignite.Ignite;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class CryptoWsConfiguration
{
    @Bean
    @ConditionalOnProperty(name = "crypto.ws.binance.enabled", havingValue = "true")
    public BinanceWsSource binanceWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var binanceWsProperties = properties.getBinance();
        return new BinanceWsSource(Exchange.BINANCE.getId(), binanceWsProperties.getTokenPairs(),
                binanceWsProperties.getSecret(), binanceWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.kucoin.enabled", havingValue = "true")
    public KucoinWsSource kucoinWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        return new KucoinWsSource(Exchange.KUCOIN.getId(), properties.getKucoin().getTokenPairs(),
                configBeans.executor, configBeans.ignite, configBeans.objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.gateio.enabled", havingValue = "true")
    public GateioWsSource gateioWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        return new GateioWsSource(Exchange.GATEIO.getId(), properties.getGateio().getTokenPairs(),
                configBeans.executor, configBeans.ignite, configBeans.objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.gemini.enabled", havingValue = "true")
    public GeminiWsSource geminiSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        return new GeminiWsSource(Exchange.GEMINI.getId(), properties.getGemini().getTokenPairs(),
                configBeans.executor, configBeans.ignite, configBeans.objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.kraken.enabled", havingValue = "true")
    public KrakenWsSource krakenSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var krakenWsProperties = properties.getKraken();
        return new KrakenWsSource(Exchange.KRAKEN.getId(), krakenWsProperties.getTokenPairs(),
                krakenWsProperties.getSecret(), krakenWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.bitfinex.enabled", havingValue = "true")
    public BitfinexWsSource bitfinexWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var bitfinexWsProperties = properties.getBitfinex();
        return new BitfinexWsSource(Exchange.BITFINEX.getId(), bitfinexWsProperties.getTokenPairs(),
                bitfinexWsProperties.getSecret(), bitfinexWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.bitflyer.enabled", havingValue = "true")
    public BitflyerWsSource bitflyerWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var bitflyerWsProperties = properties.getBitflyer();
        return new BitflyerWsSource(Exchange.BITFLYER.getId(), bitflyerWsProperties.getTokenPairs(),
                bitflyerWsProperties.getSecret(), bitflyerWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.bitstamp.enabled", havingValue = "true")
    public BitstampWsSource bitstampWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var bitstampWsProperties = properties.getBitstamp();
        return new BitstampWsSource(Exchange.BITSTAMP.getId(), bitstampWsProperties.getTokenPairs(),
                bitstampWsProperties.getSecret(), bitstampWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.ftx.enabled", havingValue = "true")
    public FtxWsSource ftxWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var ftxWsProperties = properties.getFtx();
        return new FtxWsSource(Exchange.FTX.getId(), ftxWsProperties.getTokenPairs(),
                ftxWsProperties.getSecret(), ftxWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.hitbtc.enabled", havingValue = "true")
    public HitbtcWsSource hitbtcWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var hitbtcWsProperties = properties.getHitbtc();
        return new HitbtcWsSource(Exchange.HITBTC.getId(), hitbtcWsProperties.getTokenPairs(),
                hitbtcWsProperties.getSecret(), hitbtcWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.huobi.enabled", havingValue = "true")
    public HuobiWsSource huobiWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var huobiWsProperties = properties.getHuobi();
        return new HuobiWsSource(Exchange.HUOBI.getId(), huobiWsProperties.getTokenPairs(),
                huobiWsProperties.getSecret(), huobiWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    @ConditionalOnProperty(name = "crypto.ws.coinbase.enabled", havingValue = "true")
    public CoinbaseWsSource coinbaseWsSource(WsConfigBeans configBeans, CryptoWsProperties properties)
    {
        var coinbaseWsProperties = properties.getCoinbase();
        return new CoinbaseWsSource(Exchange.COINBASE.getId(), coinbaseWsProperties.getTokenPairs(),
                coinbaseWsProperties.getSecret(), coinbaseWsProperties.getApiKey(),
                configBeans.executor, configBeans.ignite);
    }

    @Bean
    public WsConfigBeans wsConfigBeans(Ignite ignite, ExecutorService executor, ObjectMapper mapper)
    {
        return new WsConfigBeans(ignite, executor, mapper);
    }

    private static class WsConfigBeans
    {
        private final Ignite ignite;
        private final ExecutorService executor;
        private final ObjectMapper objectMapper;

        public WsConfigBeans(Ignite ignite, ExecutorService executor, ObjectMapper objectMapper)
        {
            this.ignite = ignite;
            this.executor = executor;
            this.objectMapper = objectMapper;
        }
    }
}