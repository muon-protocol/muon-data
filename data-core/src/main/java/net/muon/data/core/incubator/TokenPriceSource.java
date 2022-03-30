package net.muon.data.core.incubator;

public interface TokenPriceSource
{
    TokenPairPrice getTokenPairPrice(TokenPair pair);
}
