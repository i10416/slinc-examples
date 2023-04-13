//> using scala "3.3.0-RC3"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"
//> using lib "dev.i10416::libportaudio:0.1.0-SNAPSHOT"
//> using lib "org.typelevel::cats-effect:3.5.0-RC2"
//> using lib "org.typelevel::cats-effect-kernel:3.5.0-RC2"
//> using lib "co.fs2::fs2-core:3.6.1"

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.effect.kernel.Resource
import cats.effect.std.Dispatcher
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import fs2.concurrent.SignallingRef

import java.io.File
import scala.annotation.tailrec
import scala.concurrent.duration.*
import scala.util.chaining.*

@main
def run =
  import fr.hammons.slinc.runtime.given
  import fr.hammons.slinc.Scope
  Scope.global {
    // Spring Song/Frühlingslied Op.62 No.6
    val file = File("harunouta.wav")
    val program = for
      cancelSignal <- SignallingRef.of[IO, Boolean](false)
      q <- Queue.unbounded[IO, Float]
      emitAudioData =
        readWav(file)
          .takeWhile(_ => true)
          .covary[IO]
          .evalMap(q.offer)
      listenCancel = IO.readLine
        .map(_ == "q")
        .ifM(cancelSignal.set(true), IO.unit)
      waitUntilCancelation = fs2.Stream
        .never[IO]
        .interruptWhen(cancelSignal)
      _ <- PA
        .openDefaultStream(q)
        .use { handle =>
          for
            _ <- PA.start(handle)
            _ <- (waitUntilCancelation concurrently emitAudioData).compile.drain
              .both(listenCancel)
          yield ()
        }
    yield ()

    program.unsafeRunSync()

  }

object PA:
  import libportaudio.types.*
  import fr.hammons.slinc.Ptr
  import fr.hammons.slinc.Allocator
  import fr.hammons.slinc.runtime.given
  val inst = libportaudio.libportaudio.instance
  val SAMPLE_RATE = 44100
  private def displayErrorText(e: Int): String =
    inst.Pa_GetErrorText(e).copyIntoString(100)
  def sleep(n: Long) = IO(inst.Pa_Sleep(n))
  def start(stream: Ptr[Ptr[PaStream]]): IO[Unit] =
    for
      e <- IO(inst.Pa_StartStream(!stream))
      _ <- IO.raiseWhen(e != 0)(
        Exception(s"something went wrong while starting stream")
      )
    yield ()
  def init(): Allocator ?=> Resource[IO, Ptr[Ptr[PaStream]]] =
    Resource.make(
      for
        _ <- IO.println("[info] initializing portaudio")
        e <- IO(inst.Pa_Initialize())
        _ <- IO.raiseWhen(e != 0)(
          Exception(
            s"something went wrong while initializing portaudio: ${displayErrorText(e)}"
          )
        )
        _ <- IO.println("[info] finish initialization")
        ptr <- IO(
          Ptr.blank[Ptr[PaStream]]
        )
      yield ptr
    )(_ =>
      for
        _ <- IO.println("[info] start terminating portaudio")
        e <- IO(inst.Pa_Terminate())
        _ <- IO.raiseWhen(e != 0)(
          Exception(
            s"something went wrong while termination: ${displayErrorText(e)}"
          )
        )
        _ <- IO.println("[info] finish termination")
      yield ()
    )
  def openDefaultStream(
      q: Queue[IO, Float],
      sampleRate: Double = SAMPLE_RATE,
      framesPerBuffer: Long = 4096
  ): Allocator ?=> Resource[IO, Ptr[Ptr[PaStream]]] = for
    handle <- init()
    callbackHandle <- allocCallback(q)
    stream <- open(handle, callbackHandle, framesPerBuffer, sampleRate)
  yield stream

  def open(
      streamHandle: Ptr[Ptr[PaStream]],
      callbackHandle: Ptr[PaStreamCallback],
      framesPerBuffer: Long,
      sampleRate: Double
  )(using Allocator) = Resource.make(
    for
      _ <- IO.println("[info] opening stream")
      e <- IO(
        inst.Pa_OpenDefaultStream(
          streamHandle,
          0,
          2,
          libportaudio.libportaudio.paFloat32,
          sampleRate,
          framesPerBuffer,
          callbackHandle,
          fr.hammons.slinc.runtime.Null[Any]
        )
      )
      _ <- IO.raiseWhen(e != 0)(
        Exception(
          s"something went wrong while opening stream: ${displayErrorText(e)}"
        )
      )
      _ <- IO.println("[info] complete opening stream")
    yield streamHandle
  )(handle =>
    for
      _ <- IO.println("[info] stopping stream")
      e <- IO(inst.Pa_StopStream(!handle))
      _ <- IO.raiseWhen(e != 0)(
        Exception(
          s"something went wrong while stopping stream: ${displayErrorText(e)}"
        )
      )
      _ <- IO.println("[info] compelete stopping stream")
    yield ()
  )
  def allocCallback(
      q: Queue[IO, Float]
  ): Allocator ?=> Resource[IO, Ptr[PaStreamCallback]] =
    Dispatcher
      .sequential[IO]
      .flatMap(d =>
        Resource.eval[IO, Ptr[PaStreamCallback]](
          IO {

            Ptr.upcall((_, outputBuffer, framesPerBuffer, _, _, _) =>
              val out = outputBuffer.castTo[Float]
              var i = 0
              while (i < framesPerBuffer) do
                val s = d.unsafeRunSync(q.take)
                !out(i * 2) = s
                !out(i * 2 + 1) = s
                i += 1
              0
            )
          }
        )
      )

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioSystem

def readWav(file: File) =
  var totalFramesRead = 0
  var totalBytesRead = 0
  val ais = AudioSystem.getAudioInputStream(file)
  println(s"""|
              |file: ${file}
              |frameLength: ${ais.getFrameLength()}
              |frameRate: ${ais.getFormat().getFrameRate()}
              |channelCount: ${ais.getFormat().getChannels()}
              |bytesPerFrame: ${ais.getFormat().getFrameSize()}
              |isByteOrderBigEndian?: ${ais.getFormat().isBigEndian()}
              |""".stripMargin)

  val bytesPerFrame = ais.getFormat().getFrameSize()
  val frameBufferSize = ais.getFrameLength() * bytesPerFrame
  println("frameBufferSize: " + frameBufferSize)
  val framesBuf = Array.ofDim[Byte](frameBufferSize.toInt)
  var (numBytesRead, numFramesRead) = (0, 0)
  while (
    ais.read(framesBuf).tap { it =>
      numBytesRead = it
    } != -1
  )
  do
    totalBytesRead += numBytesRead
    numFramesRead = numBytesRead / bytesPerFrame
    totalFramesRead += numFramesRead
  val data = framesBuf
  val sbuf =
    ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
  val buf = Array.ofDim[Short](sbuf.capacity())
  sbuf.get(buf)
  val d = buf.map(_ / 32767.0f)
  fs2.Stream.iterable(d)
