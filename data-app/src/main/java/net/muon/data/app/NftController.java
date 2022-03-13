package net.muon.data.app;

import net.muon.data.nft.NftService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;

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
    public BigDecimal getPrice(@PathVariable("collection-id") String collectionId, @PathVariable("nft-id") BigInteger nftId) // FIXME ?
    {
        return nftService.getPrice(collectionId, nftId);
    }
}
