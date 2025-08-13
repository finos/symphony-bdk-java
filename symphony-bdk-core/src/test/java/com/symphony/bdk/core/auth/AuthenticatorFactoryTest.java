package com.symphony.bdk.core.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.impl.AuthenticatorFactoryImpl;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.ExtensionAppAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.ExtensionAppAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRsaImpl;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkCertificateConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkRsaKeyConfig;
import com.symphony.bdk.core.test.RsaTestHelper;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.jersey2.ApiClientBuilderJersey2;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Test class for the {@link AuthenticatorFactoryImpl}.
 */
class AuthenticatorFactoryTest {

  private static final ApiClient DUMMY_API_CLIENT = new ApiClientBuilderJersey2().build();
  private static final String RSA_PRIVATE_KEY = RsaTestHelper.generatePrivateKeyAsString();
  private static ApiClientFactory DUMMY_API_CLIENT_FACTORY;

  @SneakyThrows
  @BeforeAll
  public static void setup() {
    DUMMY_API_CLIENT_FACTORY = mock(ApiClientFactory.class);
    when(DUMMY_API_CLIENT_FACTORY.getLoginClient()).thenReturn(DUMMY_API_CLIENT);
    when(DUMMY_API_CLIENT_FACTORY.getRelayClient()).thenReturn(DUMMY_API_CLIENT);
    when(DUMMY_API_CLIENT_FACTORY.getSessionAuthClient()).thenReturn(DUMMY_API_CLIENT);
    when(DUMMY_API_CLIENT_FACTORY.getKeyAuthClient()).thenReturn(DUMMY_API_CLIENT);
  }

  @Test
  void testGetBotAuthenticatorWithValidPrivateKey(@TempDir Path tempDir) throws AuthInitializationException {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath(RSA_PRIVATE_KEY, privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    final BotAuthenticator botAuth = factory.getBotAuthenticator();
    assertNotNull(botAuth);
    assertEquals(BotAuthenticatorRsaImpl.class, botAuth.getClass());
  }

  @Test
  void testGetBotAuthenticatorWithValidPrivateKeyInClasspath() throws AuthInitializationException {
    final BdkConfig config = createRsaConfig(() -> "classpath:/keys/private-key.pem");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    final BotAuthenticator botAuth = factory.getBotAuthenticator();
    assertNotNull(botAuth);
    assertEquals(BotAuthenticatorRsaImpl.class, botAuth.getClass());
  }

  @Test
  void testGetBotAuthenticatorWithPrivateKeyNotFoundInClasspath() {
    final BdkConfig config = createRsaConfig(() -> "classpath:/keys/notfound.pem");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    assertThrows(AuthInitializationException.class, () -> factory.getBotAuthenticator());
  }

  @Test
  void testGetAuthenticatorWithValidPrivateKeyContent() throws AuthInitializationException {

    final BdkConfig config = new BdkConfig();
    config.getBot().getPrivateKey().setContent(RSA_PRIVATE_KEY.getBytes());

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    final BotAuthenticator botAuth = factory.getBotAuthenticator();

    assertEquals(BotAuthenticatorRsaImpl.class, botAuth.getClass());
  }

  @Test
  void testGetAuthenticatorWithInvalidPrivateKeyContent() {

    final BdkConfig config = new BdkConfig();
    config.getBot().getPrivateKey().setContent("invalid-key".getBytes());

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetBotAuthenticatorWithInvalidPrivateKey(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath("invalid-private-key-content", privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetBotAuthenticatorWithInvalidPrivateKeyUsingDeprecatedField(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfigDeprecatedPath(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath("invalid-private-key-content", privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetBotAuthenticatorWithNotFoundPrivateKey(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfig(() -> tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem").toAbsolutePath().toString());

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetOboAuthenticatorWithValidPrivateKey(@TempDir Path tempDir) throws AuthInitializationException {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath(RSA_PRIVATE_KEY, privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    final OboAuthenticator oboAuth = factory.getOboAuthenticator();
    assertNotNull(oboAuth);
    assertEquals(OboAuthenticatorRsaImpl.class, oboAuth.getClass());
  }

  @Test
  void testGetBotCertificateAuthenticator() throws AuthInitializationException {
    final BdkConfig config = new BdkConfig();
    config.getBot().getCertificate().setPath("/path/to/cert/file.p12");
    config.getBot().getCertificate().setPassword("password");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    BotAuthenticator botAuthenticator = factory.getBotAuthenticator();
    assertNotNull(botAuthenticator);
    assertEquals(BotAuthenticatorCertImpl.class, botAuthenticator.getClass());
  }

  @Test
  void testGetOboCertificateAuthenticator() throws AuthInitializationException {
    final BdkConfig config = new BdkConfig();
    config.getApp().getCertificate().setPath("/path/to/cert/file.p12");
    config.getApp().getCertificate().setPassword("password");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    OboAuthenticator oboAuthenticator = factory.getOboAuthenticator();
    assertNotNull(oboAuthenticator);
    assertEquals(OboAuthenticatorCertImpl.class, oboAuthenticator.getClass());
  }

  @Test
  void testGetExtAppAuthenticatorWithValidPrivateKey(@TempDir Path tempDir) throws AuthInitializationException {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath(RSA_PRIVATE_KEY, privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    final ExtensionAppAuthenticator botAuth = factory.getExtensionAppAuthenticator();

    assertEquals(ExtensionAppAuthenticatorRsaImpl.class, botAuth.getClass());
  }

  @Test
  void testGetExtAppAuthenticatorWithValidCertificatePath(@TempDir Path tempDir) throws AuthInitializationException {
    final BdkConfig config = new BdkConfig();
    config.getApp().getCertificate().setPath("/path/to/cert/file.p12");
    config.getApp().getCertificate().setPassword("password");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);
    final ExtensionAppAuthenticator extAppAuthenticator = factory.getExtensionAppAuthenticator();

    assertEquals(ExtensionAppAuthenticatorCertImpl.class, extAppAuthenticator.getClass());
  }

  @Test
  void testGetAuthenticatorWithBothCertificatePathAndContentConfigured() {
    final BdkConfig config = new BdkConfig();
    config.getBot().getCertificate().setPath("/path/to/cert/file.p12");
    config.getBot().getCertificate().setPassword("password");
    config.getBot().getCertificate().setContent("certificate-content".getBytes());
    config.getApp().getCertificate().setPath("/path/to/cert/file.p12");
    config.getApp().getCertificate().setPassword("password");
    config.getApp().getCertificate().setContent("certificate-content".getBytes());
    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getOboAuthenticator);
  }

  @Test
  void testGetAuthenticatorWithBothPrivateKeyPathAndContentConfigured() {
    final BdkConfig config = new BdkConfig();
    config.getBot().getPrivateKey().setPath("/path/to/cert/privatekey.pem");
    config.getBot().getPrivateKey().setContent("privatekey-content".getBytes());
    config.getApp().getPrivateKey().setPath("/path/to/cert/privatekey.pem");
    config.getApp().getPrivateKey().setContent("privatekey-content".getBytes());
    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getOboAuthenticator);
  }

  @Test
  void testGetExtAppAuthenticatorWithInvalidPrivateKey(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath("invalid-private-key-content", privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
  }

  @Test
  void testGetExtAppAuthenticatorWithNotFoundPrivateKey(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfig(() -> tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem").toAbsolutePath().toString());

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
  }

  @Test
  void testGetAuthenticationRsaAndCertificateNotConfigured() {

    final BdkConfig config = new BdkConfig();

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getOboAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
  }

  @Test
  void testGetBotAuthenticatorBothRsaAndCertificateConfigured(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath(RSA_PRIVATE_KEY, privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });
    config.getBot().getCertificate().setPath("/path/to/cert/file.p12");
    config.getBot().getCertificate().setPassword("password");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetAppAuthenticatorBothRsaAndCertificateConfigured(@TempDir Path tempDir) {

    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath(RSA_PRIVATE_KEY, privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });
    config.getApp().getCertificate().setPath("/path/to/cert/file.p12");
    config.getApp().getCertificate().setPassword("password");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getOboAuthenticator);
  }

  @Test
  void testGetAuthenticatorRsaInvalid(@TempDir Path tempDir) {
    final BdkConfig config = createRsaConfig(() -> {
      final Path privateKeyPath = tempDir.resolve(UUID.randomUUID().toString() + "-privateKey.pem");
      writeContentToPath(RSA_PRIVATE_KEY, privateKeyPath);
      return privateKeyPath.toAbsolutePath().toString();
    });
    BdkRsaKeyConfig privateKey = new BdkRsaKeyConfig();
    privateKey.setPath(config.getBot().getPrivateKeyPath());
    config.getBot().setPrivateKey(privateKey);
    config.getApp().setPrivateKey(privateKey);

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getExtensionAppAuthenticator);
    assertThrows(AuthInitializationException.class, factory::getOboAuthenticator);
  }

  @Test
  void testGetAuthenticatorCertificateInvalid() {
    final BdkConfig config = new BdkConfig();
    config.getBot().setCertificate(new BdkCertificateConfig());
    config.getBot().getCertificate().setPath("/path/to/cert/cert.pem");
    config.getBot().setCertificatePath("/path/to/cert/cert.pem");

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetAuthenticatorBothPathAndContentInCertificateField() {
    final BdkConfig config = new BdkConfig();
    config.getBot().setCertificate(new BdkCertificateConfig());
    config.getBot().getCertificate().setPath("/path/to/cert/cert.pem");
    config.getBot().getCertificate().setContent("certificate-content".getBytes());

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @Test
  void testGetAuthenticatorBothPathAndContentInPrivateKeyField() {
    final BdkConfig config = new BdkConfig();
    config.getBot().setPrivateKey(new BdkRsaKeyConfig());
    config.getBot().getPrivateKey().setPath("/path/to/cert/privatekey.pem");
    config.getBot().getPrivateKey().setContent("privatekey-content".getBytes());

    final AuthenticatorFactory factory = new AuthenticatorFactoryImpl(config, DUMMY_API_CLIENT_FACTORY);

    assertThrows(AuthInitializationException.class, factory::getBotAuthenticator);
  }

  @SneakyThrows
  private BdkConfig createRsaConfig(Supplier<String> privateKeyPathSupplier) {
    final BdkConfig config = new BdkConfig();

    config.getBot().getPrivateKey().setPath(privateKeyPathSupplier.get());
    config.getBot().setUsername("bot-" + UUID.randomUUID().toString());

    config.getApp().getPrivateKey().setPath(privateKeyPathSupplier.get());
    config.getApp().setAppId("app-" + UUID.randomUUID().toString());

    return config;
  }

  @SneakyThrows
  private BdkConfig createRsaConfigDeprecatedPath(Supplier<String> privateKeyPathSupplier) {
    final BdkConfig config = new BdkConfig();

    config.getBot().setPrivateKeyPath(privateKeyPathSupplier.get());
    config.getBot().setUsername("bot-" + UUID.randomUUID().toString());

    return config;
  }

  @SneakyThrows
  private static void writeContentToPath(String content, Path path) {
    try (OutputStream out = new FileOutputStream(path.toFile())) {
      IOUtils.write(content, out, StandardCharsets.UTF_8);
    }
  }
}
