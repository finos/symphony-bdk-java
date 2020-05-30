package internal.jersey;

import org.apiguardian.api.API;

import javax.ws.rs.core.Response;

/**
 * Helper class that facilitate recurrent Jersey {@link javax.ws.rs.client.Client} operations. Internal usage only.
 */
@API(status = API.Status.INTERNAL)
public class JerseyHelper {

  /**
   * Checks if {@link javax.ws.rs.client.Client} response is not successful.
   *
   * @param response Jersey {@link javax.ws.rs.client.Client} response.
   * @return true if response is not successful, false otherwise.
   */
  public static boolean isNotSuccess(Response response) {
    return !isSuccess(response);
  }

  /**
   * Checks if {@link javax.ws.rs.client.Client} response is successful.
   *
   * @param response Jersey {@link javax.ws.rs.client.Client} response.
   * @return true if response is successful, false otherwise.
   */
  public static boolean isSuccess(Response response) {
    return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
  }
}
