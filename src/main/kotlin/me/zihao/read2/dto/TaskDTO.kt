package me.zihao.read2.dto

import me.zihao.read2.model.Task
import me.zihao.read2.model.TaskStatus
import java.time.Instant

/**
 * Data Transfer Object for creating or updating a Task
 */
data class TaskRequestDTO(
    val url: String,
    val pictureUrl: String,
    val titles: List<String>,
    val artists: List<String>,
    val status: TaskStatus? = null
)

/**
 * Data Transfer Object for responding with Task information
 */
data class TaskResponseDTO(
    val id: String?,
    val url: String,
    val pictureUrl: String,
    val titles: List<String>,
    val artists: List<String>,
    val status: TaskStatus,
    val createdTS: Instant,
    val lastModifiedTS: Instant
)

/**
 * Extension function to convert Task to TaskResponseDTO
 */
fun Task.toResponseDTO(): TaskResponseDTO =
    TaskResponseDTO(
        id = this.id,
        url = this.url,
        pictureUrl = this.pictureUrl,
        titles = this.titles,
        artists = this.artists,
        status = this.status,
        createdTS = this.createdTS,
        lastModifiedTS = this.lastModifiedTS
    )

/**
 * Extension function to convert TaskRequestDTO to Task
 */
fun TaskRequestDTO.toEntity(id: String? = null): Task =
    Task(
        id = id,
        url = this.url,
        pictureUrl = this.pictureUrl,
        titles = this.titles,
        artists = this.artists,
        status = this.status ?: TaskStatus.PS1_MARKED
    )
