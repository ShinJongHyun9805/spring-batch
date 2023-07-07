package springbatch.utilize.batch.job.file;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import springbatch.utilize.batch.chunk.processor.FileItemProcessor;
import springbatch.utilize.batch.domain.dto.ProductVO;
import springbatch.utilize.batch.domain.entity.Product;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class FileJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;


    @Bean
    public Job fileJob(){
        return jobBuilderFactory.get("fileJob")
                .start(fileStep())
                //.incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step fileStep() {
        return stepBuilderFactory.get("fileStep")
                .<ProductVO, Product> chunk(10) // 청크 사이즈
                .reader(fileItemReader(null))
                .processor(fileItemProcessor())
                .writer(fileItemWriter())
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemReader<ProductVO> fileItemReader(@Value("#{jobParameters['requestDate']}") String requestDate) { // JobParameter에 인자를 전달 하여 @Value로 받음.
        System.out.println("requestDate = " + requestDate);
        return new FlatFileItemReaderBuilder<ProductVO>()
                .name("flatFile")
                .resource(new ClassPathResource("product_" + requestDate +".csv")) // 읽어올 파일 위치
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())  // 읽어온 파일 값 VO 매핑
                .targetType(ProductVO.class) // 읽어온 파일 값 VO 매핑
                .linesToSkip(1) // 첫째 행 스킵(보통 헤더 위치)
                .delimited().delimiter(",")
                .names("id", "name", "price", "type")
                .build();
    }

    @Bean
    public ItemProcessor<ProductVO, Product> fileItemProcessor() {
        return new FileItemProcessor();
    }

    @Bean
    public ItemWriter<Product> fileItemWriter(){
        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(dataSource)
                .sql("INSERT INTO product (id, name, price, type) VALUES (:id, :name, :price, :type)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .beanMapped()
                .build();
    }
}
