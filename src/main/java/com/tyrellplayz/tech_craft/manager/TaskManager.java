package com.tyrellplayz.tech_craft.manager;

import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.manager.ITaskManger;
import com.tyrellplayz.tech_craft.api.task.Task;
import com.tyrellplayz.tech_craft.network.play.CRequestMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MrCrayfish
 */
public class TaskManager implements ITaskManger {

    public static final Logger LOGGER = LogManager.getLogger("TaskManager");
    private static TaskManager instance = null;
    private Map<String, Task> registeredRequests = new HashMap<>();
    private Map<Integer, Task> requests = new HashMap<>();
    private int currentId = 0;

    public static TaskManager get() {
        if (instance == null) {
            instance = new TaskManager();
        }

        return instance;
    }

    public void registerTask(Class<? extends Task> clazz) {
        try {
            Constructor<? extends Task> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Task task = constructor.newInstance();
            LOGGER.info("Registering task '" + task.getName() + "'");
            this.registeredRequests.put(task.getName(), task);
        } catch (InstantiationException var4) {
            System.err.println("- Missing constructor '" + clazz.getSimpleName() + "()'");
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public void sendTask(Task task) {
        if (!this.registeredRequests.containsKey(task.getName())) {
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#requestRequest to register your task.");
        } else {
            int requestId = this.currentId++;
            this.requests.put(requestId, task);
            TechCraft.NETWORK.sendToServer(new CRequestMessage(requestId, task));
        }
    }

    public Task getTask(String name) {
        return this.registeredRequests.get(name);
    }

    public Task getTaskAndRemove(int id) {
        return this.requests.remove(id);
    }

}
