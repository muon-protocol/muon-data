package net.muon.data.core;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class QuoteService<Q extends Quote, S extends Source<Q>>
{

    protected final Logger LOGGER;
    private final Collection<S> sources;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    protected QuoteService(Collection<S> sources, Logger logger)
    {
        this.sources = sources;
        this.LOGGER = logger;
    }

    public abstract Quote getQuote(/*@NotNull*/ String symbol, String... exchanges);

    @PostConstruct
    public void init()
    {
        Collection<S> enabledSources = getSources(false);
        LOGGER.info("All available sources are {}", sources);
        LOGGER.info("Enabled sources are {}", enabledSources);
        enabledSources.forEach(source -> {
            executor.submit(() -> {
                try {
                    LOGGER.info("{} trying to connect", source.getId());
                    source.connect();
                } catch (RuntimeException e) {
                    source.forceDisable();
                    LOGGER.warn("Exception suppressed when {} was connecting", source.getId(), e);
                }
            });
        });
    }

    public List<Q> getAll(String... exchanges)
    {
        return getSources(false, exchanges)
                .stream()
                .flatMap(source -> source.getAll().stream()).toList();
    }

    public Collection<S> getSources(boolean all, String... exchanges)
    {
        List<String> xchanges = exchanges == null ? Collections.EMPTY_LIST : Arrays.asList(exchanges);
        if (all) {
            return Collections.unmodifiableCollection(sources);
        }
        return Collections.unmodifiableCollection(sources
                .stream()
                .filter(source -> xchanges.isEmpty() || xchanges.contains(source.getId()))
                .filter(Source::isEnabled).toList());
    }
}
