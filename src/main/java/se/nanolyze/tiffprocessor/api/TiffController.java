package se.nanolyze.tiffprocessor.api;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.nanolyze.tiffprocessor.service.NoiseReducerService;
import se.nanolyze.tiffprocessor.utils.TempFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/tiff")
@Log4j2
public class TiffController {
  private final NoiseReducerService noiseReducerService;

  public TiffController(NoiseReducerService noiseReducerService) {
    this.noiseReducerService = noiseReducerService;
  }

  @PostMapping("/reduce-noise")
  public ResponseEntity<InputStreamResource> reduceNoiseInImage(
      @RequestParam("file") MultipartFile inputTiff) {
    var fileName = Optional.ofNullable(inputTiff.getOriginalFilename()).orElse("");
    if (fileName.endsWith(".tif") | fileName.endsWith(".tiff")) {
      try (TempFile tempFile = new TempFile(inputTiff.getName())) {
        inputTiff.transferTo(tempFile.getPath());
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("image/png"))
            .body(
                new InputStreamResource(noiseReducerService.process(tempFile.getPath().toFile())));
      } catch (Exception e) {
        log.error("Unable to process the tiff image to reduce noise");
        return ResponseEntity.unprocessableEntity().build();
      }
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
