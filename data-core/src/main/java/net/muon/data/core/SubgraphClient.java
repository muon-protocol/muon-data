package net.muon.data.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class SubgraphClient
{
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SubgraphClient(HttpClient httpClient, ObjectMapper objectMapper)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public <T> T send(URI endpoint, String query, Class<T> responseType)
    {
        try {
            var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(Map.of("query", query)));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(endpoint)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(requestBody)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // TODO Exception handling
            if (response.statusCode() != 200)
                throw new RuntimeException(String.format("Subgraph request failed with code %s. %s",
                        response.statusCode(), responseBody));

            Map<String, String> result = objectMapper.readValue(responseBody, new TypeReference<>() {});
            String data = result.get("data");
            if (data == null)
                throw new RuntimeException("Unexpected response");

            return objectMapper.readValue(data, responseType);

        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
