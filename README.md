## Micronaut Face Detection Service

Micronaut Java app allowing you to get the number of faces in an image via a REST API. Face detection is done using OpenCV with a pre-trained Caffe model.

**Endpoints:**\
_/upload_ - POST request with a JPEG image in the request body which will be saved in memory to be used for face detection. Making this request multiple times will replace the currently saved image.\
_/numFaces_ - GET request which will return the number of faces detected in the image. This request needs to be made after first uploading an image
\
\
\
**Credit**
- [OpenCV](https://opencv.org)
- https://github.com/bytedeco/javacv/blob/master/samples/DeepLearningFaceDetection.java

