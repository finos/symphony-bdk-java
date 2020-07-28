package authentication.extensionapp;

import java.util.Optional;
import model.AppAuthResponse;

public interface TokensRepository {
    AppAuthResponse save(AppAuthResponse appAuthResponse);
    Optional<AppAuthResponse> get(String appToken);
}
