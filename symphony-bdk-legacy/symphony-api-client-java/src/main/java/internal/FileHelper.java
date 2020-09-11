package internal;

import static org.apache.commons.io.IOUtils.toByteArray;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

/**
 * Helper class for reading files from either classpath or system path. Internal usage only.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class FileHelper {

  /**
   * Loads file content (as byte[]) from either system or classpath location.
   *
   * @param path Absolute file path or classpath location
   * @return content of the file
   */
  @SneakyThrows
  public static byte[] readFile(@Nonnull final String path) throws FileNotFoundException {

    byte[] content;

    if(!isClasspath(path) && new File(path).exists()) {
      try(FileInputStream fis = new FileInputStream(path)) {
          content = toByteArray(fis);
          logger.debug("File loaded from system path : {}", path);
        }
        catch (Exception ex) {
          throw new FileNotFoundException("Unable to load file from path : " + path);
        }
    }
    else if (FileHelper.class.getResource(classpath(path)) != null) {
      content = toByteArray(FileHelper.class.getResourceAsStream(classpath(path)));
      logger.debug("File loaded from classpath location : {}", path);
    }
    else {
      throw new FileNotFoundException("Unable to load file from path : " + path);
    }
    return content;
  }

  public static String path(String first, String... more) {
    return Paths.get(first, more).toString();
  }

  private static String classpath(String path) {
    return path.replace("classpath:", "");
  }

  private static boolean isClasspath(String path) {
    return path.startsWith("classpath:");
  }
}
