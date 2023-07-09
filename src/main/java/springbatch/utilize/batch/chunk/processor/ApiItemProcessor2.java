package springbatch.utilize.batch.chunk.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import springbatch.utilize.batch.domain.dto.ApiRequestVO;
import springbatch.utilize.batch.domain.dto.ProductVO;

@Component
public class ApiItemProcessor2 implements ItemProcessor<ProductVO, ApiRequestVO> {
    @Override
    public ApiRequestVO process(ProductVO productVO) throws Exception {

        return ApiRequestVO.builder()
                .id(productVO.getId())
                .productVO(productVO)
                .build();
    }
}
