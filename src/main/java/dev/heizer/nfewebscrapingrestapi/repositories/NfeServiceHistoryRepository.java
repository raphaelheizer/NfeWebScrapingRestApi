package dev.heizer.nfewebscrapingrestapi.repositories;

import dev.heizer.nfewebscrapingrestapi.models.NfeServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface NfeServiceHistoryRepository extends JpaRepository<NfeServiceHistory, Long>
{
    List<NfeServiceHistory> findAllByAuthorizer_name(String name);

    NfeServiceHistory findTopByAuthorizer_Id(Long id);

    List<NfeServiceHistory> findAllByTimestampGreaterThanAndAuthorizer_Id(Timestamp timestamp,
                                                                          Long id);
}
