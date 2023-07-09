package springbatch.utilize.batch.classifier;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;
import springbatch.utilize.batch.domain.dto.ApiRequestVO;
import springbatch.utilize.batch.domain.dto.ProductVO;

import java.util.HashMap;
import java.util.Map;

public class ProcessorClassifier<T, T1> implements Classifier<T, T1> {

    private Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();

    public void setProcessorMap(Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap) {
        this.processorMap = processorMap;
    }

    @Override
    public T1 classify(T classifiable) {

        return (T1) processorMap.get(((ProductVO)classifiable).getType());
    }
}
