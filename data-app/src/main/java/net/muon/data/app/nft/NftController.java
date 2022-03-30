package net.muon.data.app.nft;

import net.muon.data.nft.NftFloorPrice;
import net.muon.data.nft.NftPrice;
import net.muon.data.nft.OpenseaSource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/nft")
public class NftController
{
    private final OpenseaSource openseaSource;

    public NftController(@Nullable OpenseaSource openseaSource)
    {
        this.openseaSource = openseaSource;
    }

    @GetMapping("/price/{collection-id}/{nft-id}")
    public NftPrice getPrice(@PathVariable("collection-id") String collectionId,
                             @PathVariable("nft-id") BigInteger nftId,
                             @RequestParam(value = "from", required = false) Long from,
                             @RequestParam(value = "to", required = false) Long to)
    {
        return getOpenseaSource().getPrice(collectionId, nftId, from, to);
    }

    @GetMapping("/floor-price/{collection-id}")
    public NftFloorPrice getFloorPrice(@PathVariable("collection-id") String collectionId,
                                       @RequestParam(value = "nft-id", required = false) BigInteger nftId,
                                       @RequestParam(value = "from", required = false) Long from,
                                       @RequestParam(value = "to", required = false) Long to)
    {
        return getOpenseaSource().getFloorPrice(collectionId, nftId, from, to);
    }

    private OpenseaSource getOpenseaSource()
    {
        if (openseaSource == null)
            throw new RuntimeException();
        return openseaSource;
    }
}
