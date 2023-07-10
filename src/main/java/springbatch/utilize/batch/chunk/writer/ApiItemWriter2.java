package springbatch.utilize.batch.chunk.writer;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import springbatch.utilize.batch.domain.dto.ApiRequestVO;
import springbatch.utilize.batch.domain.dto.ApiResponseVO;
import springbatch.utilize.service.AbstractApiServices;

import java.util.List;

public class ApiItemWriter2 extends FlatFileItemWriter<ApiRequestVO> {

    private final AbstractApiServices apiServices;

    public ApiItemWriter2(AbstractApiServices apiServices) {
        this.apiServices = apiServices;
    }


    @Override
    public void write(List<? extends ApiRequestVO> items) throws Exception {
        ApiResponseVO responseVO = apiServices.service(items);
        System.out.println("service = " + responseVO);

        items.forEach(item -> item.setApiResponseVO(responseVO));

        super.setResource(new FileSystemResource("C:\\Users\\jakie\\OneDrive\\바탕 화면\\로드맵\\utilize\\utilize\\src\\main\\resources\\product2.txt"));
        super.open(new ExecutionContext());
        super.setLineAggregator(new DelimitedLineAggregator<>()); // 구분자
        super.setAppendAllowed(true); // 덮어쓰기
        super.write(items);
    }
}
