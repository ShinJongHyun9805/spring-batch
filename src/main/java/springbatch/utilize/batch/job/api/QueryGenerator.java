package springbatch.utilize.batch.job.api;

import org.springframework.jdbc.core.JdbcTemplate;
import springbatch.utilize.batch.domain.dto.ProductVO;
import springbatch.utilize.batch.rowmapper.ProductRowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGenerator { // 타입별로 Thread 생성

    public static ProductVO[] getProductList(DataSource dataSource){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<ProductVO> productList = jdbcTemplate.query("SELECT type FROM product GROUP BY type", new ProductRowMapper() {
            @Override
            public ProductVO mapRow(ResultSet rs, int rowNums) throws SQLException {
                return ProductVO.builder().type(rs.getString("type")).build();
            }
        });

        return productList.toArray(new ProductVO[]{});
    }

    public static Map<String, Object> getParameterForQuery(String parameter, String value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter, value);

        return parameters;
    }
}
