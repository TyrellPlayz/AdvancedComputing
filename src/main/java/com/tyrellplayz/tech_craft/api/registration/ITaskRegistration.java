package com.tyrellplayz.tech_craft.api.registration;

import com.tyrellplayz.tech_craft.api.task.Task;

public interface ITaskRegistration {

    void register(Class<? extends Task> taskClass);

}
