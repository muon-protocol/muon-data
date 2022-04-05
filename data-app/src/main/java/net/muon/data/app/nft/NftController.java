package net.muon.data.app.nft;

import net.muon.data.nft.NftFloorPrice;
import net.muon.data.nft.NftPrice;
import net.muon.data.nft.OpenseaSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/nft")
@Validated
public class NftController
{
    private final OpenseaSource openseaSource;

    public NftController(@Nullable OpenseaSource openseaSource)
    {
        this.openseaSource = openseaSource;
    }

    @GetMapping("/price/{collection-id}/{token-id}")
    public NftPrice getPrice(@PathVariable("collection-id") @NotBlank String collectionId,
                             @PathVariable("token-id") BigInteger tokenId,
                             @RequestParam(value = "from", required = false) Long from,
                             @RequestParam(value = "to", required = false) Long to)
    {
        validateTimestamps(from, to);
        return getOpenseaSource().getPrice(collectionId, tokenId, from, to);
    }

    @GetMapping("/floor-price/{collection-id}")
    public NftFloorPrice getFloorPrice(@PathVariable("collection-id") @NotBlank String collectionId,
                                       @RequestParam(value = "nft-id", required = false) BigInteger nftId,
                                       @RequestParam(value = "from", required = false) Long from,
                                       @RequestParam(value = "to", required = false) Long to)
    {
        validateTimestamps(from, to);
        return getOpenseaSource().getFloorPrice(collectionId, nftId, from, to);
    }

    private void validateTimestamps(Long from, Long to)
    {
        if (from != null && to != null && from >= to)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private OpenseaSource getOpenseaSource()
    {
        if (openseaSource == null)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        return openseaSource;
    }
}
