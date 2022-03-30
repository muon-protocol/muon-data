package net.muon.data.core;

public interface TokenPriceSource
{
    String getId();

    TokenPairPrice getTokenPairPrice(TokenPair pair);
}
