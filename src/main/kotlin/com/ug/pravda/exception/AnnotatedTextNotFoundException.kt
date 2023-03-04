package com.ug.pravda.exception

class AnnotatedTextNotFoundException(id: Int): RuntimeException("Could not find annotated text with ID: $id")
