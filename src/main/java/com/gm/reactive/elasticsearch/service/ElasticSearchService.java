package com.gm.reactive.elasticsearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.reactive.elasticsearch.model.User;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Component
@Slf4j
@RequiredArgsConstructor
public class ElasticSearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;


    public Mono<User> searchByUserName(String userName) {
        return Mono
                .<GetResponse>create(sink ->
                        restHighLevelClient.getAsync(new GetRequest("user", "user", userName), listenerToTheSink(sink))
                )
                .filter(GetResponse::isExists)
                .map(GetResponse::getSource)
                .map(map -> objectMapper.convertValue(map, User.class));
    }

    public Mono<IndexResponse> index(User doc) {
        return indexDocument(doc)
                .doOnError(e -> log.error("Unable to index {}", doc, e));
    }

    // Utility methods




    private Mono<IndexResponse> indexDocument(User userDocument) {
        return Mono.create(aSink -> {
            try {
                doCreateIndex(userDocument, listenerToTheSink(aSink));
            } catch (JsonProcessingException e) {
                aSink.error(e);
            }
        });
    }

    private void doCreateIndex(User doc, ActionListener<IndexResponse> listener) throws JsonProcessingException {
        final IndexRequest indexRequest = new IndexRequest("user", "user", doc.getUsername());
        final String json = objectMapper.writeValueAsString(doc);
        indexRequest.source(json, XContentType.JSON);
        restHighLevelClient.indexAsync(indexRequest, listener);
    }

    private <T> ActionListener<T> listenerToTheSink(MonoSink<T> sink) {
        return new ActionListener<T>() {
            @Override
            public void onResponse(T response) {
                sink.success(response);
            }

            @Override
            public void onFailure(Exception e) {
                sink.error(e);
            }
        };
    }

}
