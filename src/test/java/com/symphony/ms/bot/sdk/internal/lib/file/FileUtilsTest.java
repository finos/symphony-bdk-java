package com.symphony.ms.bot.sdk.internal.lib.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FileUtilsTest {

  private static String TEST_DIR = "/tmp/symphony/test";
  private static String CONTENT = "content_";

  @Test
  public void shouldCreateFiles() throws IOException {
    List<File> files = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      files.add(
          FileUtils.writeFile(TEST_DIR + "/" + "test_" + i + ".txt", (CONTENT + i).getBytes()));
    }
    for (File file : files) {
      assertTrue(file.exists());
      byte[] fileContent = new byte[10];
      new FileInputStream(file).read(fileContent);
      assertEquals(CONTENT + file.getName().charAt(5), new String(fileContent).trim());
    }
  }

  @Test
  public void shouldDeleteEmptyDirectory() {
    new File(TEST_DIR).mkdir();

    FileUtils.deleteDirectory(TEST_DIR);

    assertFalse(new File(TEST_DIR).exists());
  }

  @Test
  public void shouldDeleteNotEmptyDirectory() throws IOException {
    List<File> files = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      files.add(
          FileUtils.writeFile(TEST_DIR + "/" + "test_" + i + ".txt", (CONTENT + i).getBytes()));
    }

    FileUtils.deleteDirectory(TEST_DIR);

    assertFalse(new File(TEST_DIR).exists());
  }

  @AfterEach
  public void tearDown() {
    File testDir = new File(TEST_DIR);
    if (testDir.exists()) {
      for (File file : testDir.listFiles()) {
        file.delete();
      }
      testDir.delete();
    }
  }

}
