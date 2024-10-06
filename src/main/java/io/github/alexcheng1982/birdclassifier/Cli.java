package io.github.alexcheng1982.birdclassifier;

import java.net.URL;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(
    name = "bird-classifier",
    mixinStandardHelpOptions = true,
    version = "0.2.0",
    description = "Classify birds"
)
public class Cli implements Callable<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger("Cli");

  @Parameters(index = "0")
  URL imageUrl;

  @Option(
      names = {"-w", "--width"},
      defaultValue = "260",
      description = "Model image width"
  )
  int imageWidth = 260;

  @Option(
      names = {"-h", "--height"},
      defaultValue = "260",
      description = "Model image height"
  )
  int imageHeight = 260;

  @Override
  public String call() throws Exception {
    return new Classifier(imageWidth, imageHeight).classify(imageUrl)
        .toLowerCase();
  }

  public static void main(String[] args) {
    try {
      var cmd = new CommandLine(new Cli());
      int exitCode = cmd.execute(args);
      String result = cmd.getExecutionResult();
      System.out.printf("%nClassified as : %s%n", result);
      System.exit(exitCode);
    } catch (Exception e) {
      LOGGER.error("Internal error: {}", e.getMessage());
    }
  }
}
