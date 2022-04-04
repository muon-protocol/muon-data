package net.muon.data.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public final class BigDecimals
{
    public static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor)
    {
        return dividend.divide(divisor, 5, RoundingMode.FLOOR);
    }

    public static BigDecimal divide(BigInteger dividend, BigDecimal divisor)
    {
        return divide(new BigDecimal(dividend), divisor);
    }

    public static BigDecimal divide(BigDecimal dividend, int divisor)
    {
        return divide(dividend, BigDecimal.valueOf(divisor));
    }

    public static BigDecimal divide(BigInteger dividend, int divisor)
    {
        return divide(new BigDecimal(dividend), BigDecimal.valueOf(divisor));
    }
}
