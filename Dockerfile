FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# KST 시간 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

COPY bookmarker-*.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]