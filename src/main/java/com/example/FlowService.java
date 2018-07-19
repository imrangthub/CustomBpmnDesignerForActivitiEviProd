package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private RuntimeService runtimeService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public boolean startProcess() {
		Map<String, Object> variables = new HashMap<String, Object>();
		ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcessDinamic");

		List<Task> tasks = taskService.createTaskQuery().list();
		while (!tasks.isEmpty()) {
			if (!tasks.isEmpty()) {
				String id = tasks.get(0).getId();
				String name = tasks.get(0).getName();
				log.debug("Task id : " + id);
				taskService.complete(id);
				log.debug("task " + name + " is complete");
				tasks = taskService.createTaskQuery().list();
			} else{
				log.debug("task is not created");
				break;
			}
		}
		log.debug(" All task complete");
		return true;
	}

}
