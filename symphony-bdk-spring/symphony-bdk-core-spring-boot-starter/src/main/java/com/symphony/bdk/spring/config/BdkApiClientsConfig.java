package com.symphony.bdk.spring.config;

import com.symphony.bdk.http.api.ApiClient;
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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Injection of all available raw-APIs generated from the official Symphony Swagger specifications.
 *
 * <p>
 * Please note that each raw-API requires a particular {@link ApiClient} as unique
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

  @Bean
  @ConditionalOnMissingBean
  public AppEntitlementApi appEntitlementApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new AppEntitlementApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public ApplicationApi applicationApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new ApplicationApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public AttachmentsApi attachmentsApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new AttachmentsApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public AuditTrailApi auditTrailApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new AuditTrailApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public CertificatePodApi certificatePodApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new CertificatePodApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public ConnectionApi connectionApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new ConnectionApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public DatafeedApi datafeedApi(@Qualifier("datafeedAgentApiClient") ApiClient agentApiClient) {
    return new DatafeedApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public DefaultApi defaultApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new DefaultApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public DisclaimerApi disclaimerApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new DisclaimerApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public DlpPoliciesAndDictionaryManagementApi dlpPoliciesAndDictionaryManagementApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new DlpPoliciesAndDictionaryManagementApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public InfoBarriersApi infoBarriersApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new InfoBarriersApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public MessagesApi messagesApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new MessagesApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageApi messageApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new MessageApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageSuppressionApi messageSuppressionApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new MessageSuppressionApi(podApiClient);
  }

  @Bean
  public PodApi podApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new PodApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public PresenceApi presenceApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new PresenceApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public RoomMembershipApi roomMembershipApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new RoomMembershipApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public SessionApi sessionApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new SessionApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public SecurityApi securityApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new SecurityApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public ShareApi shareApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new ShareApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public SignalsApi signalsApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new SignalsApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public StreamsApi streamsApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new StreamsApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public SystemApi systemApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new SystemApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public UserApi userApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new UserApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public UsersApi usersApi(@Qualifier("podApiClient") ApiClient podApiClient) {
    return new UsersApi(podApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public UtilApi utilApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new UtilApi(agentApiClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public ViolationsApi violationsApi(@Qualifier("agentApiClient") ApiClient agentApiClient) {
    return new ViolationsApi(agentApiClient);
  }
}
