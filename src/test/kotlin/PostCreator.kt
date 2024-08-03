
import domain.Post
import java.lang.Runtime.getRuntime
import java.sql.DriverManager

object PostCreator {
	private val databaseConnection = DriverManager.getConnection(AppProperties.dbURL)
	private val insertPost by lazy { databaseConnection.prepareStatement(
		"insert into post values (?, ?, ?, ?, ?, ?) on conflict(id) do nothing;"
		)
	}
	
	init {
		getRuntime().addShutdownHook(Thread { databaseConnection.close().also { println("Test db connection is closed") } })
	}
	
	fun insert(post: Post) {
		insertPost.setLong(1, post.id.value)
		insertPost.setString(2, post.createDate.toString())
		insertPost.setString(3, post.updateDate?.toString())
		insertPost.setString(4, post.title)
		insertPost.setString(5, post.shortcut)
		insertPost.setString(6, post.language.name)
		insertPost.execute()
	}
}
