package dev.heizer.nfewebscrapingrestapi.repositories;

import dev.heizer.nfewebscrapingrestapi.models.Authorizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizerRepository extends JpaRepository<Authorizer, Long>
{
    Authorizer findByName(String name);
}
