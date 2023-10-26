import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gb.DeliveryEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeliveryTest extends AbstractTest {

    @Test
    void getDeliverySql_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM delivery";
        Statement stm = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet res = stm.executeQuery(sql);
        while (res.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(DeliveryEntity.class);
        //then
        Assertions.assertEquals(15, countTableSize);
        Assertions.assertEquals(15, query.list().size());
    }
}
