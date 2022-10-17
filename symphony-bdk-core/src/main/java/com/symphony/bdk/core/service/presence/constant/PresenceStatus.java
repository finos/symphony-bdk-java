package com.symphony.bdk.core.service.presence.constant;

import org.apiguardian.api.API;

/**
 * The list of all possible values for the presence status.
 * @see <a href="https://developers.symphony.com/restapi/reference/set-presence">Set Presence</a>
 */
@API(status = API.Status.STABLE)
public enum PresenceStatus {
  AVAILABLE,
  BUSY,
  AWAY,
  ON_THE_PHONE,
  BE_RIGHT_BACK,
  IN_A_MEETING,
  OUT_OF_OFFICE,
  OFF_WORK
}
