package com.gm.reactive.elasticsearch.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.reactive.elasticsearch.properties.ElasticsearchProperties;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
public class ElasticsearchAppConfig {

    @Bean
    ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    RestHighLevelClient restHighLevelClient(ElasticsearchProperties props) {
        return new RestHighLevelClient(
                RestClient
                        .builder(props.hosts())
                        .setRequestConfigCallback(config -> config
                                .setConnectTimeout(props.getConnectTimeout())
                                .setConnectionRequestTimeout(props.getConnectionRequestTimeout())
                                .setSocketTimeout(props.getSocketTimeout())
                        )
                        .setMaxRetryTimeoutMillis(props.getMaxRetryTimeoutMillis()));
    }

}
