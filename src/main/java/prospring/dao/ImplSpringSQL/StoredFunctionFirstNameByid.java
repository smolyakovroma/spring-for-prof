package prospring.dao.ImplSpringSQL;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlFunction;

import javax.sql.DataSource;
import java.sql.Types;

public class StoredFunctionFirstNameByid extends SqlFunction<String> {

    private static final String SQL = "select getFirstNameByld(?)";

    public StoredFunctionFirstNameByid(DataSource dataSource) {
        super(dataSource, SQL);
        declareParameter(new SqlParameter(Types.INTEGER));
        compile();
    }
}