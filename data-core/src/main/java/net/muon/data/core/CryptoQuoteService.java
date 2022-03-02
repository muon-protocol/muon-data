package net.muon.data.core;

import com.google.gdata.util.common.base.Preconditions;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//@Service
//@Validated
public class CryptoQuoteService extends QuoteService<CryptoQuote, CryptoSource>
{

    private final Integer ignorePriceIfOlderThanMillis;

    public CryptoQuoteService(/*@Value("${crypto.quote.skip-prices-milisec-age:}")*/ Integer ignorePriceIfOlderThanMillis,
                                                                                     Collection<CryptoSource> sources)
    {
        super(sources, LoggerFactory.getLogger(CryptoQuoteService.class));
        this.ignorePriceIfOlderThanMillis = ignorePriceIfOlderThanMillis;
    }

    @Override
    public CryptoQuote getQuote(/*@NotNull */String symbol, String... exchanges)
    {
        List<Quote> quotes = new ArrayList<>();
        Collection<CryptoSource> enabledSources = getSources(false, exchanges);
        enabledSources.stream()
                .map(source -> source.getQuote(symbol))
                .filter(Objects::nonNull)
                .forEachOrdered(quotes::add);
        if (quotes.isEmpty()) {
            return null; // FIXME
//            LOGGER.warn("Symbol {} not found", symbol);
//            Optional<CryptoSource> proxy = enabledSources.stream()
//                    .filter(s -> s instanceof BinanceSource || s instanceof KucoinSource)
//                    .findFirst();
//            if (proxy.isEmpty()) {
//                return null;
//            }
//            LOGGER.warn("Loading {} with rest api", symbol);
//            return proxy.get().load(symbol);
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
            if (ignorePriceIfOlderThanMillis != null && (Instant.now().toEpochMilli() - q.getTime()) < ignorePriceIfOlderThanMillis) {
                sum = sum.add(q.getPrice());
            }
        }
        avg.setPrice(sum.divide(BigDecimal.valueOf(quotes.size()), 5, RoundingMode.HALF_UP));
        return avg;
    }

}
