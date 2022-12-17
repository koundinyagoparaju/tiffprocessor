package se.nanolyze.tiffprocessor.utils;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Log4j2
public class TempFile implements AutoCloseable {
  private final File file;

  public TempFile(String fileName) throws IOException {
    this.file = File.createTempFile("tmp", fileName);
  }

  public Path getPath() {
    return this.file.toPath();
  }

  @Override
  public void close() {
    if (!file.delete()) {
      log.warn("File {} is not deleted", file.getPath());
    }
  }
}
