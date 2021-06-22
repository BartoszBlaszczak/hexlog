import domain.Post
import java.io.File
import kotlin.random.Random

object PostCreator {
	private val databaseConnection by TestContext.databaseConnection
	private val insertPost by lazy { databaseConnection.prepareStatement("insert into post values (?, ?, ?, ?, ?, ?);") }
	
	init { createTable() }
	
	private fun createTable() = databaseConnection.prepareStatement(File("db.sql").readText()).execute()
	
	fun insert(post: Post) {
		insertPost.setLong(1, post.id?.value ?: Random.nextLong())
		insertPost.setString(2, post.createDate.toString())
		insertPost.setString(3, post.updateDate?.toString())
		insertPost.setString(4, post.title)
		insertPost.setString(5, post.shortcut)
		insertPost.setString(6, post.language.name)
		insertPost.execute()
	}
}
