package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
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
import com.symphony.bdk.gen.api.MessageSuppressionApi;
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

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * Injection of all available raw-APIs generated from the official Symphony Swagger specifications.
 *
 * <p>
 * Please note that each raw-API requires a particular {@link com.symphony.bdk.core.api.invoker.ApiClient} as unique
 * constructor parameter, depending on which component it targets:
 *   <ul>
 *     <li>Pod</li>
 *     <li>Agent</li>
 *   </ul>
 * </p>
 *
 * <p>
 *   Those beans are not "really" intended to be used by end-developers. They are injected into the Spring application context
 *   in order to be consumed by the service layer that provides an improved developer experience. However, as we won't
 *   be able to wrap every single raw-API within a dedicated service class, end-developers will still have the ability
 *   to do it by themselves.
 * </p>
 *
 * @see <a href="https://github.com/symphonyoss/symphony-api-spec">symphonyoss/symphony-api-spec</a>
 */
public class BdkApiClientsConfig {

  @Autowired
  private ApiClientFactory clientFactory;

  @Bean
  public AppEntitlementApi appEntitlementApi() {
    return new AppEntitlementApi(podApiClient());
  }

  @Bean
  public ApplicationApi applicationApi() {
    return new ApplicationApi(podApiClient());
  }

  @Bean
  public AttachmentsApi attachmentsApi() {
    return new AttachmentsApi(agentApiClient());
  }

  @Bean
  public AuditTrailApi auditTrailApi() {
    return new AuditTrailApi(agentApiClient());
  }

  @Bean
  public CertificatePodApi certificatePodApi() {
    return new CertificatePodApi(podApiClient());
  }

  @Bean
  public ConnectionApi connectionApi() {
    return new ConnectionApi(podApiClient());
  }

  @Bean
  public DatafeedApi datafeedApi() {
    return new DatafeedApi(agentApiClient());
  }

  @Bean
  public DefaultApi defaultApi() {
    return new DefaultApi(podApiClient());
  }

  @Bean
  public DisclaimerApi disclaimerApi() {
    return new DisclaimerApi(podApiClient());
  }

  @Bean
  public DlpPoliciesAndDictionaryManagementApi dlpPoliciesAndDictionaryManagementApi() {
    return new DlpPoliciesAndDictionaryManagementApi(agentApiClient());
  }

  @Bean
  public InfoBarriersApi infoBarriersApi() {
    return new InfoBarriersApi(podApiClient());
  }

  @Bean
  public MessagesApi messagesApi() {
    return new MessagesApi(agentApiClient());
  }

  @Bean
  public MessageApi messageApi() {
    return new MessageApi(podApiClient());
  }

  @Bean
  public MessageSuppressionApi messageSuppressionApi() {
    return new MessageSuppressionApi(podApiClient());
  }

  @Bean
  public PodApi podApi() {
    return new PodApi(podApiClient());
  }

  @Bean
  public PresenceApi presenceApi() {
    return new PresenceApi(podApiClient());
  }

  @Bean
  public RoomMembershipApi roomMembershipApi() {
    return new RoomMembershipApi(podApiClient());
  }

  @Bean
  public SessionApi sessionApi() {
    return new SessionApi(podApiClient());
  }

  @Bean
  public SecurityApi securityApi() {
    return new SecurityApi(podApiClient());
  }

  @Bean
  public ShareApi shareApi() {
    return new ShareApi(agentApiClient());
  }

  @Bean
  public SignalsApi signalsApi() {
    return new SignalsApi(agentApiClient());
  }

  @Bean
  public StreamsApi streamsApi() {
    return new StreamsApi(podApiClient());
  }

  @Bean
  public SystemApi systemApi() {
    return new SystemApi(podApiClient());
  }

  @Bean
  public UserApi userApi() {
    return new UserApi(podApiClient());
  }

  @Bean
  public UsersApi usersApi() {
    return new UsersApi(podApiClient());
  }

  @Bean
  public UtilApi utilApi() {
    return new UtilApi(agentApiClient());
  }

  @Bean
  public ViolationsApi violationsApi() {
    return new ViolationsApi(agentApiClient());
  }

  private ApiClient agentApiClient() {
    try {
      return this.clientFactory.getAgentClient();
    } catch (ApiClientInitializationException e) {
      throw new BeanCreationException("Unable to build Agent's ApiClient", e);
    }
  }

  private ApiClient podApiClient() {
    try {
      return this.clientFactory.getPodClient();
    } catch (ApiClientInitializationException e) {
      throw new BeanCreationException("Unable to build Agent's ApiClient", e);
    }
  }
}
