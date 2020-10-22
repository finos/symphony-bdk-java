package com.symphony.bdk.core.service.connection.constant;

import org.apiguardian.api.API;

/**
 * The list of all possible values for the request listing connection status.
 * @see <a href="https://developers.symphony.com/restapi/reference#list-connections">List Connections</a>
 */
@API(status = API.Status.STABLE)
public enum ConnectionStatus {
  ALL,
  PENDING_INCOMING,
  PENDING_OUTGOING,
  ACCEPTED,
  REJECTED
}
