package net.muon.data.app.crypto.dex;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

abstract class DexProperties
{
    private boolean enabled = false;

    @NotBlank
    private String subgraphEndpoint;

    @NotEmpty
    private Map<String, String> tokenAddresses;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getSubgraphEndpoint()
    {
        return subgraphEndpoint;
    }

    public void setSubgraphEndpoint(String subgraphEndpoint)
    {
        this.subgraphEndpoint = subgraphEndpoint;
    }

    public Map<String, String> getTokenAddresses()
    {
        return tokenAddresses;
    }

    public void setTokenAddresses(Map<String, String> tokenAddresses)
    {
        this.tokenAddresses = tokenAddresses;
    }
}
