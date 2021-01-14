package com.wallace.services;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_dnn.blobFromImage;
import static org.bytedeco.opencv.global.opencv_dnn.readNetFromCaffe;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import io.micronaut.context.annotation.Value;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.IntStream;

import javax.inject.Singleton;

@Singleton
public class FaceDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FaceDetectionService.class);

    private int modelImageSize;

    private Net net;

    private static final double THRESHOLD = .6;

    private Mat image;

    public FaceDetectionService(@Value("${model.image.size}") int modelImageSize) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream protoInputStream = classLoader.getResourceAsStream("deploy.prototxt.txt");
        InputStream caffeInputStream = classLoader.getResourceAsStream("res10_300x300_ssd_iter_140000.caffemodel");
        File tempProto = File.createTempFile("temp-proto", ".tmp");
        File tempCaffe = File.createTempFile("temp-caffe", ".tmp");
        assert protoInputStream != null;
        assert caffeInputStream != null;
        Files.copy(protoInputStream, tempProto.toPath(), REPLACE_EXISTING);
        Files.copy(caffeInputStream, tempCaffe.toPath(), REPLACE_EXISTING);

        net = readNetFromCaffe(tempProto.getPath(), tempCaffe.getPath());
        this.modelImageSize = modelImageSize;
        log.info("Loaded model successfully");
    }

    protected FaceDetectionService() {

    }

    public Long getNumberOfFacesInImage() {
        resize(image, image, new Size(modelImageSize, modelImageSize));
        Mat blob = blobFromImage(image, 1.0, new Size(modelImageSize, modelImageSize), new Scalar(104.0, 177.0, 123.0, 0), false, false, CV_32F);
        net.setInput(blob);
        Mat output = net.forward();
        Mat possibleFaces = new Mat(new Size(output.size(3), output.size(2)), CV_32F, output.ptr(0, 0));
        FloatIndexer srcIndexer = possibleFaces.createIndexer();

        Long numFaces = IntStream.range(0, output.size(3))
                                 .filter(index -> confidenceAboveThreshold(srcIndexer, index))
                                 .count();
        log.info("Detected {} faces in image", numFaces);

        return numFaces;
    }

    public void loadImage(Mat imageData) {
        image = imageData;
    }

    private boolean confidenceAboveThreshold(final FloatIndexer detections, final int index) {
        return detections.get(index, 2) > THRESHOLD;
    }
}
