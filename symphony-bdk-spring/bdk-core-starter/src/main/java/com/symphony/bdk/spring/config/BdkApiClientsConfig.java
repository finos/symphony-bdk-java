package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.gen.api.AppEntitlementApi;
import com.symphony.bdk.gen.api.ApplicationApi;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.AuditTrailApi;
import com.symphony.bdk.gen.api.CertificatePodApi;
import com.symphony.bdk.gen.api.ConnectionApi;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.DisclaimerApi;
import com.symphony.bdk.gen.api.DlpPoliciesAndDictionaryManagementApi;
import com.symphony.bdk.gen.api.InfoBarriersApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.PresenceApi;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.SecurityApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.gen.api.UtilApi;
import com.symphony.bdk.gen.api.ViolationsApi;

import org.springframework.context.annotation.Bean;

/**
 * Injection of all available raw-APIs generated from the official Symphony Swagger specifications.
 *
 * <p>
 *   Please note that each raw-API requires a particular {@link com.symphony.bdk.core.api.invoker.ApiClient} as unique
 *   constructor parameter, depending on which component it targets:
 *   <ul>
 *     <li>Pod</li>
 *     <li>Agent</li>
 *   </ul>
 * </p>
 *
 * <p>
 *   Those beans are not intended to be used by end-developers. They are injected into the Spring application context
 *   in order to be consumed by the service layer that provides an improved developer experience. However, as we won't
 *   be able to wrap every single raw-API within a dedicated service class, end-developers will still have the ability
 *   to do it by themselves.
 * </p>
 *
 * @see <a href="https://github.com/symphonyoss/symphony-api-spec">symphonyoss/symphony-api-spec</a>
 */
public class BdkApiClientsConfig {

  @Bean
  public AppEntitlementApi appEntitlementApi(ApiClientFactory apiClientFactory) {
    return new AppEntitlementApi(apiClientFactory.getPodClient());
  }

  @Bean
  public ApplicationApi applicationApi(ApiClientFactory apiClientFactory) {
    return new ApplicationApi(apiClientFactory.getPodClient());
  }

  @Bean
  public AttachmentsApi attachmentsApi(ApiClientFactory apiClientFactory) {
    return new AttachmentsApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public AuditTrailApi auditTrailApi(ApiClientFactory apiClientFactory) {
    return new AuditTrailApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public CertificatePodApi certificatePodApi(ApiClientFactory apiClientFactory) {
    return new CertificatePodApi(apiClientFactory.getPodClient());
  }

  @Bean
  public ConnectionApi connectionApi(ApiClientFactory apiClientFactory) {
    return new ConnectionApi(apiClientFactory.getPodClient());
  }

  @Bean
  public DatafeedApi datafeedApi(ApiClientFactory apiClientFactory) {
    return new DatafeedApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public DefaultApi defaultApi(ApiClientFactory apiClientFactory) {
    return new DefaultApi(apiClientFactory.getPodClient());
  }

  @Bean
  public DisclaimerApi disclaimerApi(ApiClientFactory apiClientFactory) {
    return new DisclaimerApi(apiClientFactory.getPodClient());
  }

  @Bean
  public DlpPoliciesAndDictionaryManagementApi dlpPoliciesAndDictionaryManagementApi(ApiClientFactory apiClientFactory) {
    return new DlpPoliciesAndDictionaryManagementApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public InfoBarriersApi infoBarriersApi(ApiClientFactory apiClientFactory) {
    return new InfoBarriersApi(apiClientFactory.getPodClient());
  }

  @Bean
  public MessagesApi messagesApi(ApiClientFactory apiClientFactory) {
    return new MessagesApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public MessageApi messageApi(ApiClientFactory apiClientFactory) {
    return new MessageApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public PodApi podApi(ApiClientFactory apiClientFactory) {
    return new PodApi(apiClientFactory.getPodClient());
  }

  @Bean
  public PresenceApi presenceApi(ApiClientFactory apiClientFactory) {
    return new PresenceApi(apiClientFactory.getPodClient());
  }

  @Bean
  public RoomMembershipApi roomMembershipApi(ApiClientFactory apiClientFactory) {
    return new RoomMembershipApi(apiClientFactory.getPodClient());
  }

  @Bean
  public SessionApi sessionApi(ApiClientFactory apiClientFactory) {
    return new SessionApi(apiClientFactory.getPodClient());
  }

  @Bean
  public SecurityApi securityApi(ApiClientFactory apiClientFactory) {
    return new SecurityApi(apiClientFactory.getPodClient());
  }

  @Bean
  public ShareApi shareApi(ApiClientFactory apiClientFactory) {
    return new ShareApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public SignalsApi signalsApi(ApiClientFactory apiClientFactory) {
    return new SignalsApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public StreamsApi streamsApi(ApiClientFactory apiClientFactory) {
    return new StreamsApi(apiClientFactory.getPodClient());
  }

  @Bean
  public SystemApi systemApi(ApiClientFactory apiClientFactory) {
    return new SystemApi(apiClientFactory.getPodClient());
  }

  @Bean
  public UserApi userApi(ApiClientFactory apiClientFactory) {
    return new UserApi(apiClientFactory.getPodClient());
  }

  @Bean
  public UsersApi usersApi(ApiClientFactory apiClientFactory) {
    return new UsersApi(apiClientFactory.getPodClient());
  }

  @Bean
  public UtilApi utilApi(ApiClientFactory apiClientFactory) {
    return new UtilApi(apiClientFactory.getAgentClient());
  }

  @Bean
  public ViolationsApi violationsApi(ApiClientFactory apiClientFactory) {
    return new ViolationsApi(apiClientFactory.getAgentClient());
  }
}
