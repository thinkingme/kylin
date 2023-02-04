FROM openjdk:11

RUN mkdir -p /kylin

WORKDIR /kylin

ARG JAR_FILE=target/jd-qinglong-0.0.1.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENV TZ=Asia/Shanghai JAVA_OPTS="--add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true -Xms256m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

CMD java  $JAVA_OPTS -jar app.jar
