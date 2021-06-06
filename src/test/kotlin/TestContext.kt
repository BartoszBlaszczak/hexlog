
import java.sql.Connection
import java.sql.DriverManager

object TestContext : AppContext(arrayOf()) {
	override val databaseConnection = lazy<Connection> { DriverManager.getConnection("jdbc:sqlite::memory:") }
}