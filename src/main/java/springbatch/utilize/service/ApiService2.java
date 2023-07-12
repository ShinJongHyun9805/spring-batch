package springbatch.utilize.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springbatch.utilize.batch.domain.dto.ApiInfo;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;
import springbatch.utilize.util.RestTemplateUtil;

/**
 * ResponseEntity 통신
 */


@Slf4j
@Service
public class ApiService2 extends AbstractApiServices {

    // Create an instance of RestTemplate
    RestTemplate restTemplate = new RestTemplate();

    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {

        // Create HttpHeaders with any desired headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer your_access_token");

        String requestBody = RestTemplateUtil.objectToJsonString(apiInfo);

        // Create the request entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send the HTTP GET request and receive the response as ResponseEntity
        String url = "http://localhost:8082/api/product/2";
        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            String responseBody = responseEntity.getBody();

            if (log.isDebugEnabled()) {
                log.debug("=====================================");
                log.debug("수신바디 확인 \r\n{}", responseBody);
                log.debug("=====================================");
            }
        } catch (Exception e) {
            throw new RuntimeException("예외 발생");
        }


        // Get the response status code
        int statusCode = responseEntity.getStatusCodeValue();

        // Process the response body as needed
        String responseBody = responseEntity.getBody();

        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCode).msg(responseEntity.getBody()).build();

        return apiResponseVO;
    }
}
