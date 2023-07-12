package springbatch.utilize.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import springbatch.utilize.batch.domain.dto.ApiInfo;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;
import springbatch.utilize.util.RestTemplateUtil;

import java.util.concurrent.TimeoutException;

/**
 * HttpEntity 통신
 */


@Slf4j
@Service
public class ApiService1 extends AbstractApiServices {

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

        // Send the HTTP POST request with the entity
        String url = "http://localhost:8081/api/product/1";
        HttpEntity<String> httpEntity = null;

        try {
            httpEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            String responseBody = httpEntity.getBody();

            if (log.isDebugEnabled()) {
                log.debug("=====================================");
                log.debug("수신바디 확인 \r\n{}", responseBody);
                log.debug("=====================================");
            }
        } catch (Exception e) {
            throw new RuntimeException("예외 발생");
        }

        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(200).msg(httpEntity.getBody()).build();

        return apiResponseVO;
    }
}
