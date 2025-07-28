package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {     // Configurazione MVC per gestire le risorse statiche e il caricamento dei file

    @Value("${upload.base-dir}")        // Inietta una proprietà dal file application.properties
    private String uploadBaseDir;

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {      // Abilita il caricamento di file tramite multipart/form-data
        return new StandardServletMultipartResolver();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {    // Abilita il supporto per i metodi HTTP nascosti (PUT, DELETE) nei form HTML
        return new HiddenHttpMethodFilter();
    }

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {    // Configura Spring per servire file statici dal filesystem
        // se uploadBaseDir=uploads, questo risolve "file:uploads/"
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadBaseDir + "/");
    }
}
