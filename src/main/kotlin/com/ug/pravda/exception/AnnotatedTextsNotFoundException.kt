package com.ug.pravda.exception

class AnnotatedTextsNotFoundException(url: String) : RuntimeException("Could not find annotated texts on URL: $url")