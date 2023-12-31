package springbatch.utilize.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import springbatch.utilize.batch.domain.dto.ApiInfo;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;
import springbatch.utilize.util.RestTemplateUtil;

import java.util.List;

/**
 * webclient non-blocking
 * */

@Service
public class ApiService3 extends AbstractApiServices {
    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer your_access_token");

        String requestBody = RestTemplateUtil.objectToJsonString(apiInfo);
        String url = "http://localhost:8083/api/product/3";

        WebClient webClient = WebClient.create();

        Mono<String> responseMono = webClient.mutate().build()
                .method(HttpMethod.POST)
                .uri(url)
                .headers(httpHeaders -> {
                    httpHeaders.addAll(headers);
                })
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("{\"statusCode\":\"200\"}");;

        // Subscribe to the Mono and process the response
        responseMono.subscribe(
                clientResponse  -> {


                },
                error -> {
                    // Handle any errors
                    System.err.println("Error: " + error.getMessage());
                }
        );


//        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8083/api/product/3", apiInfo, String.class);
//
//        int statusCodeValue = response.getStatusCodeValue();
//        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCodeValue).msg(response.getBody()).build();

        return null; //apiResponseVO;
    }
}