package com.ug.pravda.exception

class AnnotationExistsException() : RuntimeException("User already submitted this annotation")

class AnnotationNotFoundException(id: Int) : RuntimeException("Annotation not found with ID : $id")