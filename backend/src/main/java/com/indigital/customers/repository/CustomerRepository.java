package com.indigital.customers.repository;

import com.indigital.customers.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByDni(String dni);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Customer> findByDni(String dni);

    Optional<Customer> findByEmailIgnoreCase(String email);

    Optional<Customer> findByDniAndEmailIgnoreCase(String dni, String email);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:dni IS NULL OR :dni = '' OR LOWER(c.dni) LIKE LOWER(CONCAT('%', :dni, '%'))) AND " +
           "(:email IS NULL OR :email = '' OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "ORDER BY c.fechaCreacion DESC, c.id DESC")
    List<Customer> searchByFilters(@Param("dni") String dni, @Param("email") String email);
}
