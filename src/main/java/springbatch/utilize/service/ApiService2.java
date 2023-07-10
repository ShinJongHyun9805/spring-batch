package springbatch.utilize.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springbatch.utilize.batch.domain.dto.ApiInfo;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;

@Service
public class ApiService2 extends AbstractApiServices {
    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8082/api/product/2", apiInfo, String.class);

        int statusCodeValue = response.getStatusCodeValue();
        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCodeValue).msg(response.getBody()).build();

        return apiResponseVO;
    }
}
