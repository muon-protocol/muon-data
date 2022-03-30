package net.muon.data.core;

public interface TokenPriceSource
{
    TokenPairPrice getTokenPairPrice(TokenPair pair);
}
