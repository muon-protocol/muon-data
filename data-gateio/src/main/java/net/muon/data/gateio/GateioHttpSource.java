package net.muon.data.gateio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.AbstractHttpSource;
import net.muon.data.core.TokenPair;
import net.muon.data.core.TokenPairPrice;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class GateioHttpSource extends AbstractHttpSource
{
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GateioHttpSource(String id, HttpClient httpClient, ObjectMapper objectMapper)
    {
        super(id);
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    protected TokenPairPrice load(TokenPair pair)
    {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.gateio.ws/api/v4/spot/trades?currency_pair=" +
                            String.format("%s_%s", pair.token0(), pair.token1()) + "&limit=1"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            List list = objectMapper.readValue(response.body(), new TypeReference<>() {});
            if (list == null || list.isEmpty())
                return null;
            Map<String, Object> result = (Map<String, Object>) list.get(0);
            BigDecimal price = (BigDecimal) result.get("price");
            long time = (long) result.get("create_time_ms");
            return new TokenPairPrice(pair, price, Instant.ofEpochMilli(time));
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
