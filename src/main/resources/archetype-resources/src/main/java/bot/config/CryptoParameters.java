package ${package}.bot.config;

import com.bol.crypt.CryptVault;
import com.bol.secure.CachedEncryptionEventListener;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Data
@Configuration
@ConfigurationProperties(prefix = "crypto")
public class CryptoParameters {

  private String password;

  @Bean
  public CryptVault cryptVault() {
    byte[] secretKey = Base64.getDecoder().decode(password);
    return new CryptVault()
        .with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(0, secretKey);
  }

  @Bean
  public CachedEncryptionEventListener encryptionEventListener(CryptVault cryptVault) {
    return new CachedEncryptionEventListener(cryptVault);
  }
}
