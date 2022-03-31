package net.muon.data.app.crypto.dex;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("uniswap")
@Validated
public class UniswapV2Properties extends DexProperties
{
}
