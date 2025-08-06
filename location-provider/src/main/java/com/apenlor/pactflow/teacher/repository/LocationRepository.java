package com.apenlor.pactflow.teacher.repository;

import com.apenlor.pactflow.teacher.entities.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {
}
