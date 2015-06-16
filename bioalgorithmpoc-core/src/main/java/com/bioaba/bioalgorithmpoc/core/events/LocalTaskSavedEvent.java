package com.bioaba.bioalgorithmpoc.core.events;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class LocalTaskSavedEvent extends ApplicationEvent {

	private Long id;

	public LocalTaskSavedEvent(Object source, Long id) {
		super(source);

		this.id = id;
	}

	public Long getTaskId() {
		return this.id;
	}

}
