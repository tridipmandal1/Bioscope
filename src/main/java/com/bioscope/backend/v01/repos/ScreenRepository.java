package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.ScreenEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScreenRepository extends JpaRepository<ScreenEntity, UUID> {

    List<ScreenEntity> findScreenEntitiesByUserEntity(UserEntity userEntity);
}
