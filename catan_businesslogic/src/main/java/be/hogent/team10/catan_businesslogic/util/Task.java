package be.hogent.team10.catan_businesslogic.util;

/**
 *
 * @author HP
 */
public class Task {
    private PlayerTask task;
    private boolean blocking;

    public Task(PlayerTask task, boolean blocking) {
        this.task = task;
        this.blocking = blocking;
    }

    public PlayerTask getTask() {
        return task;
    }

    public void setTask(PlayerTask task) {
        this.task = task;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }   
}