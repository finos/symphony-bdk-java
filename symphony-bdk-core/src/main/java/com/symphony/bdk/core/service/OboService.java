package com.symphony.bdk.core.service;

import com.symphony.bdk.core.auth.AuthSession;

import org.apiguardian.api.API;

/**
 * Obo-enabled service contract.
 *
 * @param <S> Obo service type.
 */
@API(status = API.Status.INTERNAL)
public interface OboService<S> {

  S obo(AuthSession oboSession);
}
