# spring-cloud-starter
Spring Cloud Starter

## Development

### Startup Core Services Before Dev
```bash
cd docker
docker-compose -f core-services-docker-compose.yml up -d
```
### Stop Core Services
```bash
cd docker
docker-compose -f core-services-docker-compose.yml stop
```
### Dev Instance URLs
[Rabbit MQ](http://localhost:15672/) (uid/pwd - guest/guest)

[Postgres Admin](http://localhost:5431) (uid/pwd - user@db.com/pwd)

[Mongo Admin](http://localhost:27016)
