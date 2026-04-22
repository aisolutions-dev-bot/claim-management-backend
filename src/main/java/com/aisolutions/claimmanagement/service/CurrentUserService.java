package com.aisolutions.claimmanagement.service;

import com.aisolutions.claimmanagement.client.OrganizationAuthClient;
import com.aisolutions.claimmanagement.dto.UserDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Resolves the currently authenticated user.
 *
 * Delegates to the remote Organization Auth service (via OrganizationAuthClient).
 * On any failure (no token, service down, 401, etc.) it returns an anonymous
 * UserDTO so downstream code can proceed without NullPointerException —
 * the frontend currently defaults to "superdrew" until your login is wired up.
 */
@ApplicationScoped
public class CurrentUserService {

    public static final String ANONYMOUS_STAFF_ID = "superdrew";

    @Inject
    @RestClient
    OrganizationAuthClient authClient;

    /**
     * Returns the current user, or an anonymous fallback on any error.
     * Never throws; never returns null.
     */
    public Uni<UserDTO> getCurrentUser() {
        return authClient.getCurrentUser()
            .onFailure().recoverWithItem(err -> {
                System.err.println("[CurrentUserService] Auth lookup failed: " + err.getMessage());
                return anonymous();
            })
            .onItem().ifNull().continueWith(this::anonymous);
    }

    private UserDTO anonymous() {
        UserDTO u = new UserDTO();
        u.setStaffId(ANONYMOUS_STAFF_ID);
        return u;
    }
}
