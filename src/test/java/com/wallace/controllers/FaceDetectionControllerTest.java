package com.wallace.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wallace.services.FaceDetectionService;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

@MicronautTest
public class FaceDetectionControllerTest {

    @Inject
    @Client("/")
    RxHttpClient client;


    @Test
    public void testGetsNumberOfFaces() {
        HttpRequest<Integer> request = HttpRequest.GET("/faces/numFaces");
        String response = client.toBlocking().retrieve(request);

        assertNotNull(response);
        assertEquals("5", response);
    }


    @Replaces(FaceDetectionService.class)
    @Singleton
    public static class MockFaceDetectionService extends FaceDetectionService {

        public MockFaceDetectionService() {

        }


        @Override
        public Long getNumberOfFacesInImage() {
            return 5L;
        }
    }
}
