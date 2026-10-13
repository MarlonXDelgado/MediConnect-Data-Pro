package com.dev.mxd.mediconnect.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class InitialSchemaMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAllRelationalTables() {
        List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class);

        assertThat(tableNames).contains("DOCTORS", "PATIENTS", "APPOINTMENTS", "MEDICAL_OFFICES");
    }

    @Test
    void shouldRejectDuplicateProfessionalLicenseNumbers() {
        insertDoctor("LIC-100");

        assertThatThrownBy(() -> insertDoctor("LIC-100"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private void insertDoctor(String professionalLicenseNumber) {
        jdbcTemplate.update("""
                INSERT INTO doctors (first_name, last_name, specialty, professional_license_number,
                                     email, phone, available)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, "Ana", "Perez", "Cardiologia", professionalLicenseNumber,
                "ana@example.com", "3001234567", true);
    }
}