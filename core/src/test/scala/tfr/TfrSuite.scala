package tfr

import java.io.{File, FileInputStream}

import cats.effect.IO

class TfrSuite extends munit.FunSuite {
  private[this] val ExampleResourceFile = new File(
    getClass.getResource("/part-00000-of-00004.tfrecords").toURI
  )

  private[this] val PredictionLogResourceFile = new File(
    getClass.getResource("/prediction_log.tfrecords").toURI
  )

  test("read as example") {
    val example = TFRecord
      .readerAsExample[IO](false)
      .apply(new FileInputStream(ExampleResourceFile))
      .unsafeRunSync()

    assert(example.isRight)
  }

  test("read as example with crc32 check") {
    val example = TFRecord
      .readerAsExample[IO](true)
      .apply(new FileInputStream(ExampleResourceFile))
      .unsafeRunSync()

    assert(example.isRight)
  }

  test("read all as example") {
    val examples = TFRecord
      .streamReader(TFRecord.readerAsExample[IO](false))
      .apply(new FileInputStream(ExampleResourceFile))
      .compile
      .toList
      .unsafeRunSync()
      .filter(_.isRight)

    assertEquals(examples.length, 3750)
  }

  test("read as prediction log") {
    val predictionLog = TFRecord
      .readerAsPredictionLog[IO](false)
      .apply(new FileInputStream(PredictionLogResourceFile))
      .unsafeRunSync()

    assert(predictionLog.isRight)
  }

  test("read as prediction log") {
    val predictionLog = TFRecord
      .readerAsPredictionLog[IO](true)
      .apply(new FileInputStream(PredictionLogResourceFile))
      .unsafeRunSync()

    assert(predictionLog.isRight)
  }

  test("read all as example") {
    val predictionLog = TFRecord
      .streamReader(TFRecord.readerAsPredictionLog[IO](false))
      .apply(new FileInputStream(PredictionLogResourceFile))
      .compile
      .toList
      .unsafeRunSync()
      .filter(_.isRight)

    assertEquals(predictionLog.length, 10)
  }

}
