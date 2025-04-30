package com.hotelmgmt.services;

import com.hotelmgmt.models.housekeeping.HousekeepingTask;
import com.hotelmgmt.models.housekeeping.TaskStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Staff;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HousekeepingService {
    private List<HousekeepingTask> tasks = new ArrayList<>();
    private List<Staff> housekeepingStaff = new ArrayList<>();

    public void addStaff(Staff staff) {
        housekeepingStaff.add(staff);
    }

    public List<Staff> getHousekeepingStaff() {
        return housekeepingStaff;
    }

    public HousekeepingTask assignTask(String taskId, Room room, Staff staff, LocalDateTime scheduledTime) {
        HousekeepingTask task = new HousekeepingTask(taskId, room, staff, scheduledTime);
        tasks.add(task);
        return task;
    }

    public List<HousekeepingTask> getTasks() {
        return tasks;
    }

    public List<HousekeepingTask> getTasksByStaff(Staff staff) {
        return tasks.stream().filter(t -> t.getAssignedStaff().equals(staff)).collect(Collectors.toList());
    }

    public List<HousekeepingTask> getTasksByRoom(Room room) {
        return tasks.stream().filter(t -> t.getRoom().equals(room)).collect(Collectors.toList());
    }

    public Optional<HousekeepingTask> getTaskById(String taskId) {
        return tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst();
    }

    public void updateTaskStatus(String taskId, TaskStatus status, String notes) {
        getTaskById(taskId).ifPresent(task -> {
            task.setStatus(status);
            task.setProgressNotes(notes);
            if (status == TaskStatus.COMPLETED) {
                task.getRoom().setNeedsCleaning(false);
            }
        });
    }

    public void markRoomNeedsCleaning(Room room) {
        room.setNeedsCleaning(true);
    }

    public void markRoomCleaned(Room room) {
        room.setNeedsCleaning(false);
    }
} 