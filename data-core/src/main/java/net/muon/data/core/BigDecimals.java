package net.muon.data.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public final class BigDecimals
{
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

    public static BigDecimal divideByScale(BigInteger dividend, int decimalScale)
    {
        return divide(dividend, BigDecimal.TEN.pow(decimalScale));
    }

    public static BigDecimal divideByScale(BigInteger dividend, BigInteger decimalScale)
    {
        return divideByScale(dividend, decimalScale.intValue());
    }
}
