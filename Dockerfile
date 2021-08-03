FROM openjdk:8-alpine

RUN echo "https://mirrors.aliyun.com/alpine/v3.8/main/" > /etc/apk/repositories

RUN apk update \
        && apk upgrade \
        && apk add --no-cache bash \
        && rm -rf /var/cache/apk/*

COPY tmp/jax /app

EXPOSE 9999

VOLUME /app/jax/jar_dir

CMD ["/app/start-docker.sh"]