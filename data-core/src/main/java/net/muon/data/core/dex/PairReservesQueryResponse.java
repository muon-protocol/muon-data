package net.muon.data.core.dex;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class PairReservesQueryResponse
{
    private static final MathContext PRECISION = new MathContext(5);

    private PairsData data;

    public BigDecimal getToken0Price()
    {
        return data == null || data.pairs == null || data.pairs.isEmpty() ? null :
                data.pairs.get(0).getReserve1().divide(data.pairs.get(0).getReserve0(), PRECISION);
    }

    public BigDecimal getToken1Price()
    {
        return data == null || data.pairs == null || data.pairs.isEmpty() ? null :
                data.pairs.get(0).getReserve0().divide(data.pairs.get(0).getReserve1(), PRECISION);
    }

    public PairsData getData()
    {
        return data;
    }

    public void setData(PairsData data)
    {
        this.data = data;
    }

    public static class PairsData
    {
        private List<PairData> pairs;

        public List<PairData> getPairs()
        {
            return pairs;
        }

        public void setPairs(List<PairData> pairs)
        {
            this.pairs = pairs;
        }
    }

    public static class PairData
    {
        private BigDecimal reserve0;
        private BigDecimal reserve1;

        public BigDecimal getReserve0()
        {
            return reserve0;
        }

        public void setReserve0(BigDecimal reserve0)
        {
            this.reserve0 = reserve0;
        }

        public BigDecimal getReserve1()
        {
            return reserve1;
        }

        public void setReserve1(BigDecimal reserve1)
        {
            this.reserve1 = reserve1;
        }
    }
}
