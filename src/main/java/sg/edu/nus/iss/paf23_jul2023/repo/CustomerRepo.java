package sg.edu.nus.iss.paf23_jul2023.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.paf23_jul2023.model.Customer;

@Repository
public class CustomerRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;

    // 1st function
    private final String findAllSql = "select id, first_name, last_name from customer";

    // 2nd function
    private final String findByIdSql = "select * from customer where id = ?";

    // 3rd --> u can do yourself (Slide 24)
    private final String findAllLimitOffsetSql = "select * from customer limit ? offset ?";

    private final String insertSql = "insert into customer values (null, ?, ?)";

    private final String updateSql = "update customer set first_name = ?, last_name = ? where id = ?";

    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<Customer>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(findAllSql);

        while (rs.next()) {
            Customer customer = new Customer();
            customer.setId(rs.getInt("id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));

            customerList.add(customer);
        }

        return Collections.unmodifiableList(customerList);
    }

    public List<Customer> getAllCustomersWithLimitOff(int limit, int offset) {
        List<Customer> customerList = new ArrayList<Customer>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(findAllLimitOffsetSql, limit, offset);

        while (rs.next()) {
            Customer customer = new Customer();
            customer.setId(rs.getInt("id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));

            customerList.add(customer);
        }

        return Collections.unmodifiableList(customerList);
    }

    public Customer getCustomerById(int id) {
        Customer customer = new Customer();

        customer = jdbcTemplate.queryForObject(findByIdSql, BeanPropertyRowMapper.newInstance(Customer.class), id);

        return customer;
    }

    public Integer createCustomer(Customer customer) {
        KeyHolder generatedKey = new GeneratedKeyHolder();

        PreparedStatementCreator psc = new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                PreparedStatement ps = con.prepareStatement(insertSql, new String[] { "id" });
                ps.setString(1, customer.getFirstName());
                ps.setString(2, customer.getLastName());
                return ps;
            }

        };

        jdbcTemplate.update(psc, generatedKey);

        Integer createdCustomerId = generatedKey.getKey().intValue();
        return createdCustomerId;
    }

    public Integer updateCustomer(Customer customer) {
        int iUpdated = 0;

        iUpdated = jdbcTemplate.update(updateSql, customer.getFirstName(), customer.getLastName(), customer.getId());

        return iUpdated;
    }
}
