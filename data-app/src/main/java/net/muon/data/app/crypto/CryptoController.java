package net.muon.data.app.crypto;

import io.swagger.v3.oas.annotations.Parameter;
import net.muon.data.core.TokenPair;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController
{
    private final CryptoTokenService cryptoTokenService;

    public CryptoController(CryptoTokenService cryptoTokenService)
    {
        this.cryptoTokenService = cryptoTokenService;
    }

    @GetMapping("/pair/{pair}")
    public TokenPairPriceResponse getCryptoQuote(@PathVariable("pair")
                                                 @Parameter(description = "This can be a crypto pair, eg."
                                                         + "BTC-USD. Pair tokens must be separated by a dash") String pair,
                                                 @RequestParam(value = "exchanges", required = false) Exchange... exchanges)
    {
        return cryptoTokenService.getPrice(TokenPair.parse(pair), exchanges);
    }
}
