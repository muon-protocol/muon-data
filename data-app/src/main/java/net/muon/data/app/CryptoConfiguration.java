package net.muon.data.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.binance.BinanceSource;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.gateio.GateioSource;
import net.muon.data.kucoin.KucoinSource;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
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
}
