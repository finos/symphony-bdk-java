package configuration;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class SymConfigTest {

  private SymConfig symConfigAgentUrl443;
  private SymConfig symConfigAgentUrl7443;
  private SymConfig symConfigPodUrl7443;
  private SymConfig symConfigDatafeedIdFilepathWithSeparator;
  private SymConfig symConfigDatafeedIdFilepathNull;
  private SymConfig symConfigDatafeedIdFilepathEmpty;

  @Before
  public void loadSymConfig(){
    symConfigAgentUrl443 = SymConfigLoader.loadFromFile("src/test/resources/sym-config-agent-url-443.json");
    assertNotNull(symConfigAgentUrl443);

    symConfigAgentUrl7443 = SymConfigLoader.loadFromFile("src/test/resources/sym-config.json");
    assertNotNull(symConfigAgentUrl7443);

    symConfigPodUrl7443 = SymConfigLoader.loadFromFile("src/test/resources/sym-config-agent-podport-7443.json");
    assertNotNull(symConfigPodUrl7443);

    symConfigDatafeedIdFilepathWithSeparator = SymConfigLoader.loadFromFile("src/test/resources/sym-config-datafeedIdFilepathWithSeparator.json");
    assertNotNull(symConfigDatafeedIdFilepathWithSeparator);

    symConfigDatafeedIdFilepathNull = SymConfigLoader.loadFromFile("src/test/resources/sym-config-datafeedIdFilepathNull.json");
    assertNotNull(symConfigDatafeedIdFilepathNull);

    symConfigDatafeedIdFilepathEmpty = SymConfigLoader.loadFromFile("src/test/resources/sym-config-datafeedIdFilepathEmpty.json");
    assertNotNull(symConfigDatafeedIdFilepathEmpty);
  }

  // agentUrl
  @Test
  public void testGetAgentUrl443(){
    final String expectedAgentUrl = "https://localhost/src";
    assertEquals(expectedAgentUrl, symConfigAgentUrl443.getAgentUrl());
  }

  @Test
  public void testGetAgentUrl7443(){
    final String expectedAgentUrl = "https://localhost:7443/src";
    assertEquals(expectedAgentUrl, symConfigAgentUrl7443.getAgentUrl());
  }
  // End agentUrl

  // podUrl
  @Test
  public void testGetPodUrl443(){
    final String expectedPodUrl = "https://localhost/src/test/resources";
    assertEquals(expectedPodUrl, symConfigAgentUrl443.getPodUrl());
  }

  @Test
  public void testGetPodUrl7443(){
    final String expectedPodUrl = "https://localhost:7443/src/test/resources";
    assertEquals(expectedPodUrl, symConfigPodUrl7443.getPodUrl());
  }
  // End podUrl

  // keyAuthUrl
  @Test
  public void testGetKeyAuthUrl443(){
    final String expectedKeyAutUrl = "https://localhost/src/test/resources";
    assertEquals(expectedKeyAutUrl, symConfigAgentUrl443.getKeyAuthUrl());
  }

  @Test
  public void testGetKeyAuthUrl7443(){
    final String expectedKeyAutUrl = "https://localhost:7443/src/test/resources";
    assertEquals(expectedKeyAutUrl, symConfigAgentUrl7443.getKeyAuthUrl());
  }
  // End keyAuthUrl

  // SessionAuthUrl
  @Test
  public void testGetSessionAuthUrl443(){
    final String expectedSessionAuthUrl = "https://localhost/test1";
    assertEquals(expectedSessionAuthUrl, symConfigAgentUrl443.getSessionAuthUrl());
  }

  @Test
  public void testGetSessionAuthUrl7443(){
    final String expectedSessionAuthUrl = "https://localhost:7443/test1";
    assertEquals(expectedSessionAuthUrl, symConfigAgentUrl7443.getSessionAuthUrl());
  }
  // End SessionAuthUrl

  // getDatafeedIdFilePath
  @Test
  public void testGetDataFeedIdPath(){
    final String expectedDataFilePath =  "src/test/resources/testdatafeed.id" + File.separator;
    assertEquals(expectedDataFilePath, symConfigAgentUrl7443.getDatafeedIdFilePath());
  }

  @Test
  public void testGetDataFeedIdPathEmpty(){
    final String expectedDataFilePath = "." + File.separator;
    assertEquals(expectedDataFilePath, symConfigDatafeedIdFilepathEmpty.getDatafeedIdFilePath());
  }

  @Test
  public void testGetDataFeedIdPathNull(){
    final String expectedDataFilePath = "." + File.separator;
    assertEquals(expectedDataFilePath, symConfigDatafeedIdFilepathNull.getDatafeedIdFilePath());
  }

  @Test
  public void testGetDataFeedIdPathWithSeparator(){
    final String dataFilePath = symConfigDatafeedIdFilepathWithSeparator.getDatafeedIdFilePath();
    assertTrue(dataFilePath.endsWith(File.separator));
  }
}
