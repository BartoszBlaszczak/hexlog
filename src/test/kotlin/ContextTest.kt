import io.kotest.core.spec.style.FunSpec

abstract class ContextTest(body: FunSpec.() -> Unit) : FunSpec(body) {
	companion object { init { appContext = TestContext } }
}