package com.tyrellplayz.tech_craft.api.manager;

import com.tyrellplayz.tech_craft.api.task.Task;

public interface ITaskManger {

    void sendTask(Task task);

    Task getTask(String id);

    Task getTaskAndRemove(int i);

}
