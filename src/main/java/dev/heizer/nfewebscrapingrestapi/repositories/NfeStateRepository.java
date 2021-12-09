package dev.heizer.nfewebscrapingrestapi.repositories;

import dev.heizer.nfewebscrapingrestapi.models.NfeState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NfeStateRepository extends JpaRepository<NfeState, Integer> {
    List<NfeState> findAllByName(String name);


    @Query(value = "SELECT DISTINCT ON(state_name) * FROM public.state order by state_name", nativeQuery = true)
    List<NfeState> findDistinctNames();
}
