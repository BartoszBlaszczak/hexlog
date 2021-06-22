package adapter.out

import domain.Language.EN
import domain.Language.PL
import domain.Post
import domain.PostId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class DemoPostsRepositoryTest : FunSpec({
	this as DemoPostsRepositoryTest
	
	val date = LocalDate.now().atStartOfDay()
	val expectedPL = Post(PostId(1), "tytuł", "streszczenie", PL, date)
	val expectedEN = Post(PostId(2), "title", "shortcut", EN, date)
 
	test("find all posts") {
		DemoPostsRepository.findAll(PL) shouldContainExactly arrayOf(expectedPL)
		DemoPostsRepository.findAll(EN) shouldContainExactly arrayOf(expectedEN)
    }
	
	test("find by id") {
		DemoPostsRepository.find(PostId(1)) shouldBe expectedPL
		DemoPostsRepository.find(PostId(2)) shouldBe expectedEN
		DemoPostsRepository.find(PostId(3)) shouldBe null
	}
})
