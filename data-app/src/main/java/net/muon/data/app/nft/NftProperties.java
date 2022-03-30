package net.muon.data.app.nft;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("nft")
@Validated
public class NftProperties
{
    private OpenseaProperties opensea;

    public OpenseaProperties getOpensea()
    {
        return opensea;
    }

    public void setOpensea(OpenseaProperties opensea)
    {
        this.opensea = opensea;
    }

    public static class OpenseaProperties
    {
        private boolean enabled = false;

        @NotBlank
        private String subgraphEndpoint;

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
    }
}
