DATE=$(shell date +%Y%m%d)
JAX_VERSION=$(shell xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)
COMMIT_ID=$(shell git rev-parse --short HEAD)

.PHONY: clean
clean:
	rm -rf tmp

.PHONY: mvn-clean
mvn-clean:
	./mvnw -v && ./mvnw -P flink12 clean && ./mvnw clean

.PHONY: package
package: mvn-clean
	./mvnw package -DskipTests
	./mvnw -P flink12 package -DskipTests

.PHONY: font-end
font-end:
	cd jax-ui && rm -rf dist && yarn install && yarn build

.PHONY: unpack-jax
unpack-jax: package font-end
	mkdir -p tmp && rm -f tmp/jax-*.tar.gz && ls jax-web/target/jax-*.tar.gz | head -1 | xargs -I {} mv {} tmp
	rm -rf tmp/jax && tar -zxf tmp/jax-*.tar.gz -C tmp/ && rm -f tmp/jax-*.tar.gz && mkdir -p tmp/jax/jax/work && mkdir -p tmp/jax/jax/jar_dir && mkdir -p tmp/jax/jax/jar_lib && mkdir -p tmp/jax/jax/jar_tmp
	rm -rf tmp/jax/jax/jar_lib/ && mkdir -p tmp/jax/jax/jar_lib/flink && mkdir -p tmp/jax/jax/jar_lib/spark
	mv jax-flink-entry-1_9/target/jax-flink-entry-*.jar tmp/jax/jax/jar_lib/jax-flink-entry.jar
	cp jax-api/target/jax-api-*.jar tmp/jax/jax/jar_lib/flink/
	cp jax-api-flink-1_9/target/jax-api-*.jar tmp/jax/jax/jar_lib/flink/
	cp jax-core/target/jax-core-*.jar tmp/jax/jax/jar_lib/flink/
	cp jax-common/target/jax-common-*.jar tmp/jax/jax/jar_lib/flink/
	rm -rf tmp/jax/jax/tool/ && mkdir -p tmp/jax/jax/ && ls jax-tool/target/jax-tool-*.tar.gz | head -1 | xargs -I {} tar -zxf {} -C tmp/jax/jax/
	rm -rf tmp/jax/jax/www && mkdir -p tmp/jax/jax/ && cd jax-ui && mv dist ../tmp/jax/jax/www
	rm -rf tmp/jax/jax/jar_lib/flink-1_12 && mkdir -p tmp/jax/jax/jar_lib/flink-1_12
	mv jax-flink-entry-1_12/target/jax-flink-entry-*.jar tmp/jax/jax/jar_lib/jax-flink-1_12-entry.jar
	cp jax-api-flink-1_12/target/jax-api-*.jar tmp/jax/jax/jar_lib/flink-1_12/
	cp jax-api/target/jax-api-*.jar tmp/jax/jax/jar_lib/flink-1_12/
	cp jax-core/target/jax-core-*.jar tmp/jax/jax/jar_lib/flink-1_12/
	cp jax-common/target/jax-common-*.jar tmp/jax/jax/jar_lib/flink-1_12/

.PHONY: unpack-flink
unpack-flink:
	mkdir -p tmp && rm -f tmp/flink-1.9.1-bin-scala_2.11.tgz && wget -P tmp/ https://mirrors.huaweicloud.com/apache/flink/flink-1.9.1/flink-1.9.1-bin-scala_2.11.tgz
	rm -rf tmp/jax/flink-1.9.1 && tar -zxf tmp/flink-1.9.1-bin-scala_2.11.tgz -C tmp/jax && rm -f tmp/flink-1.9.1-bin-scala_2.11.tgz && rm -rf tmp/jax/flink && mv tmp/jax/flink-1.9.1 tmp/jax/flink
	mkdir -p tmp && rm -f tmp/flink-1.12.3-bin-scala_2.11.tgz && wget -P tmp/ https://mirrors.huaweicloud.com/apache/flink/flink-1.12.3/flink-1.12.3-bin-scala_2.11.tgz
	rm -rf tmp/jax/flink-1.12.3 && tar -zxf tmp/flink-1.12.3-bin-scala_2.11.tgz -C tmp/jax && rm -f tmp/flink-1.12.3-bin-scala_2.11.tgz

.PHONY: package-all
package-all: unpack-jax unpack-flink
	cd tmp && tar -zcf jax-all-${JAX_VERSION}-${DATE}-${COMMIT_ID}.tar.gz jax/

.PHONY: image
image: package-all
	docker build -t jax:${JAX_VERSION} .