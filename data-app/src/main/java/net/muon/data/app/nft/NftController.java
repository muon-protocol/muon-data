package net.muon.data.app.nft;

import net.muon.data.nft.AveragePrice;
import net.muon.data.nft.NftSale;
import net.muon.data.nft.OpenseaSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

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

    @GetMapping("/sales")
    public List<NftSale> getSales(@RequestParam(value = "collection-id", required = false) String collectionId,
                                  @RequestParam(value = "token-id", required = false) BigInteger tokenId,
                                  @RequestParam(value = "fromTimestamp", required = false) Long fromTimestamp,
                                  @RequestParam(value = "toTimestamp", required = false) Long toTimestamp,
                                  @RequestParam(value = "fromPrice", required = false) BigDecimal fromPrice,
                                  @RequestParam(value = "toPrice", required = false) BigDecimal toPrice,
                                  @RequestParam(value = "order", required = false) String order,
                                  @RequestParam(value = "desc", required = false) Boolean desc,
                                  @RequestParam(value = "limit", defaultValue = "10") @Max(100) Integer limit,
                                  @RequestParam(value = "offset", required = false) Integer offset)
    {
        validateTimestamps(fromTimestamp, toTimestamp);
        if (order != null && !Set.of("price", "time").contains(order))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order field");
        return getOpenseaSource().getSales(collectionId, tokenId, fromTimestamp, toTimestamp,
                fromPrice, toPrice, order, desc, limit, offset);
    }

    @GetMapping("/sales/last")
    public NftSale getLastSale(@RequestParam(value = "collection-id", required = false) String collectionId,
                               @RequestParam(value = "token-id", required = false) BigInteger tokenId,
                               @RequestParam(value = "fromTimestamp", required = false) Long fromTimestamp,
                               @RequestParam(value = "toTimestamp", required = false) Long toTimestamp,
                               @RequestParam(value = "fromPrice", required = false) BigDecimal fromPrice,
                               @RequestParam(value = "toPrice", required = false) BigDecimal toPrice)
    {
        validateTimestamps(fromTimestamp, toTimestamp);
        return getOpenseaSource().getLastSale(collectionId, tokenId, fromTimestamp, toTimestamp, fromPrice, toPrice);
    }

    @GetMapping("/floor-price")
    public NftSale getFloorPrice(@RequestParam(value = "collection-id", required = false) String collectionId,
                                 @RequestParam(value = "token-id", required = false) BigInteger tokenId,
                                 @RequestParam(value = "fromTimestamp", required = false) Long fromTimestamp,
                                 @RequestParam(value = "toTimestamp", required = false) Long toTimestamp)
    {
        validateTimestamps(fromTimestamp, toTimestamp);
        return getOpenseaSource().getFloorPrice(collectionId, tokenId, fromTimestamp, toTimestamp);
    }

    @GetMapping("/average-price")
    public AveragePrice getAveragePrice(@RequestParam(value = "collection-id", required = false) String collectionId,
                                        @RequestParam(value = "token-id", required = false) BigInteger tokenId,
                                        @RequestParam(value = "fromTimestamp") Long fromTimestamp,
                                        @RequestParam(value = "toTimestamp") Long toTimestamp,
                                        @RequestParam(value = "fromPrice", required = false) BigDecimal fromPrice,
                                        @RequestParam(value = "toPrice", required = false) BigDecimal toPrice)
    {
        validateTimestamps(fromTimestamp, toTimestamp);
        return getOpenseaSource().getAveragePrice(collectionId, tokenId, fromTimestamp, toTimestamp, fromPrice, toPrice);
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
