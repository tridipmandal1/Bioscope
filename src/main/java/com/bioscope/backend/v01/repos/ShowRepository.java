package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.ShowEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, UUID> {



    List<ShowEntity> findShowEntitiesByLocation(String location);

    @Query("SELECT s FROM ShowEntity s WHERE s.movie IS NULL AND (" +
            " s.location LIKE CONCAT('%', :location, '%') AND (" +
            " s.showName LIKE CONCAT('%', :query, '%') OR " +
            " s.showDescription LIKE CONCAT('%', :query, '%') OR " +
            " s.showType LIKE CONCAT('%', :query, '%') OR " +
            " s.location LIKE CONCAT('%', :query, '%')))")
    List<ShowEntity> findShowEntitiesByQuery(@Param("query") String query,
                                             @Param("location") String location);


    @Query("SELECT s FROM ShowEntity s WHERE s.location LIKE CONCAT('%',:location,'%') ORDER BY s.bookings DESC")
    List<ShowEntity> findTrendingShowsByLocation(@Param("location") String location);


    List<ShowEntity> findShowEntitiesByUser(UserEntity user);
}
