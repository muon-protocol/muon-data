package net.muon.data.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import net.muon.data.core.SubgraphClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@OpenAPIDefinition(servers = {
        @Server(url = "/", description = "Default Server URL")
})
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class MuonDataApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MuonDataApplication.class, args);
    }

    @Bean
    public HttpClient httpClient()
    {
        return HttpClient.newHttpClient();
    }

    @Bean
    public SubgraphClient subgraphClient(HttpClient httpClient, ObjectMapper objectMapper)
    {
        return new SubgraphClient(httpClient, objectMapper);
    }

    @Bean
    public ExecutorService executor()
    {
        return Executors.newFixedThreadPool(10);
    }
}
