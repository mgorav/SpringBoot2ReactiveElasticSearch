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

    private final Timer esIndexTimer = Metrics.timer("es.timer");
    private final LongAdder esConcurrent = Metrics.gauge("es.esConcurrent", new LongAdder());
    private final Counter esSuccesses = Metrics.counter("es.index", "result", "success");
    private final Counter esFailures = Metrics.counter("es.index", "result", "failure");

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
                .compose(this::countSuccessAndFailure)
                .compose(this::countEsConcurrent)
                .compose(this::measureEsTime)
                .doOnError(e -> log.error("Unable to index {}", doc, e));
    }

    // Utility methods
    private Mono<IndexResponse> countEsConcurrent(Mono<IndexResponse> monoIndexResponse) {
        return monoIndexResponse
                .doOnSubscribe(s -> esConcurrent.increment())
                .doOnTerminate(esConcurrent::decrement);
    }

    private Mono<IndexResponse> measureEsTime(Mono<IndexResponse> monoIndexResponse) {
        return Mono
                .fromCallable(System::currentTimeMillis)
                .flatMap(time ->
                        monoIndexResponse.doOnSuccess(response ->
                                esIndexTimer.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS))
                );
    }

    private Mono<IndexResponse> countSuccessAndFailure(Mono<IndexResponse> mono) {
        return mono
                .doOnError(e -> esFailures.increment())
                .doOnSuccess(response -> esSuccesses.increment());
    }

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
