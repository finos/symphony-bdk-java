package authentication.extensionapp;

import authentication.extensionapp.InMemoryTokensRepository;
import java.util.Optional;
import model.AppAuthResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InMemoryTokensRepositoryTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void getTest() throws Exception {
    // Arrange
    InMemoryTokensRepository inMemoryTokensRepository = new InMemoryTokensRepository();
    String appToken = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    inMemoryTokensRepository.get(appToken);
  }

  @Test
  public void saveTest() throws Exception {
    // Arrange
    InMemoryTokensRepository inMemoryTokensRepository = new InMemoryTokensRepository();
    AppAuthResponse appAuthResponse = new AppAuthResponse();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    inMemoryTokensRepository.save(appAuthResponse);
  }
}
