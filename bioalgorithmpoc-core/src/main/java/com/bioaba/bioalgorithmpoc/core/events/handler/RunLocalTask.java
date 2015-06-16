package com.bioaba.bioalgorithmpoc.core.events.handler;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bioaba.bioalgorithmpoc.core.events.LocalTaskSavedEvent;
import com.bioaba.bioalgorithmpoc.core.facade.LocalTaskFacade;

@Component
public class RunLocalTask implements ApplicationListener<LocalTaskSavedEvent> {

	@Inject
	private LocalTaskFacade facade; 
	
	@Override
	public void onApplicationEvent(LocalTaskSavedEvent event) {
		facade.runTask(event.getTaskId());
	}
}
