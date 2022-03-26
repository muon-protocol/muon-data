package net.muon.data.app;

import com.google.gdata.util.common.base.Preconditions;
import net.muon.data.binance.BinanceSource;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.Quote;
import net.muon.data.core.QuoteService;
import net.muon.data.kucoin.KucoinSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
@Validated
public class CryptoQuoteService extends QuoteService<CryptoQuote, CryptoSource>
{
    private final Integer ignorePriceIfOlderThanMillis;

    public CryptoQuoteService(@Value("${crypto.quote.skip-prices-milisec-age:}") Integer ignorePriceIfOlderThanMillis,
                              Collection<CryptoSource> sources)
    {
        super(sources, LoggerFactory.getLogger(CryptoQuoteService.class));
        this.ignorePriceIfOlderThanMillis = ignorePriceIfOlderThanMillis;
    }

    @Override
    public CryptoQuote getQuote(String symbol, String... exchanges)
    {
        List<Quote> quotes = new ArrayList<>();
        Collection<CryptoSource> enabledSources = getSources(false, exchanges);
        enabledSources.stream()
                .map(source -> source.getQuote(symbol))
                .filter(Objects::nonNull)
                .forEachOrdered(quotes::add);
        if (quotes.isEmpty()) {
            LOGGER.warn("Symbol {} not found", symbol);
            Optional<CryptoSource> proxy = enabledSources.stream()
                    .filter(s -> s instanceof BinanceSource || s instanceof KucoinSource)
                    .findFirst();
            if (proxy.isEmpty()) {
                return null;
            }
            LOGGER.warn("Loading {} with rest api", symbol);
            return proxy.get().load(symbol);
        }
        return avg(quotes);
    }

    private CryptoQuote avg(List<Quote> quotes)
    {
        Preconditions.checkArgument(!quotes.isEmpty());
        Quote p0 = quotes.get(0);
        CryptoQuote avg = new CryptoQuote();
        avg.setStatus(p0.getStatus());
        avg.setTime(p0.getTime());
        avg.setSymbol(p0.getSymbol());
        BigDecimal sum = quotes.get(0).getPrice();
        for (int i = 1; i < quotes.size(); i++) {
            Quote q = quotes.get(i);
            if (ignorePriceIfOlderThanMillis == null || (Instant.now().toEpochMilli() - q.getTime()) < ignorePriceIfOlderThanMillis) {
                sum = sum.add(q.getPrice());
            }
        }
        avg.setPrice(sum.divide(BigDecimal.valueOf(quotes.size()), 5, RoundingMode.HALF_UP));
        return avg;
    }

}
