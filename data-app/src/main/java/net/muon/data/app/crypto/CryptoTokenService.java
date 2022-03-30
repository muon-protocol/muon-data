package net.muon.data.app.crypto;

import net.muon.data.binance.BinanceHttpSource;
import net.muon.data.binance.BinanceWsSource;
import net.muon.data.core.incubator.TokenPair;
import net.muon.data.core.incubator.TokenPriceSource;
import net.muon.data.gateio.GateioHttpSource;
import net.muon.data.gateio.GateioWsSource;
import net.muon.data.gemini.GeminiWsSource;
import net.muon.data.kraken.KrakenWsSource;
import net.muon.data.kucoin.KucoinWsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class CryptoTokenService
{
    private static final MathContext PRECISION = new MathContext(5);

    private final Map<Exchange, TokenPriceSource> websocketSources = new HashMap<>();
    private final Map<Exchange, TokenPriceSource> httpSources = new HashMap<>();
    private final Integer ignorePriceIfOlderThanMillis;

    public CryptoTokenService(@Nullable KucoinWsSource kucoinWsSource,
                              @Nullable BinanceWsSource binanceWsSource,
                              @Nullable BinanceHttpSource binanceHttpSource,
                              @Nullable GateioWsSource gateioWsSource,
                              @Nullable GateioHttpSource gateioHttpSource,
                              @Nullable GeminiWsSource geminiWsSource,
                              @Nullable KrakenWsSource krakenWsSource,
                              @Nullable UniswapSource uniswapSource,
                              @Nullable SushiswapSource sushiswapSource,
                              @Value("${crypto.quote.skip-prices-milisec-age:}") Integer ignorePriceIfOlderThanMillis)
    {
        this.ignorePriceIfOlderThanMillis = ignorePriceIfOlderThanMillis;
        if (binanceWsSource != null)
            websocketSources.put(Exchange.BINANCE, binanceWsSource);
        if (kucoinWsSource != null)
            websocketSources.put(Exchange.KUCOIN, kucoinWsSource);
        if (gateioWsSource != null)
            websocketSources.put(Exchange.GATEIO, gateioWsSource);
        if (geminiWsSource != null)
            websocketSources.put(Exchange.GEMINI, geminiWsSource);
        if (krakenWsSource != null)
            websocketSources.put(Exchange.GEMINI, krakenWsSource);

        if (binanceHttpSource != null)
            httpSources.put(Exchange.BINANCE, binanceHttpSource);
        if (gateioHttpSource != null)
            httpSources.put(Exchange.GATEIO, gateioHttpSource);
        if (uniswapSource != null)
            httpSources.put(Exchange.UNISWAP_V2, uniswapSource);
        if (sushiswapSource != null)
            httpSources.put(Exchange.SUSHISWAP, sushiswapSource);
    }

    public TokenPairPriceResponse getPrice(TokenPair pair, Exchange... exchanges)
    {
        List<ExchangePrice> prices = new ArrayList<>();

        if (exchanges.length == 0) {
            // If no exchanges are provided, first try getting prices from web socket sources.
            // If no prices were found, Try a random (first) http source.
            websocketSources.forEach((exchange, source) ->
                    prices.add(new ExchangePrice(exchange, source.getTokenPairPrice(pair))));
            if (prices.isEmpty() && !httpSources.isEmpty()) {
                Map.Entry<Exchange, TokenPriceSource> sourceEntry = httpSources.entrySet().iterator().next();
                prices.add(new ExchangePrice(sourceEntry.getKey(), sourceEntry.getValue().getTokenPairPrice(pair)));
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
                prices.add(new ExchangePrice(exchange, source.getTokenPairPrice(pair)));
            }

        return createResponse(pair, prices);
    }

    private TokenPairPriceResponse createResponse(TokenPair pair, List<ExchangePrice> prices)
    {
        var response = new TokenPairPriceResponse();
        response.setPair(pair);
        response.setPrices(prices);

        if (prices.isEmpty())
            return response;

        ExchangePrice p0 = prices.get(0);
        BigDecimal sum = p0.getPrice();
        Instant now = Instant.now();

        for (int i = 1; i < prices.size(); i++) {
            ExchangePrice p = prices.get(i);
            if (ignorePriceIfOlderThanMillis == null || (now.toEpochMilli() - p.getTime()) < ignorePriceIfOlderThanMillis)
                sum = sum.add(p.getPrice());
        }

        response.setAveragePrice(sum.divide(BigDecimal.valueOf(prices.size()), PRECISION));
        return response;
    }

}
