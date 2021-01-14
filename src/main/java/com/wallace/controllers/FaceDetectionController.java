package com.wallace.controllers;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import com.wallace.services.FaceDetectionService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

@Controller("/faces")
public class FaceDetectionController {

    @Inject
    FaceDetectionService faceDetectionService;

    @Get("/numFaces")
    public int numFaces() {
        return faceDetectionService.getNumberOfFacesInImage().intValue();
    }

    @Post("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> uploadImage(CompletedFileUpload fileUpload) {
        try {
            // Nasty work around - saving temp file and then reloading it using opencv imread
            // Should find a way to load it directly into opencv mat from byte array
            File file = File.createTempFile("output.png", "");
            Path path = Paths.get(file.getAbsolutePath());
            Files.write(path, fileUpload.getBytes());
            Mat image = imread(file.getAbsolutePath(), IMREAD_COLOR);
            faceDetectionService.loadImage(image);
            return HttpResponse.ok("Uploaded");
        } catch (IOException exception) {
            return HttpResponse.badRequest("Upload Failed");
        }
    }

}
