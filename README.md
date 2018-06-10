# Non blocking/reactive APIs using Spring Boot 2.0.2 Reactive + Elastic Search


This project demonstrates:
1. Spring Boo 2.0.2 reactive problem model
2. Elastic search interaction along with async indexing


### Pre-requisite
Run Elastic search as Docker image shown below:
```bash
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.0.1
```

### Examples
1.  Creates/Updates User

```bash
curl -X PUT "http://localhost:8080/users" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"dateOfBirth\": \"15/06/1978\", \"email\": \"blah@gmail.co\", \"firstName\": \"Gaurav\", \"lastName\": \"Malhotra\", \"sex\": \"M\", \"telephoneNumber\": \"123\", \"username\": \"mgorav\"}"
```

2. Get User

```bash
   curl -X GET "http://localhost:8080/users/mgorav1" -H "accept: */*"
```

