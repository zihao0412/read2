package me.zihao.read2.controller

import me.zihao.read2.dto.TaskRequestDTO
import me.zihao.read2.dto.TaskResponseDTO
import me.zihao.read2.service.TaskService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {
    @PostMapping
    fun create(@RequestBody taskRequestDTO: TaskRequestDTO): ResponseEntity<TaskResponseDTO> {
        val response = taskService.create(taskRequestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun listPage(pageable: Pageable): ResponseEntity<Page<TaskResponseDTO>> {
        val response = taskService.listPage(pageable)
        return ResponseEntity.ok(response)
    }
}
