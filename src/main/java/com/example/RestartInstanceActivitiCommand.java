package com.example;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class RestartInstanceActivitiCommand implements Command<Void> {

	private final String executionId;
	private final String activityId;
	
	public RestartInstanceActivitiCommand(String executionId,String activityId) {
		this.executionId = executionId;
		this.activityId = activityId;
	}
	
	@Override
	public Void execute(CommandContext commandContext) {
		System.out.println("start executed");
		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(this.executionId);
		execution.setActivity(new ActivityImpl(this.activityId, execution.getProcessDefinition()));
		return null;
	}

}
