package springbatch.utilize.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springbatch.utilize.batch.domain.dto.ApiInfo;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;

@Service
public class ApiService3 extends AbstractApiServices {
    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo.ApiInfoBuilder apiInfo) {

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8083/api/product/3", apiInfo, String.class);

        int statusCodeValue = response.getStatusCodeValue();
        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCodeValue).msg(response.getBody()).build();

        return apiResponseVO;
    }
}