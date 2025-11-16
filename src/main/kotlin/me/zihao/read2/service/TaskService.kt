package me.zihao.read2.service

import me.zihao.read2.dto.TaskRequestDTO
import me.zihao.read2.dto.TaskResponseDTO
import me.zihao.read2.dto.toEntity
import me.zihao.read2.dto.toResponseDTO
import me.zihao.read2.model.Task
import me.zihao.read2.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {
    fun create(taskRequestDTO: TaskRequestDTO): TaskResponseDTO {
        val task = taskRequestDTO.toEntity()
        val savedTask = taskRepository.save(task)
        return savedTask.toResponseDTO()
    }

    fun listPage(pageable: Pageable): Page<TaskResponseDTO> {
        val sortOrder = Sort.by(
            Sort.Order.asc("status"),
            Sort.Order.desc("lastModifiedTS")
        )
        val pageableWithSort = org.springframework.data.domain.PageRequest.of(
            pageable.pageNumber,
            pageable.pageSize,
            sortOrder
        )
        return taskRepository.findAll(pageableWithSort).map { it.toResponseDTO() }
    }
}
