//> using scala "3.3.0-RC3"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"
//> using lib "dev.i10416::libportaudio:0.1.0-SNAPSHOT"
import fr.hammons.slinc.Struct
import fr.hammons.slinc.Ptr
import fr.hammons.slinc.Scope

case class AudioData(phase: Float, freq: Double, isPlaying: Int) derives Struct

@main
def program =
  import libportaudio.types.*
  import libportaudio.libportaudio.instance.*
  import fr.hammons.slinc.runtime.given

  
  val SAMPLE_RATE = 44100
  val FREQ = 440
  val REFERENCE_FREQ = 220
  val PHASE_SHIFT = ((2 * math.Pi * FREQ / SAMPLE_RATE)).toFloat

  val cb: Ptr[PaStreamCallback] = Scope.global {

    Ptr.upcall((_, outputBuffer, framesPerBuffer, _, _, acc) =>
      val data = acc.castTo[AudioData]
      val out = outputBuffer.castTo[Float]
      var phase = (!data).phase
      var i = 0
      val ps = (!data).freq / SAMPLE_RATE
      while (i < framesPerBuffer) do
        val s = math.sin(phase * math.Pi).toFloat
        !out(i * 2) = s
        !out(i * 2 + 1) = s
        phase += ps.toFloat
        if phase >= 1.0f then phase = phase - 2.0f
        i += 1
      !data = (!data).copy(
        phase = phase,
        isPlaying = 1
      )
      0
    )
  }

  Scope.global {
    val info = Pa_GetVersionInfo()
    println((!info).versionControlRevision.copyIntoString(100))
    println((!info).versionText.copyIntoString(100))


    val init_e = Pa_Initialize()
    if init_e != 0 then println(Pa_GetErrorText(init_e))
    val stream = Ptr.blank[Ptr[PaStream]]

    val sampledata =
      AudioData(0.0f, REFERENCE_FREQ * math.pow(2, 3.0 / 12), 1)
    val sampledataptr = Ptr.copy[AudioData](sampledata)
    val e = Pa_OpenDefaultStream(
      stream,
      0,
      2,
      libportaudio.libportaudio.paFloat32,
      SAMPLE_RATE,
      256,
      cb,
      sampledataptr
    )
    if e != 0 then println(Pa_GetErrorText(e).copyIntoString(100))
    val streaminfo = Pa_GetStreamInfo(!stream)
    println(!streaminfo)
    val start_err = Pa_StartStream(!stream)
    println("start sleeping")
    while ((!sampledataptr).isPlaying == 1) do Pa_Sleep(10)
    println("finish sleeping")
    val stop_err = Pa_StopStream(!stream)
    if stop_err != 0 then println(Pa_GetErrorText(stop_err))
    val termination_err = Pa_Terminate()
    if termination_err != 0 then
      println("something went wrong:")
      println(termination_err)
  }
