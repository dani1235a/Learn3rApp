package group7.tcss450.uw.edu.uilearner;

import java.util.List;

/**
 * Created by Myles on 12/1/2017.
 * This class represents a task
 */

public class Event {


    private String start;

    private String end;

    private String uuid;
    private String summary;
    private List<Task> tasks;
    private String name;
    private String title;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSummary() {
        return summary;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public static class Task {
        boolean done;

        String summary;

        public Task(boolean done, String summary) {
            this.done = done;
            this.summary = summary;
        }
        public boolean isDone() {
            return done;
        }

        public String getDescription() {
            return summary;
        }

    }

}
