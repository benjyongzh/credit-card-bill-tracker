package com.credit_card_bill_tracker.backend.auth;

import com.credit_card_bill_tracker.backend.user.UserDTO;
import com.credit_card_bill_tracker.backend.user.UserRepository;
import com.credit_card_bill_tracker.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OidcUser> {

    private final UserRepository userRepository;
    private final UserService userService;
    private final OidcUserService delegate = new OidcUserService();

    @Override
    public OidcUser loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);
        String email = oidcUser.getEmail();
        userRepository.findByEmail(email).orElseGet(() -> {
            UserDTO dto = new UserDTO();
            dto.setUsername(email);
            dto.setEmail(email);
            dto.setPassword(UUID.randomUUID().toString());
            return userService.register(dto);
        });
        return oidcUser;
    }
}
