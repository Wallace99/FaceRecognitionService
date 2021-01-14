FROM gradle:jdk15 as build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM amazoncorretto:15.0.1

EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/facedetection-all.jar /app/facedetection.jar
COPY docker/* docker/

RUN ["chmod", "+x", "docker/entrypoint.sh"]
CMD "docker/entrypoint.sh"