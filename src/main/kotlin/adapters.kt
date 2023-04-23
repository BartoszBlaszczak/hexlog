import adapter.out.DatabasePostsRepository
import domain.PostsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("")
val repository: PostsRepository = DatabasePostsRepository
