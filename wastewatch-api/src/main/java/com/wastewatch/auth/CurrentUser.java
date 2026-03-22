package com.wastewatch.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {

    /**
     * Returns the Supabase user UUID from the JWT.
     * Supabase puts the user id in the "sub" claim.
     * Call this anywhere you need to know who is logged in.
     */
    public UUID getId() {
        return UUID.fromString(getJwt().getSubject());
    }

    /**
     * Returns the user's email from the JWT.
     * Supabase includes email in the token claims.
     */
    public String getEmail() {
        return getJwt().getClaimAsString("email");
    }

    /**
     * Returns the user's role from the JWT.
     * We set custom claims in Supabase for: "citizen" or "authority_officer"
     */
    public String getRole() {
        return getJwt().getClaimAsString("user_role");
    }

    /**
     * Returns true if the current user is an authority officer.
     */
    public boolean isOfficer() {
        return "authority_officer".equals(getRole());
    }

    /**
     * Returns the raw JWT — useful for reading any other claim.
     */
    public Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Jwt) auth.getPrincipal();
    }
}