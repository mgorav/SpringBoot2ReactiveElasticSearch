# Spring Boot 2.0.2 Reactive + Elastic Search
This project demonstrates:
1. Spring Boo 2.0.2 reactive problem model
2. Elastic search interaction along with async indexing


### Pre-requisite
Run Elastic search as Docker image shown below:
```bash
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.0.1
```


