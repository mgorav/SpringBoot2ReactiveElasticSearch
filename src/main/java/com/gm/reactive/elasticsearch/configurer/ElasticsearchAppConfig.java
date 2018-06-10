package com.gm.reactive.elasticsearch.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gm.reactive.elasticsearch.controller.UserController;
import com.gm.reactive.elasticsearch.properties.ElasticsearchProperties;
import com.google.common.base.Predicates;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
@EnableSwagger2
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

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(UserController.class.getPackage().getName()))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .pathMapping("/")
                .apiInfo(apiInfo());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().validatorUrl("validatorUrl").build();
    }


    private ApiInfo apiInfo() {
        String description = "application Visualization Service";
        return new ApiInfoBuilder()
                .title("application")
                .description(description)
                .license("GM")
                .licenseUrl("http://architectcorner.blogspot.nl")
                .version("0.1")
                .build();
    }
}
