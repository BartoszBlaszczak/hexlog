package domain

import java.time.LocalDateTime

@JvmInline value class PostId(val value: Long)

data class Post(
    val id: PostId,
    val title: String,
    val shortcut: String,
    val language: Language,
    val createDate: LocalDateTime = LocalDateTime.now(),
    val updateDate: LocalDateTime? = null
)
