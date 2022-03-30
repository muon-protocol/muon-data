package net.muon.data.core;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

// FIXME This can be a record in java 16+
public final class TokenPair
{
    public static TokenPair parse(String pair) throws IllegalArgumentException
    {
        checkNotNull(pair);
        String[] split = pair.split("-");
        checkArgument(split.length == 2, "Invalid token pair: %s", pair);
        return new TokenPair(split[0], split[1]);
    }

    private final String token0;
    private final String token1;

    public TokenPair(String token0, String token1)
    {
        this.token0 = token0;
        this.token1 = token1;
    }

    public String token0()
    {
        return token0;
    }

    public String token1()
    {
        return token1;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPair tokenPair = (TokenPair) o;
        return token0.equals(tokenPair.token0) && token1.equals(tokenPair.token1);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(token0, token1);
    }

    @Override
    public String toString()
    {
        return String.format("%s-%s", token0, token1);
    }
}
