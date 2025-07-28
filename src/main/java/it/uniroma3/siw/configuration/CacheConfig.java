// CacheConfig.java
package it.uniroma3.siw.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager("credenzialiByUsername", "credenzialiById"); // Due cache distinte per le credenziali, indicizzate per username e id
        cm.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)      // Numero massimo di elementi nella cache
            .expireAfterAccess(30, TimeUnit.MINUTES) // Tempo di scadenza dopo l'ultimo accesso
        );
        return cm;
    }
}
