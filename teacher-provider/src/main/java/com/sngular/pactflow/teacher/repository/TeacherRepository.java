package com.sngular.pactflow.teacher.repository;

import com.sngular.pactflow.teacher.model.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {
}
