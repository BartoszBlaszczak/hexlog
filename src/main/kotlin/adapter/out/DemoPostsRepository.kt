package adapter.out

import domain.Language
import domain.Language.EN
import domain.Language.PL
import domain.Post
import domain.PostId
import domain.PostsRepository
import java.time.LocalDate

object DemoPostsRepository: PostsRepository {
	private val posts = listOf(
		Post(PostId(1), "tytuł", "streszczenie", PL, LocalDate.now().atStartOfDay()),
		Post(PostId(2), "title", "shortcut", EN, LocalDate.now().atStartOfDay())
	)
	
	override fun findAll(language: Language): Array<Post> = posts.filter { it.language == language }.toTypedArray()
	override fun find(id: PostId): Post? = posts.find { it.id == id }
}
