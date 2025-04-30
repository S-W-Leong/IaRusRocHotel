package com.hotelmgmt.models.housekeeping;

import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Staff;
import java.time.LocalDateTime;

public class HousekeepingTask {
    private String taskId;
    private Room room;
    private Staff assignedStaff;
    private LocalDateTime scheduledTime;
    private TaskStatus status;
    private String progressNotes;

    public HousekeepingTask(String taskId, Room room, Staff assignedStaff, LocalDateTime scheduledTime) {
        this.taskId = taskId;
        this.room = room;
        this.assignedStaff = assignedStaff;
        this.scheduledTime = scheduledTime;
        this.status = TaskStatus.PENDING;
        this.progressNotes = "";
    }

    public String getTaskId() { return taskId; }
    public Room getRoom() { return room; }
    public Staff getAssignedStaff() { return assignedStaff; }
    public void setAssignedStaff(Staff assignedStaff) { this.assignedStaff = assignedStaff; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getProgressNotes() { return progressNotes; }
    public void setProgressNotes(String progressNotes) { this.progressNotes = progressNotes; }
} 