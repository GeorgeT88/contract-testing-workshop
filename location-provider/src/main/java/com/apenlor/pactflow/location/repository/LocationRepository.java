package com.apenlor.pactflow.location.repository;

import com.apenlor.pactflow.location.entities.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {
}
