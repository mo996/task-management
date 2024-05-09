package com.daaeboul.taskmanagementsystem;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void shouldConnectToDatabase() {
        assertThat(dataSource).isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(1)).isTrue(); // Timeout of 1 second
        } catch (SQLException e) {
            fail("Could not obtain a valid database connection", e);
        }
    }
}

