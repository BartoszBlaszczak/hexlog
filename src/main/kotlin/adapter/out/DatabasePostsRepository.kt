package adapter.out

import appContext
import domain.Language
import domain.Post
import domain.PostId
import domain.PostsRepository
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.time.LocalDateTime

object DatabasePostsRepository: PostsRepository {
	private val logger = LoggerFactory.getLogger(javaClass)
	private val connection by appContext.databaseConnection
	
	private val findPost = connection.prepareStatement("select * from post where post.id = ?;")
	private val findAllPosts = connection.prepareStatement("select * from post where post.language = ? order by post.create_date desc;")
	
	init {
		if (connection.isClosed) logger.error("Database connection is closed!")
		else logger.info("Database connection is open")
	}
	
	override fun findAll(language: Language): Array<Post> {
		val posts = mutableListOf<Post>()
		findAllPosts.setString(1, language.name)
		findAllPosts.executeQuery().use { while (it.next()) posts.add(toPost(it)) }
		return posts.toTypedArray()
	}
	
	override fun find(id: PostId): Post? {
		findPost.setLong(1, id.value)
		return findPost.executeQuery().use { resultSet -> resultSet.takeIf { it.next() }?.let(::toPost) }
	}
	
	private fun toPost(resultSet: ResultSet): Post = Post(
		id = PostId(resultSet.getLong("id")),
		createDate = LocalDateTime.parse(resultSet.getString("create_date")),
		updateDate = resultSet.getString("update_date")?.let(LocalDateTime::parse),
		title = resultSet.getString("title"),
		shortcut = resultSet.getString("shortcut"),
		language = Language.valueOf(resultSet.getString("language"))
	)
}