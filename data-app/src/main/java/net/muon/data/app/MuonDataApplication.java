package net.muon.data.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import net.muon.data.core.SubgraphClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.http.HttpClient;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@OpenAPIDefinition(servers = {
        @Server(url = "/", description = "Default Server URL")
})
@SpringBootApplication
@EnableScheduling
public class MuonDataApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MuonDataApplication.class, args);
    }

    @Bean
    public HttpClient subgraphClient()
    {
        return HttpClient.newHttpClient();
    }

    @Bean
    public SubgraphClient subgraphClient(HttpClient httpClient, ObjectMapper objectMapper)
    {
        return new SubgraphClient(httpClient, objectMapper);
    }

    @Bean
    public Executor executor()
    {
        return Executors.newFixedThreadPool(8);
    }
}
