# Package

```sh
$ make package-all
```

# Docker

requirements

```
node/npm/yarn
wget
docker/docker compose
```

With flink 1.9 standalone cluster:

```sh
$ make image
$ docker-compose up
```

With flink 1.12 standalone cluster:

```sh
$ make image
$ FLINK_IMAGE=flink:1.12.3-scala_2.11 docker-compose up
```
