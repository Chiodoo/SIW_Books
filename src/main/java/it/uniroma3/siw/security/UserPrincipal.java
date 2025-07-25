package it.uniroma3.siw.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import it.uniroma3.siw.model.Credentials;

/**
 * UserPrincipal unifies Spring Security UserDetails and OIDC principal,
 * storing the application Credentials + User domain object.
 */
public class UserPrincipal implements UserDetails, OidcUser {

    private final Credentials credentials;
    private final Map<String,Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    /**
     * Costruttore per form-login (solo UserDetails).
     */
    public UserPrincipal(Credentials credentials) {
        this(credentials, Collections.emptyMap(), null, null);
    }

    /**
     * Costruttore per OAuth2 puro (implicitamente supportato come OidcUser -> OAuth2User).
     */
    public UserPrincipal(Credentials credentials, Map<String,Object> attributes) {
        this(credentials, attributes, null, null);
    }

    /**
     * Costruttore per OIDC (UserDetails + OidcUser).
     */
    public UserPrincipal(Credentials credentials,
                         OidcIdToken idToken,
                         OidcUserInfo userInfo) {
        this(credentials, Collections.emptyMap(), idToken, userInfo);
    }

    // Costruttore interno comune
    private UserPrincipal(Credentials credentials,
                          Map<String,Object> attributes,
                          OidcIdToken idToken,
                          OidcUserInfo userInfo) {
        this.credentials = credentials;
        this.attributes  = attributes;
        this.idToken     = idToken;
        this.userInfo    = userInfo;
    }

    // --- UserDetails methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority(credentials.getRole())
        );
    }

    @Override public String getPassword() { return credentials.getPassword(); }
    @Override public String getUsername() { return credentials.getUsername(); }
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return true; }

    // --- OidcUser (extends OAuth2User) methods ---
    @Override
    public Map<String,Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        if (idToken != null && idToken.getSubject() != null) {
            return idToken.getSubject();
        }
        Object sub = attributes.get("sub");
        return (sub != null? sub.toString(): credentials.getUsername());
    }

    @Override public OidcIdToken getIdToken()   { return idToken; }
    @Override public OidcUserInfo getUserInfo(){ return userInfo; }
    @Override public Map<String,Object> getClaims() { return attributes; }

    // --- Accesso al dominio ---
    public Credentials getCredentialsDomain() {
        return credentials;
    }

    public it.uniroma3.siw.model.User getUserDomain() {
        return credentials.getUser();
    }
}
