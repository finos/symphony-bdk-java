package internal;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;
import utils.HttpClientBuilderHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
  public static byte[] readFile(@Nonnull final String path) {

    byte[] content;

    if(new File(path).exists()) {
      content = IOUtils.toByteArray(new FileInputStream(path));
      logger.debug("File loaded from system path : {}", path);
    }
    else if (HttpClientBuilderHelper.class.getResource(path) != null) {
      content = IOUtils.toByteArray(HttpClientBuilderHelper.class.getResourceAsStream(path.replace("classpath:", "")));
      logger.debug("File loaded from classpath location : {}", path);
    } else {
      throw new FileNotFoundException("Unable to load custom truststore from path : " + path);
    }

    return content;
  }
}
