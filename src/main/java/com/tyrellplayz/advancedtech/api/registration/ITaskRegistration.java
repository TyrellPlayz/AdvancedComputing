package com.tyrellplayz.advancedtech.api.registration;

import com.tyrellplayz.advancedtech.api.task.Task;

public interface ITaskRegistration {

    void register(Class<? extends Task> taskClass);

}
