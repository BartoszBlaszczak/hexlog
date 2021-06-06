package domain

interface PostsRepository {
	fun findAll(language: Language): Array<Post>
	fun find(id: PostId): Post?
}