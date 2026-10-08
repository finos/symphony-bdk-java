package com.symphony.bdk.core.service.clientdata;

import com.symphony.bdk.gen.api.model.BlastList;
import com.symphony.bdk.gen.api.model.OrganisationStructure;
import com.symphony.bdk.gen.api.model.UserOrganisationsResponse;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Service interface exposing OBO-enabled endpoints to manage client data.
 */
@API(status = API.Status.STABLE)
public interface OboClientDataService {

  /**
   * Retrieves the authenticated user's aggregated client data (directories, workspaces, and
   * blast distribution lists) for the default {@code symphonyPrime} application.
   * {@link ClientDataService#getClientData()}
   *
   * @return a {@link UserOrganisationsResponse} containing the user's client data.
   */
  UserOrganisationsResponse getClientData();

  /**
   * Retrieves the authenticated user's aggregated organization data.
   *
   * @deprecated Use {@link #getClientData()} instead.
   * @return a {@link UserOrganisationsResponse} containing the user's organization data.
   */
  @Deprecated
  default UserOrganisationsResponse getUserOrganization() {
    return getClientData();
  }

  /**
   * Retrieves the authenticated user's custom directories/folders.
   * {@link ClientDataService#getUserDirectories()}
   *
   * @return a list of {@link OrganisationStructure} directories.
   */
  List<OrganisationStructure> getUserDirectories();

  /**
   * Retrieves the authenticated user's workspaces.
   * {@link ClientDataService#getUserWorkspaces()}
   *
   * @return a list of {@link OrganisationStructure} workspaces.
   */
  List<OrganisationStructure> getUserWorkspaces();

  /**
   * Retrieves the authenticated user's blast distribution lists, each with its recipients.
   * {@link ClientDataService#getUserDistributionLists()}
   *
   * @return a list of {@link BlastList} blast lists.
   */
  List<BlastList> getUserDistributionLists();
}
