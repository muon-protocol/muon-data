package net.muon.data.app.crypto.dex;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("sushiswap")
@Validated
public class SushiswapProperties extends DexProperties
{
}
