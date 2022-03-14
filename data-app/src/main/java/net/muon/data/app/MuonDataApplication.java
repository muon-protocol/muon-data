package net.muon.data.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MuonDataApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MuonDataApplication.class, args);
    }
}
