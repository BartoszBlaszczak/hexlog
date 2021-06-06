package domain

import java.time.LocalDateTime

// Unfortunately thymeleaf do not works well with value classes...
//@JvmInline value class PostId(val value: Long)
data class PostId(val value: Long)

data class Post(
    val id: PostId? = null,
    val title: String,
    val shortcut: String,
    val language: Language,
    val createDate: LocalDateTime = LocalDateTime.now(),
    val updateDate: LocalDateTime? = null
)