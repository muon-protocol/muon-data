package net.muon.data.app.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.binance.BinanceHttpSource;
import net.muon.data.binance.BinanceWsSource;
import net.muon.data.core.SubgraphClient;
import net.muon.data.core.TokenPair;
import net.muon.data.gateio.GateioHttpSource;
import net.muon.data.gateio.GateioWsSource;
import net.muon.data.gemini.GeminiWsSource;
import net.muon.data.kraken.KrakenWsSource;
import net.muon.data.kucoin.KucoinWsSource;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
public class CryptoConfiguration
{
    @Bean
    @ConditionalOnProperty(prefix = "kucoin", name = "disabled", havingValue = "false", matchIfMissing = true)
    public KucoinWsSource kucoinWsSource(Ignite ignite, ObjectMapper objectMapper, Executor executor,
                                         @Value("${kucoin.symbols:}") List<TokenPair> subscriptionPairs)
    {
        return new KucoinWsSource(ignite, subscriptionPairs, objectMapper, executor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "binance", name = "disabled", havingValue = "false", matchIfMissing = true)
    public BinanceWsSource binanceWsSource(Ignite ignite, Executor executor,
                                           @Value("${binance.symbols:}") List<TokenPair> subscriptionPairs,
                                           @Value("${binance.secret}") String secret,
                                           @Value("${binance.api-key}") String apiKey)
    {
        return new BinanceWsSource(ignite, subscriptionPairs, executor, secret, apiKey);
    }

    @Bean
    @ConditionalOnProperty(prefix = "binance", name = "disabled", havingValue = "false", matchIfMissing = true)
    public BinanceHttpSource binanceHttpSource()
    {
        return new BinanceHttpSource();
    }

    @Bean
    @ConditionalOnProperty(prefix = "gateio", name = "disabled", havingValue = "false", matchIfMissing = true)
    public GateioWsSource gateioWsSource(Ignite ignite, ObjectMapper objectMapper, Executor executor,
                                         @Value("${gateio.symbols:}") List<TokenPair> subscriptionPairs)
    {
        return new GateioWsSource(ignite, subscriptionPairs, objectMapper, executor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gateio", name = "disabled", havingValue = "false", matchIfMissing = true)
    public GateioHttpSource gateioHttpSource(HttpClient httpClient, ObjectMapper objectMapper)
    {
        return new GateioHttpSource(httpClient, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "kraken", name = "disabled", havingValue = "false", matchIfMissing = true)
    public KrakenWsSource krakenSource(Ignite ignite, Executor executor,
                                       @Value("${kraken.symbols:}") List<TokenPair> subscriptionPairs,
                                       @Value("${kraken.secret}") String secret,
                                       @Value("${kraken.api-key}") String apiKey)
    {
        return new KrakenWsSource(ignite, subscriptionPairs, executor, secret, apiKey);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gemini", name = "disabled", havingValue = "false", matchIfMissing = true)
    public GeminiWsSource geminiSource(Ignite ignite, ObjectMapper objectMapper, Executor executor,
                                       @Value("${gemini.symbols:}") List<TokenPair> subscriptionPairs)
    {
        return new GeminiWsSource(ignite, subscriptionPairs, objectMapper, executor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "uniswap", name = "disabled", havingValue = "false", matchIfMissing = true)
    public UniswapSource uniswapSource(SubgraphClient subgraphClient, UniswapSource.UniswapProperties properties)
    {
        return new UniswapSource(subgraphClient, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "sushiswap", name = "disabled", havingValue = "false", matchIfMissing = true)
    public SushiswapSource sushiswapSource(SubgraphClient subgraphClient, SushiswapSource.SushiswapProperties properties)
    {
        return new SushiswapSource(subgraphClient, properties);
    }

    abstract static class DexProperties
    {
        private HashMap<String, String> tokens;

        public HashMap<String, String> getTokens()
        {
            return tokens;
        }

        public void setTokens(HashMap<String, String> tokens)
        {
            this.tokens = tokens;
        }
    }
}