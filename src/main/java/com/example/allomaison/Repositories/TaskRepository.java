package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCustomerId(Long customerId);
    long countByCityZipcodeInAndStartTimeBetweenAndStatusAndCatId(
            List<Integer> zipcodes,
            Timestamp startTimeFrom,
            Timestamp startTimeTo,
            Task.Status status,
            Integer catId
    );

    List<Task> findByCityZipcodeInAndStartTimeBetweenAndStatusAndCatId(
            List<Integer> zipcodes,
            Timestamp startTimeFrom,
            Timestamp startTimeTo,
            Task.Status status,
            Integer catId
    );


}
