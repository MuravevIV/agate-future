package com.ilyamur.agate.future

import org.junit.Test
import scala.concurrent.{Future => ScalaFuture, Await => ScalaAwait, ExecutionContext}
import com.twitter.util.{Future => TwitterFuture, Await => TwitterAwait}
import org.junit.Assert._
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit


class ConvertersTest {

    import com.ilyamur.agate.future.Converters._

    import ExecutionContext.Implicits.global
    val awaitDuration = Duration(3, TimeUnit.SECONDS)

    @Test
    def scalaToTwitterSuccess_same() {
        val value = new Object()
        val scalaFuture: ScalaFuture[Any] = ScalaFuture.successful(value)
        val twitterFuture: TwitterFuture[Any] = scalaFuture.asTwitter
        val result = TwitterAwait.result(twitterFuture)
        assertSame(value, result)
    }

    @Test
    def scalaToTwitterFailure_same() {
        val exception = new Exception()
        val scalaFuture: ScalaFuture[Any] = ScalaFuture.failed(exception)
        val twitterFuture: TwitterFuture[Any] = scalaFuture.asTwitter
        val optException = try {
            TwitterAwait.result(twitterFuture)
            None
        } catch {
            case e: Exception => Some(e)
        }
        optException match {
            case Some(e) =>
                assertSame(exception, e)
            case None =>
                fail("must throw an exception")
        }
    }

    @Test
    def twitterToScalaSuccess_same() {
        val value = new Object()
        val twitterFuture: TwitterFuture[Any] = TwitterFuture.value(value)
        val scalaFuture: ScalaFuture[Any] = twitterFuture.asScala
        val result = ScalaAwait.result(scalaFuture, awaitDuration)
        assertSame(value, result)
    }

    @Test
    def twitterToScalaFailure_same() {
        val exception = new Exception()
        val twitterFuture: TwitterFuture[Any] = TwitterFuture.exception(exception)
        val scalaFuture: ScalaFuture[Any] = twitterFuture.asScala
        val optException = try {
            ScalaAwait.result(scalaFuture, awaitDuration)
            None
        } catch {
            case e: Exception => Some(e)
        }
        optException match {
            case Some(e) =>
                assertSame(exception, e)
            case None =>
                fail("must throw an exception")
        }
    }
}
