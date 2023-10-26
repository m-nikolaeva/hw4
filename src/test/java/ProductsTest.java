import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import ru.gb.ProductsEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsTest extends AbstractTest {

    @Test
    @Order(1)
    void getProducts_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM products";
        Statement stm = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet res = stm.executeQuery(sql);
        while (res.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(ProductsEntity.class);
        //then
        Assertions.assertEquals(10, countTableSize);
        Assertions.assertEquals(10, query.list().size());
    }

    @Test
    @Order(2)
    void getProductsSql_whenValid_shouldReturn() throws SQLException{
        String sql = "SELECT * FROM products";

        Statement stm = getConnection().createStatement();
        ResultSet res = stm.executeQuery(sql);
        Assertions.assertNotNull(res);
        int countTableSize = 0;

        while (res.next()) {
            countTableSize++;
            System.out.println(res.getInt("product_id") +  "\t" +
                    res.getString("menu_name") + "\t" +
                    res.getDouble("price"));
            }
            Assertions.assertEquals(10,countTableSize);
    }

    @Test
    @Order(3)
    void addProducts_whenValid_shouldSave() {
        //given
        ProductsEntity entity = new ProductsEntity();
        entity.setProductId((short) 11);
        entity.setMenuName("udon noodles");
        entity.setPrice("300");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 11).addEntity(ProductsEntity.class);
        ProductsEntity productsEntity = (ProductsEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(productsEntity);
        Assertions.assertEquals("udon noodles", productsEntity.getMenuName());
    }

    @Test
    @Order(4)
    void delProducts_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 11).addEntity(ProductsEntity.class);
        Optional<ProductsEntity> productsEntity = (Optional<ProductsEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(productsEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(productsEntity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 11).addEntity(ProductsEntity.class);
        Optional<ProductsEntity> productsEntityAfterDelete = (Optional<ProductsEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(productsEntityAfterDelete.isPresent());
    }
}
