package adapter.out

import AppProperties
import atShutdown
import domain.Language
import domain.Post
import domain.PostId
import domain.PostsRepository
import logger
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDateTime

object DatabasePostsRepository : PostsRepository {
	private val connection = DriverManager.getConnection(AppProperties.dbURL).atShutdown(Connection::close)
	private val findPostById by lazy { connection.prepareStatement("select * from post where post.id = ?;") }
	private val findAllPostsByLanguage
			by lazy { connection.prepareStatement("select * from post where post.language = ? order by post.create_date desc;") }
	
	init {
		if (connection.isClosed) logger.error("Database connection is closed!")
		else logger.info("Database connection is open")
		connection.prepareStatement(File("db.sql").readText()).execute()
	}
	
	override fun findAll(language: Language): Array<Post> {
		findAllPostsByLanguage.setString(1, language.name)
		return findAllPostsByLanguage.executeQuery().results(::toPost).toTypedArray()
	}
	
	override fun find(id: PostId): Post? {
		findPostById.setLong(1, id.value)
		return findPostById.executeQuery().results(::toPost).firstOrNull()
	}
	
	private fun <T> ResultSet.results(mapper: (ResultSet) -> T): List<T> {
		val results = mutableListOf<T>()
		use { while (it.next()) results.add(mapper(it)) }
		return results
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
