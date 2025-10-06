FROM openjdk:11-jre

# 环境参数
ENV PORT=8080
ENV JAVA_OPTS=""
ENV JAR_ARG=""
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

EXPOSE $PORT

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

VOLUME /tmp
ADD proj-liweida-service.jar  /app.jar
RUN bash -c 'touch /app.jar'

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.fuerda.egd=file:/dev/./urandom -Dlog4j2.formatMsgNoLookups=true -server -jar /app.jar $JAR_ARG"]