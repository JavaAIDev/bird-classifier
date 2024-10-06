# Bird Classifier

A bird classifier powered by [Onnx runtime](https://onnxruntime.ai/) for Java.
It
uses [Bird Classifier EfficientNet-B2](https://huggingface.co/dennisjooo/Birds-Classifier-EfficientNetB2)
model for bird classification.

To use this bird classifier, download the latest release jar file `bird-classifier.jar
` and run it with url of an image.

```sh
java -jar bird-classifier.jar https://images.unsplash.com/photo-1470619549108-b85c56fe5be8
```

The output is `american flamingo`.