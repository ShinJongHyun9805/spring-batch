package springbatch.utilize.batch.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseVO {

    private int status;
    private String msg;
}
