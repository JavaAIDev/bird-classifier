package javaaidev.birdclassifier;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.SessionOptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.Map;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Classifier {

  private static final OrtEnvironment env = OrtEnvironment.getEnvironment();

  private final int imageWidth;
  private final int imageHeight;

  private static final Logger LOGGER = LoggerFactory.getLogger("Classifier");

  public Classifier(int imageWidth, int imageHeight) {
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
  }

  public String classify(URL url) throws OrtException, IOException {
    LOGGER.info("Classify {}", url);
    try (OrtSession.SessionOptions options = new SessionOptions();
        InputStream modelStream = getClass().getResourceAsStream("/model.onnx");
        OrtSession session = env.createSession(modelStream.readAllBytes(),
            options);
        InputStream configJsonStream = getClass().getResourceAsStream(
            "/config.json")
    ) {
      var tensor = imageDataToTensor(url);
      var inputName = session.getInputNames().stream().toList().getFirst();
      var outputData = session.run(Map.of(
          inputName, tensor
      ));
      var objectMapper = new ObjectMapper();
      var config = objectMapper.readValue(
          configJsonStream,
          new TypeReference<Map<String, Object>>() {
          });
      var id2label = (Map<?, ?>) config.get("id2label");
      try (var output = outputData.get(0)) {
        float[][] values = (float[][]) output.getValue();
        var result = argmax(values[0]);
        return (String) id2label.get(Integer.toString(result));
      }
    }
  }

  OnnxTensor imageDataToTensor(URL url) throws IOException, OrtException {
    var bufferedImage = ImageUtils.resizeImage(ImageIO.read(url), imageWidth,
        imageHeight);
    var height = bufferedImage.getHeight();
    var width = bufferedImage.getWidth();
    var size = width * height;
    var r = new int[size];
    var g = new int[size];
    var b = new int[size];
    var index = 0;
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        var color = new Color(bufferedImage.getRGB(w, h));
        r[index] = color.getRed();
        g[index] = color.getGreen();
        b[index] = color.getBlue();
        index++;
      }
    }
    var data = new int[r.length + g.length + b.length];
    System.arraycopy(r, 0, data, 0, r.length);
    System.arraycopy(g, 0, data, r.length, g.length);
    System.arraycopy(b, 0, data, r.length + g.length, b.length);
    int total = data.length;
    float[] float32Data = new float[total];
    for (int i = 0; i < total; i++) {
      float32Data[i] = (float) (data[i] / 255.0);
    }
    return OnnxTensor.createTensor(env, FloatBuffer.wrap(float32Data),
        new long[]{1, 3, imageWidth, imageWidth});
  }

  int argmax(float[] floatData) {
    var index = 0;
    var max = Float.MIN_VALUE;
    for (int i = 0; i < floatData.length; i++) {
      if (floatData[i] > max) {
        max = floatData[i];
        index = i;
      }
    }
    return index;
  }
}
