package dev.heizer.nfewebscrapingrestapi.repositories;

import dev.heizer.nfewebscrapingrestapi.models.NfeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<NfeService, Long>
{
    NfeService findByName(String name);
}
