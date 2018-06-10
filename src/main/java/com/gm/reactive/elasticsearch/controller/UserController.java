package com.gm.reactive.elasticsearch.controller;

import com.gm.reactive.elasticsearch.model.User;
import com.gm.reactive.elasticsearch.service.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Mono<ResponseEntity<User>> NOT_FOUND = Mono.just(ResponseEntity.notFound().build());

    private final ElasticSearchService elasticSearchService;

    @PutMapping
    public Mono<ResponseEntity<Map<String, Object>>> put(@RequestBody User user) {
        return elasticSearchService
                .index(user)
                .map(this::toMap)
                .map(m -> ResponseEntity.status(CREATED).body(m));
    }

    @GetMapping("/{userName}")
    public Mono<ResponseEntity<User>> get(@PathVariable("userName") String userName) {
        return elasticSearchService
                .searchByUserName(userName)
                .map(ResponseEntity::ok)
                .switchIfEmpty(NOT_FOUND);
    }


    private Map<String, Object> toMap(IndexResponse response) {
        Map<String, Object> index = new HashMap<>();
        index.put("id", response.getId());
        index.put("index", response.getIndex());
        index.put("type", response.getType());
        index.put("version", response.getVersion());
        index.put("result", response.getResult().getLowercase());
        index.put("seqNo", response.getSeqNo());
        index.put("primaryTerm", response.getPrimaryTerm());
        return index;
    }

}

