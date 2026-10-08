package com.symphony.bdk.core.service.clientdata;

import com.symphony.bdk.gen.api.model.ClientDataResponse;
import com.symphony.bdk.gen.api.model.DistributionLists;
import com.symphony.bdk.gen.api.model.ClientDataStructure;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Service interface exposing OBO-enabled endpoints to manage client data.
 */
@API(status = API.Status.STABLE)
public interface OboClientDataService {

  /**
   * Retrieves the authenticated user's aggregated client data (folders, workspaces, and
   * distribution lists) for the default {@code symphonyPrime} application.
   * {@link ClientDataService#getClientData()}
   *
   * @return a {@link ClientDataResponse} containing the user's client data.
   */
  ClientDataResponse getClientData();

  /**
   * Retrieves the authenticated user's custom folders.
   * {@link ClientDataService#getUserFolders()}
   *
   * @return a list of {@link ClientDataStructure} folders.
   */
  List<ClientDataStructure> getUserFolders();

  /**
   * Retrieves the authenticated user's custom directories/folders.
   *
   * @deprecated Use {@link #getUserFolders()} instead.
   * @return a list of {@link ClientDataStructure} directories.
   */
  @Deprecated
  default List<ClientDataStructure> getUserDirectories() {
    return getUserFolders();
  }

  /**
   * Retrieves the authenticated user's workspaces.
   * {@link ClientDataService#getUserWorkspaces()}
   *
   * @return a list of {@link ClientDataStructure} workspaces.
   */
  List<ClientDataStructure> getUserWorkspaces();

  /**
   * Retrieves the authenticated user's distribution lists, each with its recipients.
   * {@link ClientDataService#getUserDistributionLists()}
   *
   * @return a list of {@link DistributionLists} distribution lists.
   */
  List<DistributionLists> getUserDistributionLists();
}
