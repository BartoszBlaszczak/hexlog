package adapter.out

import AppProperties
import domain.Language
import domain.Post
import domain.PostId
import domain.PostsRepository
import logger
import java.io.File
import java.lang.Runtime.getRuntime
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDateTime

object DatabasePostsRepository : PostsRepository {
	private val connection = DriverManager.getConnection(AppProperties.dbURL)
	
	private val findPost by lazy { connection.prepareStatement("select * from post where post.id = ?;") }
	private val findAllPosts by lazy {
		connection.prepareStatement("select * from post where post.language = ? order by post.create_date desc;")
	}
	
	init {
		if (connection.isClosed) logger.error("Database connection is closed!")
		else logger.info("Database connection is open")
		connection.prepareStatement(File("db.sql").readText()).execute()
		getRuntime().addShutdownHook(Thread(connection::close))
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
