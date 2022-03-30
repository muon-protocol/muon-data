package net.muon.data.app.nft;

import net.muon.data.nft.subgraph.NftService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/api/nft")
public class NftController
{
    private final NftService nftService;

    public NftController(NftService nftService)
    {
        this.nftService = nftService;
    }

    @GetMapping("/price/{collection-id}/{nft-id}")
    public Map<String, BigDecimal> getPrice(@PathVariable("collection-id") String collectionId, @PathVariable("nft-id") BigInteger nftId,
                                            @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to)
    {
        return nftService.getPrice(collectionId, nftId, from, to);
    }

    @GetMapping("/floor-price/{collection-id}")
    public BigDecimal getFloorPrice(@PathVariable("collection-id") String collectionId, @RequestParam(value = "nft-id", required = false) BigInteger nftId,
                                    @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to)
    {
        return nftService.getFloorPrice(collectionId, nftId, from, to);
    }

//    @Scheduled(initialDelay = 2000, fixedDelay = 15000)
//    public void scanSales()
//    {
//        nftService.scanSales();
//    }
}
