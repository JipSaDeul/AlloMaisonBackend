package com.example.allomaison.Services;

import com.example.allomaison.DTOs.TaskDTO;
import com.example.allomaison.DTOs.Requests.TaskRequest;
import com.example.allomaison.Entities.Task;
import com.example.allomaison.Mapper.TaskMapper;
import com.example.allomaison.Repositories.TaskRepository;
import com.example.allomaison.DTOs.CityDTO;
import com.example.allomaison.Utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CityService cityService;
    private static final double DEFAULT_MAX_DISTANCE = 20.0; // Default max distance in km
    private static final long DEFAULT_MAX_TASKS = 256; // Default max tasks to consider
    private static final long DEFAULT_MIN_TASKS = 20; // Minimum tasks to consider before reducing distance

    public List<TaskDTO> getNearbyTasks(Integer zipcode, Integer catId, Timestamp startTimeFrom, Timestamp startTimeTo) {
        double maxDistance = DEFAULT_MAX_DISTANCE;

        // Step 1: get nearby cities within the default max distance
        List<Pair<CityDTO, Double>> cities = cityService.getNearbyCities(zipcode, maxDistance);
        List<Integer> zipList = cities.stream().map(p -> p.getFirst().zipcode()).toList();

        // Step 2: if the number of tasks exceeds the default max tasks, reduce the distance
        long count = taskRepository.countByCityZipcodeInAndStartTimeBetweenAndStatusAndCatId(
                zipList, startTimeFrom, startTimeTo, Task.Status.PENDING, catId
        );

        if (count > DEFAULT_MAX_TASKS) {
            maxDistance = DEFAULT_MAX_DISTANCE / 2.0;
            cities = cityService.getNearbyCities(zipcode, maxDistance);
            zipList = cities.stream().map(p -> p.getFirst().zipcode()).toList();

            long reducedCount = taskRepository.countByCityZipcodeInAndStartTimeBetweenAndStatusAndCatId(
                    zipList, startTimeFrom, startTimeTo, Task.Status.PENDING, catId
            );
            // [[unlikely]] fallback: if reduced range yields too few tasks, slightly expand it
            if (reducedCount < DEFAULT_MIN_TASKS) {
                maxDistance = DEFAULT_MAX_DISTANCE * 0.75;
                cities = cityService.getNearbyCities(zipcode, maxDistance);
                zipList = cities.stream().map(p -> p.getFirst().zipcode()).toList();
            }
        }

        // Step 3: map city zipcodes to their distances
        Map<Integer, Double> zipcodeDistanceMap = cities.stream()
                .collect(Collectors.toMap(p -> p.getFirst().zipcode(), Pair::getSecond));

        // Step 4: query tasks with matching catId and status
        List<Task> tasks = taskRepository.findByCityZipcodeInAndStartTimeBetweenAndStatusAndCatId(
                zipList, startTimeFrom, startTimeTo, Task.Status.PENDING, catId
        );

        final double usedMaxDistance = maxDistance;

        // Step 5: sort tasks by budget * (1 - distance / maxDistance)
        return tasks.stream()
                .sorted(Comparator.comparingDouble(t -> {
                    double dist = zipcodeDistanceMap.getOrDefault(t.getCityZipcode(), usedMaxDistance);
                    return -t.getBudget() * (1.0 - dist / usedMaxDistance);
                }))
                .map(TaskMapper::toDTO)
                .toList();
    }

    @SuppressWarnings("unused")
    public Optional<TaskDTO> getTaskById(Long taskId) {
        return taskRepository.findById(taskId).map(TaskMapper::toDTO);
    }

    public Optional<TaskDTO> createTask(TaskRequest request) {
        try {
            Task task = new Task();
            task.setTaskId(UUIDUtil.uuidToLong());

            task.setCustomerId(request.getCustomerId());
            task.setTitle(request.getTitle());
            task.setCatId(request.getCatId());
            task.setFrequency(request.getFrequency());
            task.setCityZipcode(request.getCityZipcode());
            task.setStartTime(request.getStartTime());
            task.setEndTime(request.getEndTime());
            task.setAddress(request.getAddress());
            task.setBudget(request.getBudget());
            task.setCustomerContact(request.getCustomerContact());
            task.setDescription(request.getDescription());

            Task saved = taskRepository.saveAndFlush(task);
            return Optional.of(TaskMapper.toDTO(saved));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public boolean updateTaskStatus(Long taskId, Task.Status newStatus) {
        return taskRepository.findById(taskId).map(task -> {
            task.setStatus(newStatus);
            taskRepository.save(task);
            return true;
        }).orElse(false);
    }

    public List<TaskDTO> getTasksByCustomerId(Long customerId) {
        return taskRepository.findByCustomerId(customerId).stream()
                .map(TaskMapper::toDTO)
                .toList();
    }
}
