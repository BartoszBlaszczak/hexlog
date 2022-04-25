package adapter.`in`

import TestContext
import io.kotest.core.spec.style.FunSpec
import start

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class RunningAppTest(body: FunSpec.() -> Unit) : FunSpec(body) {
	companion object {
		init { start(TestContext) }
	}
}
