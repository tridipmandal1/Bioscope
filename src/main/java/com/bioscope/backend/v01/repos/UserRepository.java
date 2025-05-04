package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>{

    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findUserEntitiesByLocation(String location);

    List<UserEntity> findUserEntitiesByLocationAndRole(String location, Roles role);

    @Query(
            "SELECT u FROM UserEntity u WHERE " +
                    "u.role = :role AND (" +
                    "u.name LIKE CONCAT('%', :query, '%') " +
                    "OR " +
                    "u.location LIKE CONCAT('%', :query, '%'))"
    )
    List<UserEntity> searchHosts(@Param("query") String query, @Param("role") Roles role);
}
