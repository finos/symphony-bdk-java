package internal;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileHelperTest {

  @Test
  public void should_read_file_from_classpath() throws FileNotFoundException {
    assertNotNull(FileHelper.readFile("/avatar.png"));
    assertNotNull(FileHelper.readFile("classpath:/avatar.png"));
  }

  @Test
  public void should_read_file_from_system() throws IOException {
    final Path file = Files.createTempFile(UUID.randomUUID().toString(), ".txt");
    assertNotNull(FileHelper.readFile(file.toAbsolutePath().toString()));
    Files.delete(file);
  }

  @Test(expected = FileNotFoundException.class)
  public void fail_to_read_file() throws FileNotFoundException {
    FileHelper.readFile(UUID.randomUUID().toString() + ".abc");
  }
}