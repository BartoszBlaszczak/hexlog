package adapter.out

import ContextTest
import PostCreator
import domain.Language.EN
import domain.Language.PL
import domain.Post
import domain.PostId
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class DatabasePostsRepositoryTest : ContextTest({
	this as DatabasePostsRepositoryTest
	
	test("find all posts") {
		// given
		val post1 = Post(title = "tytuł 1", shortcut = "streszczenie 1", language = PL).also(PostCreator::insert)
		val post2 = Post(title = "tytuł 2", shortcut = "streszczenie 2", language = PL).also(PostCreator::insert)
		val post3 = Post(title = "title 3", shortcut = "shortcut 3", language = EN).also(PostCreator::insert)
		
		// when
		val found = DatabasePostsRepository.findAll(PL)
		
		// then
		found.any { it basedOn post1 } shouldBe true
		found.any { it basedOn post2 } shouldBe true
		found.any { it basedOn post3 } shouldBe false
	}
	
	test("find by id") {
		// given
		val post = Post(PostId(Random.nextLong()), "tytuł", "streszczenie", PL).also(PostCreator::insert)
		
		// when
		val found = DatabasePostsRepository.find(post.id!!)
		
		// then
		found shouldBe post
	}
}) {
	private infix fun Post.basedOn(other: Post): Boolean =
		id != null &&
		updateDate == other.updateDate &&
		title == other.title &&
		shortcut == other.shortcut &&
		language == other.language
}
