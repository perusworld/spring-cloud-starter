# spring-cloud-starter
Spring Cloud Starter

## Development

### Startup Core Services Before Dev
```bash
mvn clean package -Dmaven.test.skip=true
cd docker
docker-compose -f core-services-docker-compose.yml up -d
docker-compose -f eureka.yml up -d
```
### Stop Core Services
```bash
cd docker
docker-compose -f eureka.yml stop
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
docker-compose -f services-docker-compose up -d
```
### Stop Backend Services During UI Dev
```bash
cd docker
docker-compose -f services-docker-compose stop
```
### Dev Instance URLs
[Rabbit MQ](http://localhost:15672/) (uid/pwd - guest/guest)

[Postgres Admin](http://localhost:5431) (uid/pwd - user@db.com/pwd)

[Mongo Admin](http://localhost:27016)

[Service Registry](http://localhost:8761)

