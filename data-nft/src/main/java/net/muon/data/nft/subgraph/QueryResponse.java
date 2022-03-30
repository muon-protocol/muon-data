package net.muon.data.nft.subgraph;

class QueryResponse<T>
{
    private T data;

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
