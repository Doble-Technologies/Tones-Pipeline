FROM gradle:jdk17-corretto AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM amazoncorretto:17.0.7-alpine

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/ /app/

COPY --from=build /home/gradle/src/build/resources/ /app/resources



EXPOSE 8443
EXPOSE 9090

ENTRYPOINT ["java","-jar","/app/tones-1.jar","-D", "exec.mainClass=\"tech.parkhurst.MainKt\""]
