//ai-service/src/main/java/dev/sammy_ulfh/ai/model/SprintData.java
package dev.sammy_ulfh.ai.model;

import java.util.List;

public class SprintData {
    private List<Task> activeTasks;
    private List<User> users;

    // Constructors, getters, setters
    public SprintData() {}

    public SprintData(List<Task> activeTasks, List<User> users) {
        this.activeTasks = activeTasks;
        this.users = users;
    }

    public List<Task> getActiveTasks() { return activeTasks; }
    public void setActiveTasks(List<Task> activeTasks) { this.activeTasks = activeTasks; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}