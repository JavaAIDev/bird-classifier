package com.javaaidev.birdclassifier;

import java.awt.image.BufferedImage;

public class ImageUtils {

  public static BufferedImage resizeImage(BufferedImage sourceImage,
      int targetWidth,
      int targetHeight) {
    var image = sourceImage.getScaledInstance(targetWidth, targetHeight,
        BufferedImage.SCALE_AREA_AVERAGING);
    var outputImage = new BufferedImage(targetWidth, targetHeight,
        BufferedImage.TYPE_INT_ARGB);
    outputImage.getGraphics().drawImage(image, 0, 0, null);
    return outputImage;
  }
}
