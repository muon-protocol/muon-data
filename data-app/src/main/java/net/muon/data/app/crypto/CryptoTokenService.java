package net.muon.data.app.crypto;

import net.muon.data.app.crypto.configuration.CryptoWsProperties;
import net.muon.data.core.*;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class CryptoTokenService
{
    private static final MathContext PRECISION = new MathContext(5);

    private final Map<Exchange, AbstractWsSource> websocketSources = new HashMap<>();
    private final Map<Exchange, AbstractHttpSource> httpSources = new HashMap<>();
    private final Integer ignorePriceIfOlderThanMillis;
    private final ExecutorService executor;

    public CryptoTokenService(List<AbstractHttpSource> httpSources, List<AbstractWsSource> wsSources,
                              ExecutorService executor, CryptoWsProperties cryptoWsProperties)
    {
        this.executor = executor;
        this.ignorePriceIfOlderThanMillis = cryptoWsProperties.getSkipPricesAfterMillis();
        Map<String, Exchange> exchangeMap = Arrays.stream(Exchange.values())
                .collect(Collectors.toMap(Exchange::getId, exchange -> exchange));
        wsSources.forEach(source -> {
            Exchange exchange = exchangeMap.get(source.getId());
            if (exchange != null)
                this.websocketSources.put(exchange, source);
        });
        httpSources.forEach(source -> {
            Exchange exchange = exchangeMap.get(source.getId());
            if (exchange != null)
                this.httpSources.put(exchange, source);
        });
    }

    @Async
    @EventListener(ApplicationStartedEvent.class)
    public void startWsSources()
    {
        websocketSources.values().forEach(source -> {
            executor.submit(() -> {
                try {
                    source.connect();
                } catch (RuntimeException ex) {
                    source.disconnect();
                }
            });
        });
    }

    public TokenPairPriceResponse getPrice(TokenPair pair, Exchange... exchanges)
    {
        List<ExchangePrice> prices = new ArrayList<>();

        if (exchanges == null || exchanges.length == 0) {
            // If no exchanges are provided, first try getting prices from web socket sources.
            // If no prices were found, Try a random (first) http source.
            websocketSources.forEach((exchange, source) -> addPrice(prices, exchange, source.getTokenPairPrice(pair)));
            if (prices.isEmpty() && !httpSources.isEmpty()) {
                Map.Entry<Exchange, AbstractHttpSource> sourceEntry = httpSources.entrySet().iterator().next();
                addPrice(prices, sourceEntry.getKey(), sourceEntry.getValue().getTokenPairPrice(pair));
            }
        } else
            for (Exchange exchange : exchanges) {
                TokenPriceSource source;
                if (websocketSources.containsKey(exchange))
                    source = websocketSources.get(exchange);
                else if (httpSources.containsKey(exchange))
                    source = httpSources.get(exchange);
                else
                    continue;
                addPrice(prices, exchange, source.getTokenPairPrice(pair));
            }

        return createResponse(pair, prices);
    }

    private void addPrice(List<ExchangePrice> prices, Exchange exchange, TokenPairPrice pairPrice)
    {
        if (pairPrice == null)
            return;
        prices.add(new ExchangePrice(exchange, pairPrice));
    }

    private TokenPairPriceResponse createResponse(TokenPair pair, List<ExchangePrice> prices)
    {
        var response = new TokenPairPriceResponse();
        response.setPair(pair);
        response.setPrices(prices);

        if (prices.isEmpty())
            return response;

        BigDecimal sum = BigDecimal.ZERO;
        Instant now = Instant.now();

        for (ExchangePrice price : prices)
            if (ignorePriceIfOlderThanMillis == null || (now.toEpochMilli() - price.getTime()) < ignorePriceIfOlderThanMillis)
                sum = sum.add(price.getPrice());

        response.setAveragePrice(sum.divide(BigDecimal.valueOf(prices.size()), PRECISION));
        return response;
    }

}
