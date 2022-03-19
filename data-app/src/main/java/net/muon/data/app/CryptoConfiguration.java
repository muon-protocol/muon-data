package net.muon.data.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.binance.BinanceSource;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.gateio.GateioSource;
import net.muon.data.gemini.GeminiSource;
import net.muon.data.kraken.KrakenSource;
import net.muon.data.kucoin.KucoinSource;
import net.muon.data.sushiswap.SushiswapSource;
import net.muon.data.uniswap.UniswapSource;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class CryptoConfiguration
{
    @Bean
    @ConditionalOnProperty(prefix = "kucoin", name = "disabled", havingValue = "false", matchIfMissing = true)
    public KucoinSource kucoinSource(Ignite ignite, ObjectMapper objectMapper,
                                     @Value("${exchanges:}") Optional<List<String>> exchanges,
                                     @Value("${kucoin.symbols:}") Optional<List<String>> symbols,
                                     List<QuoteChangeListener> changeListeners)
    {
        return new KucoinSource(ignite, objectMapper, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), changeListeners);
    }

    @Bean
    @ConditionalOnProperty(prefix = "binance", name = "disabled", havingValue = "false", matchIfMissing = true)
    public BinanceSource binanceSource(Ignite ignite,
                                       @Value("${exchanges:}") Optional<List<String>> exchanges,
                                       @Value("${binance.symbols:}") Optional<List<String>> symbols,
                                       @Value("${binance.secret}") String secret,
                                       @Value("${binance.api-key}") String apiKey,
                                       List<QuoteChangeListener> changeListeners)
    {
        return new BinanceSource(ignite, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), secret, apiKey, changeListeners);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gateio", name = "disabled", havingValue = "false", matchIfMissing = true)
    public GateioSource gateioSource(Ignite ignite,
                                     ObjectMapper objectMapper,
                                     @Value("${exchanges:}") Optional<List<String>> exchanges,
                                     @Value("${gateio.symbols:}") Optional<List<String>> symbols,
                                     List<QuoteChangeListener> changeListeners)
    {
        return new GateioSource(ignite, objectMapper, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), changeListeners);
    }

    @Bean
    @ConditionalOnProperty(prefix = "kraken", name = "disabled", havingValue = "false", matchIfMissing = true)
    public KrakenSource krakenSource(Ignite ignite,
                                     @Value("${exchanges:}") Optional<List<String>> exchanges,
                                     @Value("${kraken.symbols:}") Optional<List<String>> symbols,
                                     @Value("${kraken.secret}") String secret,
                                     @Value("${kraken.api-key}") String apiKey,
                                     List<QuoteChangeListener> changeListeners)
    {
        return new KrakenSource(ignite, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), secret, apiKey, changeListeners);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gemini", name = "disabled", havingValue = "false", matchIfMissing = true)
    public GeminiSource geminiSource(Ignite ignite,
                                     ObjectMapper objectMapper,
                                     @Value("${exchanges:}") Optional<List<String>> exchanges,
                                     @Value("${gemini.symbols:}") Optional<List<String>> symbols,
                                     List<QuoteChangeListener> changeListeners)
    {
        return new GeminiSource(ignite, objectMapper, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), changeListeners);
    }

    @Bean
    @ConditionalOnProperty(prefix = "uniswap", name = "disabled", havingValue = "false", matchIfMissing = true)
    public UniswapSource uniswapSource(Ignite ignite, ObjectMapper objectMapper,
                                       @Value("${uniswap.subgraph-endpoint:}") String endpoint,
                                       @Value("${exchanges:}") Optional<List<String>> exchanges,
                                       @Value("${uniswap.symbols:}") Optional<List<String>> symbols,
                                       @Value("#{${uniswap.tokens}}") Map<String, String> tokens,
                                       List<QuoteChangeListener> changeListeners)
    {
        return new UniswapSource(ignite, objectMapper, endpoint, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), tokens, changeListeners);
    }

    @Bean
    @ConditionalOnProperty(prefix = "sushiswap", name = "disabled", havingValue = "false", matchIfMissing = true)
    public SushiswapSource sushiswapSource(Ignite ignite, ObjectMapper objectMapper,
                                           @Value("${sushiswap.subgraph-endpoint:}") String endpoint,
                                           @Value("${exchanges:}") Optional<List<String>> exchanges,
                                           @Value("${sushiswap.symbols:}") Optional<List<String>> symbols,
                                           @Value("#{${sushiswap.tokens}}") Map<String, String> tokens,
                                           List<QuoteChangeListener> changeListeners)
    {
        return new SushiswapSource(ignite, objectMapper, endpoint, exchanges.orElse(Collections.emptyList()),
                symbols.orElse(Collections.emptyList()), tokens, changeListeners);
    }
}