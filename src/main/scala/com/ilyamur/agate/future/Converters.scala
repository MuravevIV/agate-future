package com.ilyamur.agate.future

import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise, ExecutionContext}
import com.twitter.util.{Future => TwitterFuture, Promise => TwitterPromise}

object Converters {

    implicit class RichScalaFuture[T](val scalaFuture: ScalaFuture[T]) {

        def asTwitter(implicit ec: ExecutionContext): TwitterFuture[T] = {
            val twitterPromise = TwitterPromise[T]()
            scalaFuture.onSuccess { case value =>
                twitterPromise.setValue(value)
            }
            scalaFuture.onFailure { case e =>
                twitterPromise.setException(e)
            }
            twitterPromise
        }
    }

    implicit class RichTwitterFuture[T](val twitterFuture: TwitterFuture[T]) {

        def asScala: ScalaFuture[T] = {
            val scalaPromise = ScalaPromise[T]()
            twitterFuture.onSuccess { case value =>
                scalaPromise.success(value)
            }
            twitterFuture.onFailure { case e =>
                scalaPromise.failure(e)
            }
            scalaPromise.future
        }
    }
}
