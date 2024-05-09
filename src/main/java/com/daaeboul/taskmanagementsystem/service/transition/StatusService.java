package com.daaeboul.taskmanagementsystem.service.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.status.DuplicateStatusNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.status.StatusNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.repository.transition.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    /**
     * Creates a new status.
     *
     * @param status The status to create.
     * @return The created status.
     * @throws DuplicateStatusNameException If a status with the same name already exists.
     */
    @Transactional
    public Status createStatus(Status status) {
        if (statusRepository.existsByStatusName(status.getStatusName())) {
            throw new DuplicateStatusNameException("Status name already exists: " + status.getStatusName());
        }
        return statusRepository.save(status);
    }

    /**
     * Finds a status by its ID.
     *
     * @param id The ID of the status.
     * @return An Optional containing the status if found, otherwise empty.
     */
    public Optional<Status> findStatusById(Long id) {
        return statusRepository.findById(id);
    }

    /**
     * Finds a status by its name.
     *
     * @param statusName The name of the status.
     * @return An Optional containing the status if found, otherwise empty.
     */
    public Optional<Status> findStatusByName(String statusName) {
        return statusRepository.findByStatusName(statusName);
    }

    /**
     * Finds all statuses.
     *
     * @return A list of all statuses.
     */
    public List<Status> findAllStatuses() {
        return statusRepository.findAll();
    }

    /**
     * Finds all statuses with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of statuses.
     */
    public Page<Status> findAllStatuses(Pageable pageable) {
        return statusRepository.findAll(pageable);
    }

    /**
     * Finds statuses by name containing the given string (case-insensitive) with pagination and sorting.
     *
     * @param statusName The string to search for in status names.
     * @param pageable   Pagination and sorting parameters.
     * @return A Page of statuses matching the criteria.
     */
    public Page<Status> findStatusesByNameContaining(String statusName, Pageable pageable) {
        return statusRepository.findByStatusNameContainingIgnoreCase(statusName, pageable);
    }

    /**
     * Finds statuses by description containing the given string (case-insensitive).
     *
     * @param descriptionPart The string to search for in status descriptions.
     * @return A list of statuses matching the criteria.
     */
    public List<Status> findStatusesByDescriptionContaining(String descriptionPart) {
        return statusRepository.findByDescriptionContainingIgnoreCase(descriptionPart);
    }

    /**
     * Updates a status.
     *
     * @param updatedStatus The status with updated information.
     * @return The updated status.
     * @throws StatusNotFoundException If the status is not found.
     */
    @Transactional
    public Status updateStatus(Status updatedStatus) {
        Status existingStatus = statusRepository.findById(updatedStatus.getId())
                .orElseThrow(() -> new StatusNotFoundException("Status not found with ID: " + updatedStatus.getId()));

        // Update fields
        existingStatus.setStatusName(updatedStatus.getStatusName());
        existingStatus.setDescription(updatedStatus.getDescription());

        return statusRepository.save(existingStatus);
    }

    /**
     * Deletes a status by its ID.
     *
     * @param id The ID of the status to delete.
     * @throws StatusNotFoundException If the status is not found.
     */
    @Transactional
    public void deleteStatus(Long id) {
        if (!statusRepository.existsById(id)) {
            throw new StatusNotFoundException("Status not found with ID: " + id);
        }
        statusRepository.deleteById(id);
    }

    /**
     * Finds statuses created before the specified date and time.
     *
     * @param dateTime The date and time to search before.
     * @return A list of statuses created before the given date and time.
     */
    public List<Status> findStatusesCreatedBefore(LocalDateTime dateTime) {
        return statusRepository.findByCreatedAtBefore(dateTime);
    }
}