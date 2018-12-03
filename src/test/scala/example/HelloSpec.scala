package example

import org.scalatest._

class HelloSpec extends FlatSpec with Matchers with DiagrammedAssertions {
  "The Hello object" should "say hello" in {
    Hello.greeting shouldEqual "hello!"
  }
}
