package com.ug.pravda.dao

import com.ug.pravda.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Int>