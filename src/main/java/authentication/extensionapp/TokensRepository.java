package authentication.extensionapp;

import model.AppAuthResponse;

import java.util.Optional;

public interface TokensRepository {

  AppAuthResponse save(AppAuthResponse appAuthResponse);

  Optional<AppAuthResponse> get(String appToken);
}
