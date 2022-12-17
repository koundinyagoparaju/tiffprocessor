package se.nanolyze.tiffprocessor.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;

@Service
public class NoiseReducerService {

  public InputStream process(@Nonnull File tiffFile) {
    var tiffReader = getTiffReader();
    try (var imageInputStream = new FileImageInputStream(tiffFile)) {
      tiffReader.setInput(imageInputStream);
      int numOfImages = tiffReader.getNumImages(true);
      if (numOfImages == 0) return FileInputStream.nullInputStream();
      return buildAverageImage(tiffReader, numOfImages);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private InputStream buildAverageImage(ImageReader tiffReader, int numOfImages)
      throws IOException {
    var firstImage = tiffReader.read(0);
    int width = firstImage.getWidth(),
        height = firstImage.getHeight(),
        numOfPixels = height * width * firstImage.getData().getNumDataElements();
    float[] summedPixels = new float[numOfPixels];
    int[] averagedPixels = new int[numOfPixels];
    BufferedImage averagedImage = firstImage;
    for (int i = 0; i < numOfImages; i++) {
      var image = tiffReader.read(i);
      var imageData = image.getData();
      int[] pixels = imageData.getPixels(0, 0, width, height, new int[numOfPixels]);
      for (int j = 0; j < summedPixels.length; j++) {
        summedPixels[j] = summedPixels[j] + pixels[j];
      }
    }
    for (int i = 0; i < summedPixels.length; i++) {
      summedPixels[i] = summedPixels[i] / numOfImages;
      averagedPixels[i] = Math.round(summedPixels[i]);
    }
    WritableRaster raster = averagedImage.getData().createCompatibleWritableRaster();
    raster.setPixels(0, 0, width, height, averagedPixels);
    averagedImage.setData(raster);
    var outputStream = new ByteArrayOutputStream(numOfPixels);
    ImageIO.write(averagedImage, "png", outputStream);
    return new ByteArrayInputStream(outputStream.toByteArray());
  }

  private ImageReader getTiffReader() {
    var imageReaders = ImageIO.getImageReadersByFormatName("tiff");
    while (imageReaders.hasNext()) {
      var imageReader = imageReaders.next();
      if (imageReader.getClass().getName().equals("com.sun.imageio.plugins.tiff.TIFFImageReader")) {
        return imageReader;
      }
    }
    throw new IllegalStateException("TIFFImageReader not found");
  }
}
