package com.example;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * This command deletes task without checks
 */
public class DeleteTaskWithoutCheckCommand implements Command<Object> {

    private final TaskEntity task;

    public DeleteTaskWithoutCheckCommand(TaskEntity task) {
        this.task = task;
    }


    public Object execute(CommandContext commandContext) {
        commandContext.getTaskEntityManager().deleteTask(this.task, "Janina", false);
        return null;
    }
}
