package com.bioaba.bioalgorithmpoc.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTask;

@Repository
public interface ILocalTaskRepository extends JpaRepository<LocalTask, Long> {

	public LocalTask findBytaskKeyBioABA(String taskId);
}
