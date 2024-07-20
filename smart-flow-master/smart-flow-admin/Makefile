VERSION=1.1.4
base:
	mvn -f ../pom.xml clean install
init:
	docker buildx create --use --name smartbuilder
	docker buildx inspect smartbuilder --bootstrap
local: base
	mvn clean package
	cp target/smart-flow-admin-*.jar  smart-flow-admin.jar
	docker build --no-cache -t smartboot/smart-flow-admin:${VERSION} -t smartboot/smart-flow-admin:latest .
	docker buildx build --no-cache -t smartboot/smart-flow-admin:${VERSION} -t smartboot/smart-flow-admin:latest . --platform=linux/amd64,linux/arm64 . --push
	rm -rf smart-flow-admin.jar

clear:
	docker buildx rm smartbuilder