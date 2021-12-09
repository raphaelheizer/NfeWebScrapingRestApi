package dev.heizer.nfewebscrapingrestapi.repositories;

import dev.heizer.nfewebscrapingrestapi.models.ServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long>
{

}
