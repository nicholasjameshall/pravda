package com.ug.pravda.exception

class UserNotFoundException(userId: Int) : RuntimeException("User not found: $userId")