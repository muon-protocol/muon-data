package net.muon.data.app.crypto.configuration;

import net.muon.data.core.TokenPair;

import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractWsProperties
{
    private boolean enabled = false;
    private List<String> pairs;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public List<String> getPairs()
    {
        return pairs;
    }

    public void setPairs(List<String> pairs)
    {
        this.pairs = pairs;
    }

    public List<TokenPair> getTokenPairs()
    {
        return pairs.stream().map(TokenPair::parse).collect(Collectors.toList());
    }
}
