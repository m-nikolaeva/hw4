import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderProductsTest extends AbstractTest {

    @Test
    void getOrderProductsSql_whenValid_shouldReturn() throws SQLException {
        String sql = "SELECT * FROM orders_products";

        Statement stm  = getConnection().createStatement();
        ResultSet res    = stm.executeQuery(sql);
        Assertions.assertNotNull(res);
        int countTableSize = 0;

        while (res.next()) {
            countTableSize++;
            System.out.println(res.getInt("order_id") +  "\t" +
                    res.getString("product_id") + "\t" +
                    res.getDouble("quantity"));
        }
        Assertions.assertEquals(23,countTableSize);
    }
}
