package com.bioaba.bioalgorithmpoc.core.service;

import javax.inject.Inject;

import org.jvnet.jaxb2_commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTask;
import com.bioaba.bioalgorithmpoc.persistence.repository.ILocalTaskRepository;

@Service
public class LocalTaskService {

	private ILocalTaskRepository repository ;
	
	@Inject
	public LocalTaskService(ILocalTaskRepository repository){
		this.repository = repository;
	}
	
	public ILocalTaskRepository getRepository(){
		return this.repository;
	}

	public LocalTask find(Long id) {
		return repository.findOne(id);
	}

	public LocalTask save( LocalTask entity){
		if(entity.getId() == null && StringUtils.isEmpty(entity.getStatus()))
			entity.setStatus("RECEIVED");
		repository.save(entity);
		return entity;
	}
	
	public LocalTask findBytaskKeyBioABA(String taskId){
		return this.repository.findBytaskKeyBioABA(taskId);
	}
}
