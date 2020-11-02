package com.symphony.bdk.http.api;

/**
 * Abstract class to be inherited by concrete implementations of ApiClient
 * which do not override {@link ApiClient#rotate()}
 * i.e. target base url never changes.
 */
public abstract class RegularApiClient implements ApiClient {
}
