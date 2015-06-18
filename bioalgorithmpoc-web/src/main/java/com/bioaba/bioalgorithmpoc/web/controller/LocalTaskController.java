package com.bioaba.bioalgorithmpoc.web.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.bioaba.bioalgorithmpoc.core.events.LocalTaskSavedEvent;
import com.bioaba.bioalgorithmpoc.core.facade.LocalTaskFacade;
import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTask;
import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTaskParameter;

@ControllerAdvice
@RequestMapping("/tasks")
public class LocalTaskController {
	@Inject
	private ApplicationEventPublisher eventPublisher;

	private LocalTaskFacade facade;

	@Inject
	public LocalTaskController(LocalTaskFacade facade) {
		this.facade = facade;
	}

	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public void test(Exception ex) {
		System.out.println(ex.toString());
	}
	
	@RequestMapping(value = "/{taskKey}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Map<String, String> find(@RequestParam Long taskKey){
		String result = "";
		Map<String, String> map = new HashMap<String, String>();
		
		LocalTask entity = this.facade.find(taskKey);
		if(entity.getResult() == null){
			result = Base64.getEncoder().encodeToString(entity.getResult());
		}
		
		map.put("result", result);
		map.put("status", entity.getStatus());
		return map;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody String save(@RequestBody Map<String, Object> map) {		
		byte[] query = Base64Utils.decodeFromString((String)map.get("query"));
		String taskKey = (String) map.get("taskKey");
		String algorithmName = (String) map.get("algorithmName");
		Map<String, String> parameters = (Map<String, String>) map.get("parameters");
		String databaseName = (String) map.get("databaseName");
		String databaseURL = (String) map.get("databaseURL");
		String callbackURL = (String) map.get("callbackURL");

		LocalTask task = new LocalTask();
		task.setAlgorithmName(algorithmName);
		task.setCallbackURL(callbackURL);
		task.setDatabaseName(databaseName);
		task.setDatabaseURL(databaseURL);

		task.setParameters(new ArrayList<LocalTaskParameter>());
		for (Map.Entry<String, String> entry : parameters.entrySet())
		{
			LocalTaskParameter param = new LocalTaskParameter();
			param.setChave(entry.getKey());
			param.setValor(entry.getValue());
			task.getParameters().add(param);
		}
		
		task.setTaskKeyBioABA(taskKey);

		task = facade.saveTask(task, query);
		
		// Chamada de runTask
		eventPublisher.publishEvent(new LocalTaskSavedEvent(this, task
				.getId()));


		return "/tasks/" + task.getId();
	}
}
