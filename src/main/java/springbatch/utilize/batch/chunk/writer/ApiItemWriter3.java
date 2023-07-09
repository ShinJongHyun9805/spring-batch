package springbatch.utilize.batch.chunk.writer;

import org.springframework.batch.item.ItemWriter;
import springbatch.utilize.batch.domain.dto.ApiRequestVO;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;
import springbatch.utilize.service.AbstractApiServices;

import java.util.List;

public class ApiItemWriter3 implements ItemWriter<ApiRequestVO> {

    private final AbstractApiServices apiServices;

    public ApiItemWriter3(AbstractApiServices apiServices) {
        this.apiServices = apiServices;
    }

    @Override
    public void write(List<? extends ApiRequestVO> items) throws Exception {
        ApiResponseVO responseVO = apiServices.service(items);
        System.out.println("service = " + responseVO);
    }
}
