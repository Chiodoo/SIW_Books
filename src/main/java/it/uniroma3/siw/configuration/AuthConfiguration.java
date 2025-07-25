package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import it.uniroma3.siw.security.CustomOAuth2UserService;
import it.uniroma3.siw.security.CustomOidcUserService;
import it.uniroma3.siw.security.CustomUserDetailsService;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;


@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;


@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder());
}
    
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity, CustomOAuth2UserService customOAuth2UserService, CustomOidcUserService customOidcUserService) throws Exception {
        httpSecurity
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(requests -> requests
                // Pagine pubbliche e risorse statiche
                .requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login", "/css/**", "/images/**", "favicon.ico", "/js/**", "/webjars/**", "/books", "/book/**", "/uploads/**").permitAll()
                // Registrazione e login aperti a tutti (POST)
                .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                // Area amministrativa solo per admin
                .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)
                // Tutte le altre richieste devono essere autenticate
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/success", true)
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
          .loginPage("/login")
          .userInfoEndpoint(endpoints -> endpoints
              // per OAUTH2 puro (GitHub, Facebook…)
              .userService(customOAuth2UserService)
              // per OIDC (Google, Azure AD…)
              .oidcUserService(customOidcUserService))
          .defaultSuccessUrl("/success", true)
        );

        return httpSecurity.build();
    }

}