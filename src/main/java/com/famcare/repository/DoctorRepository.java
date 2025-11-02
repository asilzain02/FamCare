package com.famcare.repository;

import com.famcare.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Doctor> doctorRowMapper = (rs, rowNum) -> {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setUsername(rs.getString("username"));
        doctor.setPassword(rs.getString("password"));
        doctor.setEmail(rs.getString("email"));
        doctor.setFullName(rs.getString("full_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setExperienceYears(rs.getInt("experience_years"));
        doctor.setPhoneNumber(rs.getString("phone_number"));
        doctor.setBio(rs.getString("bio"));
        doctor.setIsAvailable(rs.getBoolean("is_available"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            doctor.setCreatedAt(ts.toLocalDateTime());
        }
        
        return doctor;
    };

    public void save(Doctor doctor) {
        String sql = "INSERT INTO doctors (username, password, email, full_name, specialization, " +
                    "qualification, license_number, experience_years, phone_number, bio, is_available) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                doctor.getUsername(),
                doctor.getPassword(),
                doctor.getEmail(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getLicenseNumber(),
                doctor.getExperienceYears(),
                doctor.getPhoneNumber(),
                doctor.getBio(),
                doctor.getIsAvailable()
        );
    }

    public Optional<Doctor> findById(Integer id) {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try {
            Doctor doctor = jdbcTemplate.queryForObject(sql, doctorRowMapper, id);
            return Optional.of(doctor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Doctor> findByUsername(String username) {
        String sql = "SELECT * FROM doctors WHERE username = ?";
        try {
            Doctor doctor = jdbcTemplate.queryForObject(sql, doctorRowMapper, username);
            return Optional.of(doctor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Doctor> findAll() {
        String sql = "SELECT * FROM doctors ORDER BY full_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public List<Doctor> findAvailable() {
        String sql = "SELECT * FROM doctors WHERE is_available = true ORDER BY full_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public List<Doctor> findBySpecialization(String specialization) {
        String sql = "SELECT * FROM doctors WHERE specialization = ? AND is_available = true ORDER BY full_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper, specialization);
    }

    public void update(Doctor doctor) {
        String sql = "UPDATE doctors SET email = ?, full_name = ?, specialization = ?, " +
                    "qualification = ?, license_number = ?, experience_years = ?, phone_number = ?, " +
                    "bio = ?, is_available = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                doctor.getEmail(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getLicenseNumber(),
                doctor.getExperienceYears(),
                doctor.getPhoneNumber(),
                doctor.getBio(),
                doctor.getIsAvailable(),
                doctor.getId()
        );
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM doctors";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}