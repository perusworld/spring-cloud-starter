# spring-cloud-starter
Spring Cloud Starter

## Development

### Startup Core Services Before Dev
```bash
mvn clean package -Dmaven.test.skip=true
cd docker
docker-compose -f core-services-docker-compose.yml build
docker-compose -f core-services-docker-compose.yml up -d
docker-compose -f service-routing-docker-compose.yml build
docker-compose -f service-routing-docker-compose.yml up -d
```
### Stop Core Services
```bash
cd docker
docker-compose -f service-routing-docker-compose.yml stop
docker-compose -f core-services-docker-compose.yml stop
```
### Test
```bash
mvn compile test
```
### Startup Backend Services During UI Dev
```bash
mvn package
cd docker
docker-compose -f services-docker-compose.yml build
docker-compose -f services-docker-compose.yml up -d
```
### Stop Backend Services During UI Dev
```bash
cd docker
docker-compose -f services-docker-compose.yml stop
```
### Startup Web
```bash
mvn package
cd docker
docker-compose -f web-docker-compose.yml build
docker-compose -f web-docker-compose.yml up -d
```
### Stop Web
```bash
cd docker
docker-compose -f web-docker-compose.yml stop
```

### Dev Instance URLs

| Service | URL | UID/PWD | Health | URL via Gateway |
| ------------- | ------------- | :-----: | --- | --- |
| Rabbit MQ | [http://localhost:15672/](http://localhost:15672/) | guest/guest | | |
| Postgres Admin | [http://localhost:5431](http://localhost:5431) | user@db.com/pwd | | |
| Mongo Admin | [http://localhost:27016](http://localhost:27016) | | | |
| Service Registry | [http://localhost:8761](http://localhost:8761) | | | |
| Service Gateway | [http://localhost:8080](http://localhost:8080) | | [http://localhost:9080/actuator/health](http://localhost:9080/actuator/health) | |
| Sample REST Service | [http://localhost:8081/](http://localhost:8081/) | | [http://localhost:9081/actuator/health](http://localhost:9081/actuator/health) | [http://localhost:8080/sample-rest-service/](http://localhost:8080/api/sample-rest-service/) |
| Sample Spring Web | [http://localhost:8082/](http://localhost:8082/) | | [http://localhost:9082/actuator/health](http://localhost:9082/actuator/health) | [http://localhost:8080/sample-spring-web/](http://localhost:8080/sample-spring-web/) |
