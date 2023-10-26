import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrdersTest extends AbstractTest {

    @Test
    void getOrdersSql_whenValid_shouldReturn() throws SQLException {
        String sql = "SELECT * FROM orders";
        Statement stm  = getConnection().createStatement();
        ResultSet res    = stm.executeQuery(sql);
        Assertions.assertNotNull(res);
        int countTableSize = 0;
        while (res.next()) {
            countTableSize++;
            System.out.println(res.getInt("order_id") +  "\t" +
                    res.getString("customer_id") + "\t" +
                    res.getDouble("date_get"));
        }
        Assertions.assertEquals(15,countTableSize);
    }
}
