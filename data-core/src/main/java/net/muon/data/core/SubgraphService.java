package net.muon.data.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class SubgraphService
{
    private final String endpoint;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    public SubgraphService(String endpoint, HttpClient httpClient, ObjectMapper objectMapper)
    {
        this.endpoint = endpoint;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String fetchQueryResponse(String query) throws URISyntaxException, IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(prepareQuery(query)))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private String prepareQuery(String query) throws JsonProcessingException
    {
        var q = new HashMap<String, String>();
        q.put("query", query);
        return objectMapper.writeValueAsString(q);
    }
}
