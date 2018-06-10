# Non blocking/reactive APIs using Spring Boot 2.0.2 Reactive + Reactive Elastic Search

The beautiful world of non-blocking APIs using Spring Boot 2.0.2 & Elastic Search demonstrated in my project "SpringBoot2ReactiveElasticSearch".  This application with start with "Netty" in place of traditional "Tomcat": "Netty started on port(s): 8080"

This project demonstrates:
1. Building reactive APIs (PUB-SUB model) like search, put ..
2. Reactive integration with Elastic Search 
3. Spring Boot direct integration with Elastic Search without Spring Data to unearth the power of Elastic Search
4. Elastic search core API based indexing
5. Lazy APIs — meaning computation will start only after subscribing
6. Exception(s) is/are first little citizen (ActionListener<IndexResponse>)
7. Source-Sink programing model
8...

NOTE: The Elastic Search interaction is made reactive using elasticsearch-rest-high-level-client

### Pre-requisite
Run Elastic search as Docker image shown below:
```bash
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.0.1
```

### Examples
1.  Create/Update User

```bash
curl -X PUT "http://localhost:8080/users" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"dateOfBirth\": \"15/06/1978\", \"email\": \"blah@gmail.co\", \"firstName\": \"Gaurav\", \"lastName\": \"Malhotra\", \"sex\": \"M\", \"telephoneNumber\": \"123\", \"username\": \"mgorav\"}"
```

2. Get User

```bash
   curl -X GET "http://localhost:8080/users/mgorav1" -H "accept: */*"
```

