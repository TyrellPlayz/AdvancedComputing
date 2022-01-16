package com.tyrellplayz.advancedtech.api.manager;

import com.tyrellplayz.advancedtech.api.task.Task;

public interface ITaskManger {

    void sendTask(Task task);

    Task getTask(String id);

    Task getTaskAndRemove(int i);

}
