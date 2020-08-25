package com.symphony.bdk.bot.sdk.webapi.accesscontrol;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Simple file-based basic authentication service
 *
 * @author Marcus Secato
 *
 */
@Service
public class BasicAuthProvider implements AuthenticationProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthProvider.class);

  private static final String AUTHORITY = "admin";
  private BasicAuthProps authProps;
  private HashService hashService;

  public BasicAuthProvider(BasicAuthProps authProps, HashService hashService) {
    this.authProps = authProps;
    this.hashService = hashService;
  }

  /**
   * Checks if the Basic Authentication credentials are correct
   *
   * @param auth name and hashedPassword to be hashed
   */
  @Override
  public Authentication authenticate(Authentication auth) {
    try {
      Collection<? extends GrantedAuthority> authorities =
          Collections.singleton(new SimpleGrantedAuthority(AUTHORITY));
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
              auth.getName(), auth.getCredentials().toString(), authorities);

      if (authProps.isBasicAuth()) {
        String userToken = hashService.generateHash(
            auth.getCredentials().toString(), authProps.getSalt());

        if (authProps.getName().equals(auth.getName())
            && hashService.checkToken(userToken, authProps.getHashedPassword())) {
          return authenticationToken;
        }
      }
    } catch (NoSuchAlgorithmException e) {
      LOGGER.info("Error authenticating user {}",  e.getMessage());
    }
    return null;
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return true;
  }

}
