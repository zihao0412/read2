package me.zihao.read2.model

@Document(collection = "Task")
data class Task(
    @Id val id: String?,
    val url: String,
    val pictureUrl: String,
    val titles: List<String>,
    val artists: List<String>,
    val status: TaskStatus,
    val createdTS: Instant = Instant.now(),
    val lastModifiedTS: Instant = Instant.now()
)