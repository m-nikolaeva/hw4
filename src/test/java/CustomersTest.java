import org.hibernate.Session;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.gb.CustomersEntity;

import org.hibernate.query.Query;

import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomersTest extends AbstractTest {

    @Test
    @Order(1)
    void getCustomers_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM customers";
        Statement stm = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet res = stm.executeQuery(sql);
        while (res.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(CustomersEntity.class);
        //then
        Assertions.assertEquals(15, countTableSize);
        Assertions.assertEquals(15, query.list().size());
    }

    @Test
    @Order(2)
    void addCustomer_whenValid_shouldSave() {
        //given
        CustomersEntity entity = new CustomersEntity();
        entity.setCustomerId((short) 17);
        entity.setApartment("305");
        entity.setDistrict("Восточный");
        entity.setFirstName("Иван");
        entity.setLastName("Иванов");
        entity.setHouse("187");
        entity.setPhoneNumber("+7 999 999 5555");
        entity.setStreet("Октябрьская");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM customers WHERE customer_id=" + 17).addEntity(CustomersEntity.class);
        CustomersEntity customersEntity = (CustomersEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(customersEntity);
        Assertions.assertEquals("Октябрьская", customersEntity.getStreet());
    }

    @Test
    @Order(3)
    void delCustomer_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM customers WHERE customer_id=" + 17).addEntity(CustomersEntity.class);
        Optional<CustomersEntity> customersEntity = (Optional<CustomersEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(customersEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(customersEntity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM customers WHERE customer_id=" + 17).addEntity(CustomersEntity.class);
        Optional<CustomersEntity> customersEntityAfterDelete = (Optional<CustomersEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(customersEntityAfterDelete.isPresent());
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({"Jennifer, Radriges", "Mario, Gordon", "Erica, Visputchu"})
    void getCustomerByLastName_whenValid_shouldReturn(String name, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM customers WHERE first_name='" + name + "'";
        Statement stm = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet res = stm.executeQuery(sql);
        while (res.next()) {
            nameString = res.getString(3);
        }
        //then
        Assertions.assertEquals(lastName, nameString);
    }

    @Test
    @Order(5)
    void addCustomer_whenNotValid_shouldThrow() {
        //given
        CustomersEntity entity = new CustomersEntity();
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
    }
}
