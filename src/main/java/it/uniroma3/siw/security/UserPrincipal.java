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
import it.uniroma3.siw.model.User;

/**
 * UserPrincipal unifies Spring Security UserDetails and OIDC principal,
 * storing essential user data to avoid LazyInitialization.
 */
public class UserPrincipal implements UserDetails, OidcUser {

    private final Long userId;
    private final String displayName;
    private final String username;
    private final String password;
    private final String role;
    private final boolean oauth2;
    private final Map<String,Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    /**
     * Form-login constructor.
     */
    public UserPrincipal(Credentials credentials) {
        this(credentials, Collections.emptyMap(), null, null);
    }

    /**
     * OAuth2 constructor (attributes only).
     */
    public UserPrincipal(Credentials credentials, Map<String,Object> attributes) {
        this(credentials, attributes, null, null);
    }

    /**
     * OIDC constructor (idToken + userInfo).
     */
    public UserPrincipal(Credentials credentials,
                         OidcIdToken idToken,
                         OidcUserInfo userInfo) {
        this(credentials, Collections.emptyMap(), idToken, userInfo);
    }

    // Common initializer
    private UserPrincipal(Credentials credentials,
                          Map<String,Object> attributes,
                          OidcIdToken idToken,
                          OidcUserInfo userInfo) {
        User user = credentials.getUser();
        this.userId      = user.getId();
        this.displayName = user.getName() +
            (user.getSurname() != null && !user.getSurname().isBlank()
                ? " " + user.getSurname() : "");
        this.username    = credentials.getUsername();
        this.password    = credentials.getPassword();
        this.role        = credentials.getRole();
        this.oauth2      = (idToken != null) || !attributes.isEmpty();
        this.attributes  = attributes;
        this.idToken     = idToken;
        this.userInfo    = userInfo;
    }

    // --- Convenience getters ---
    public Long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Role string from Credentials */
    public String getRole() {
        return role;
    }

    /** true if OAuth2 or OIDC login */
    public boolean isOAuth2() {
        return oauth2;
    }

    /** alias (for templates) */
    public boolean isOauth2() {
        return oauth2;
    }

    // --- UserDetails interface ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority(role)
        );
    }

    @Override public String getPassword()             { return password; }
    @Override public String getUsername()             { return username; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    // --- OidcUser (and OAuth2User) ---
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
        return (sub != null ? sub.toString() : username);
    }

    @Override public OidcIdToken getIdToken()   { return idToken; }
    @Override public OidcUserInfo getUserInfo(){ return userInfo; }
    @Override public Map<String,Object> getClaims(){ return attributes; }
}
