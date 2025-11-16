package me.zihao.read2.repository

import me.zihao.read2.model.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository : MongoRepository<Task, String>
