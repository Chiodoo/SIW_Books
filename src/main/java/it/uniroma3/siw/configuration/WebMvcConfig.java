package it.uniroma3.siw.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.lang.NonNull;


/**
 * WebMvcConfig è una classe di configurazione che implementa {@link WebMvcConfigurer}
 * per personalizzare la configurazione di Spring MVC per l'applicazione.
 * <p>
 * Questa classe configura specificamente gli handler delle risorse statiche.
 * Mappa tutte le richieste con il pattern "/uploads/**" alla directory locale "uploads/".
 * Questo permette di accedere tramite richieste HTTP ai file presenti nella cartella "uploads".
 * </p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
    }
}
