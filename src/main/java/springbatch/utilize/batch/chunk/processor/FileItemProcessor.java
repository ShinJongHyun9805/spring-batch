package springbatch.utilize.batch.chunk.processor;

import org.springframework.batch.item.ItemProcessor;
import springbatch.utilize.batch.domain.dto.ProductVO;
import springbatch.utilize.batch.domain.entity.Product;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {
    @Override
    public Product process(ProductVO item) throws Exception {
        Product product = Product.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .type(item.getType())
                .build();

        return product;
    }
}
