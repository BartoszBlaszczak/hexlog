import adapter.out.DatabasePostsRepository
import adapter.out.DemoPostsRepository
import domain.PostsRepository
import java.sql.DriverManager

open class AppContext(args: Array<String>) {
    val properties = lazy { AppProperties(args) }
    private val profiles = lazy { listOf(DemoProfile).filter { properties.value.profiles.contains(it.name) } }
    private fun <T> from(supplier: (Profile) -> T?): T? = profiles.value.firstNotNullOfOrNull(supplier::invoke)
    
    open val postsRepository = lazy { from(Profile::postsRepository) ?: DatabasePostsRepository }
    open val databaseConnection = lazy { DriverManager.getConnection(properties.value.dbURL) }
}

private interface Profile {
    val name: String
    val postsRepository: PostsRepository? get() = null
}

private object DemoProfile : Profile {
    override val name = "demo"
    override val postsRepository: PostsRepository = DemoPostsRepository
}
