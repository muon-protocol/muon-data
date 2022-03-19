package net.muon.data.core.dex;

import java.util.List;

public class TokenAddressQueryResponse
{
    private TokensData data;

    public String getAddress()
    {
        return data == null || data.tokens == null || data.tokens.isEmpty() ? null : data.tokens.get(0).getId();
    }

    public TokensData getData()
    {
        return data;
    }

    public void setData(TokensData data)
    {
        this.data = data;
    }

    public static class TokensData
    {
        private List<TokenData> tokens;

        public List<TokenData> getTokens()
        {
            return tokens;
        }

        public void setTokens(List<TokenData> tokens)
        {
            this.tokens = tokens;
        }
    }

    public static class TokenData
    {
        private String id;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }
    }
}
