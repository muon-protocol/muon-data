package net.muon.data.app.nft;

import net.muon.data.nft.subgraph.NftService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{collection-id}/{nft-id}")
    public Map<String, BigDecimal> getPrice(@PathVariable("collection-id") String collectionId, @PathVariable("nft-id") BigInteger nftId)
    {
        return nftService.getPrice(collectionId, nftId);
    }

//    @Scheduled(initialDelay = 2000, fixedDelay = 15000)
//    public void scanSales()
//    {
//        nftService.scanSales();
//    }
}
