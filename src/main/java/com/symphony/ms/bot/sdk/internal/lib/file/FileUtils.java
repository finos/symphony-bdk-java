package com.symphony.ms.bot.sdk.internal.lib.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for files
 */
public class FileUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

  /**
   * Creates a file with a content in a path in the filesystem
   *
   * @param path
   * @param content
   * @return the file
   * @throws IOException
   */
  public static File writeFile(String path, byte[] content) throws IOException {
    File file = new File(path);
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    if (!file.exists()) {
      file.createNewFile();
    }
    new FileOutputStream(file, false).write(content);
    return file;
  }

  /**
   * Deletes a file in a path from the filesystem
   *
   * @param path
   */
  public static void deleteDirectory(String path) {
    File directory = new File(path);
    if (directory.exists()) {
      File[] allContents = directory.listFiles();
      if (allContents != null) {
        for (File file : allContents) {
          if (!file.delete()) {
            LOGGER.error("Failure deleting file {}", file.getPath());
          }
        }
      }
      if (!directory.delete()) {
        LOGGER.error("Failure deleting file directory {}", directory.getPath());
      }
    }
  }

}
