package net.muon.data.app;

import io.swagger.v3.oas.annotations.Parameter;
import net.muon.data.core.CryptoQuote;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/quote")
public class QuoteController
{
    private final CryptoQuoteService cryptoQuoteService;

    public QuoteController(CryptoQuoteService cryptoQuoteService)
    {
        this.cryptoQuoteService = cryptoQuoteService;
    }

    @GetMapping("/crypto")
    public List<CryptoQuote> getAllCrypto(@RequestParam(value = "exchanges", required = false) String... exchanges)
    {
        return cryptoQuoteService.getAll(exchanges);//FIXME space limitations if exchange is not set! bad api?
    }

    @GetMapping("/crypto/{pair}")
    public CryptoQuote getCryptoQuote(@PathVariable("pair")
                                      @Parameter(description = "This can be a crypto pair, eg."
                                              + "BTC-USD. Pair tokens must be separated by a dash") String pair,
                                      @RequestParam(value = "exchanges", required = false) String... exchanges)
    {
        CryptoQuote quote = cryptoQuoteService.getQuote(pair.toUpperCase(), exchanges);
        if (quote == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return quote;
    }
}
