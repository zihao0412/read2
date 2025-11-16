package com.zihao.demo.repository

import com.zihao.demo.model.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository : MongoRepository<Task, String>
