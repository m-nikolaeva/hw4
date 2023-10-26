import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import ru.gb.CourierInfoEntity;

import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourierInfoTest extends AbstractTest {

    @Test
    @Order(1)
    void getCouriersInfoSql_whenValid_shouldReturn() throws SQLException {
        String sql = "SELECT * FROM courier_info";
        Statement stm = getConnection().createStatement();
        ResultSet res = stm.executeQuery(sql);
        Assertions.assertNotNull(res);
        int countTableSize = 0;
        while (res.next()) {
            countTableSize++;
            System.out.println(res.getInt("courier_id") +  "\t" +
                    res.getString("first_name") + "\t" +
                    res.getString("last_name") + "\t" +
                    res.getString("phone_number") + "\t" +
                    res.getString("delivery_type"));
        }
        Assertions.assertEquals(4,countTableSize);
    }

    @Test
    @Order(2)
    void addCourier_whenValid_shouldSave() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 5);
        entity.setFirstName("Петр");
        entity.setLastName("Петров");
        entity.setPhoneNumber("+7 999 345 3355");
        entity.setDeliveryType("bike");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierInfoEntity = (CourierInfoEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(courierInfoEntity);
        Assertions.assertEquals("bike", courierInfoEntity.getDeliveryType());
    }

    @Test
    @Order(3)
    void delCourier_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntity = (Optional<CourierInfoEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(courierInfoEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(courierInfoEntity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntityAfterDelete = (Optional<CourierInfoEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(courierInfoEntityAfterDelete.isPresent());
    }

    @Test
    @Order(4)
    void addCourier_whenNotValid_shouldThrow() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
    }
}

