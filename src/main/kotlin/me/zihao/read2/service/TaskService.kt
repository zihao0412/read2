package me.zihao.read2.service

import me.zihao.read2.dto.PageResponse
import me.zihao.read2.dto.TaskRequestDTO
import me.zihao.read2.dto.TaskResponseDTO
import me.zihao.read2.dto.toEntity
import me.zihao.read2.dto.toResponseDTO
import me.zihao.read2.repository.TaskRepository
import org.springframework.data.domain.PageRequest
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

    fun listPage(pageable: Pageable): PageResponse<TaskResponseDTO> {
        val sortOrder = Sort.by(
            Sort.Order.asc("status"),
            Sort.Order.desc("lastModifiedTS")
        )
        val pageableWithSort = PageRequest.of(
            pageable.pageNumber,
            pageable.pageSize,
            sortOrder
        )
        val page = taskRepository.findAll(pageableWithSort).map { it.toResponseDTO() }
        return PageResponse(
            content = page.content,
            pageNumber = page.number,
            pageSize = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            sort = sortOrder
        )
    }
}
