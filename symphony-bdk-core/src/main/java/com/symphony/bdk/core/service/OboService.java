package com.symphony.bdk.core.service;

import com.symphony.bdk.core.auth.AuthSession;

/**
 * Obo-enabled service contract.
 *
 * @param <S> Obo service type.
 */
public interface OboService<S> {

  S obo(AuthSession oboSession);
}
