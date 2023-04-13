package libportaudio

import fr.hammons.slinc.FSet
import fr.hammons.slinc.Ptr
import fr.hammons.slinc.Struct
import fr.hammons.slinc.annotations.NeedsResource
import fr.hammons.slinc.types.*

@NeedsResource("libportaudio.so")
private trait libportaudio derives FSet:

  import types.*

  // meta
  def Pa_GetVersion(): CInt
  def Pa_GetVersionText(): Ptr[CChar]
  def Pa_GetVersionInfo(): Ptr[PaVersionInfo]
  def Pa_GetErrorText(errorCode: PaError): Ptr[CChar]
  // control
  def Pa_Initialize(): PaError
  def Pa_Terminate(): PaError
  def Pa_GetHostApiCount(): PaHostApiIndex
  def Pa_GetDefaultHostApi(): PaHostApiIndex
  def Pa_GetHostApiInfo(hostApi: PaHostApiIndex): Ptr[PaHostApiInfo]
  def Pa_HostApiDeviceIndexToDeviceIndex(
      hostApi: PaHostApiIndex,
      hostApiDeviceIndex: Int
  ): PaDeviceIndex
  def Pa_GetLastHostErrorInfo(): Ptr[PaHostErrorInfo]

  def Pa_GetDeviceCount(): PaDeviceIndex
  def Pa_GetDefaultInputDevice(): PaDeviceIndex
  def Pa_GetDefaultOutputDevice(): PaDeviceIndex
  def Pa_GetDeviceInfo(device: PaDeviceIndex): Ptr[PaDeviceInfo]
  def Pa_IsFormatSupported(
      inputParameters: Ptr[PaStreamParameters],
      outputParameters: Ptr[PaStreamParameters],
      sampleRate: Double
  ): PaError

  /** @param stream
    *   The address of a PaStream pointer which will receive a pointer to the
    *   newly opened stream.
    * @param inputParameters
    *   A structure that describes the input parameters used by the opened
    *   stream. See [[PaStreamParameters]] for a description of these
    *   parameters. inputParameters must be NULL for output-only streams.
    * @param outputParameters
    *   A structure that describes the output parameters used by the opened
    *   stream. See [[PaStreamParameters]] for a description of these
    *   parameters. outputParameters must be NULL for input-only streams.
    * @param sampleRate
    *   The desired sampleRate. For full-duplex streams it is the sample rate
    *   for both input and output. Note that the actual sampleRate may differ
    *   very slightly from the desired rate because of hardware limitations. The
    *   exact rate can be queried using [[Pa_GetStreamInfo]]. If nothing close
    *   to the desired sampleRate is available then the open will fail and
    *   return an error.
    * @param framesPerBuffer
    *   The number of frames passed to the stream callback function, or the
    *   preferred block granularity for a blocking read/write stream. The
    *   special value [[paFramesPerBufferUnspecified]] (0) may be used to
    *   request that the stream callback will receive an optimal (and possibly
    *   varying) number of frames based on host requirements and the requested
    *   latency settings. Note: With some host APIs, the use of non-zero
    *   framesPerBuffer for a callback stream may introduce an additional layer
    *   of buffering which could introduce additional latency. PortAudio
    *   guarantees that the additional latency will be kept to the theoretical
    *   minimum however, it is strongly recommended that a non-zero
    *   framesPerBuffer value only be used when your algorithm requires a fixed
    *   number of frames per stream callback.
    * @param streamFlags
    *   Flags which modify the behavior of the streaming process. This parameter
    *   may contain a combination of flags ORed together. Some flags may only be
    *   relevant to certain buffer formats.
    * @param streamCallback
    *   A pointer to a client supplied function that is responsible for
    *   processing and filling input and output buffers. If this parameter is
    *   NULL the stream will be opened in 'blocking read/write' mode. In
    *   blocking mode, the client can receive sample data using Pa_ReadStream
    *   and write sample data using Pa_WriteStream, the number of samples that
    *   may be read or written without blocking is returned by
    *   [[Pa_GetStreamReadAvailable]] and [[Pa_GetStreamWriteAvailable]]
    *   respectively.
    * @param userData
    *   A client supplied pointer which is passed to the stream callback
    *   function. It could for example, contain a pointer to instance data
    *   necessary for processing the audio buffers. This parameter is ignored if
    *   streamCallback is NULL.
    *
    * @return
    *   Upon success [[Pa_OpenStream]] returns paNoError and places a pointer to
    *   a valid PaStream in the stream argument. The stream is inactive
    *   (stopped). If a call to [[Pa_OpenStream]] fails, a non-zero error code
    *   is returned (see PaError for possible error codes) and the value of
    *   stream is invalid.
    */
  def Pa_OpenStream(
      stream: Ptr[Ptr[PaStream]],
      inputParameters: Ptr[PaStreamParameters],
      outputParameters: Ptr[PaStreamParameters],
      sampleRate: Double,
      framePerBuffer: Long,
      streamFlags: PaStreamFlags,
      streamCallback: Ptr[?],
      userData: Ptr[Any]
  ): PaError

  /** @param stream
    *   The address of a PaStream pointer which will receive a pointer to the
    *   newly opened stream.
    * @param numInputChannels
    *   The number of channels of sound that will be supplied to the stream
    *   callback or returned by [[Pa_ReadStream]]. It can range from 1 to the
    *   value of maxInputChannels in the [[PaDeviceInfo]] record for the default
    *   input device. If 0 the stream is opened as an output-only stream.
    * @param numOutputChannels
    *   The number of channels of sound to be delivered to the stream callback
    *   or passed to [[[Pa_WriteStream]]. It can range from 1 to the value of
    *   maxOutputChannels in the [[PaDeviceInfo]] record for the default output
    *   device. If 0 the stream is opened as an input-only stream.
    * @param sampleFormat
    *   The sample format of both the input and output buffers provided to the
    *   callback or passed to and from [[Pa_ReadStream]] and [[Pa_WriteStream]].
    *   sampleFormat may be any of the formats described by the
    *   [[PaSampleFormat]] enumeration.
    * @param sampleRate
    *   Same as [[Pa_OpenStream]] parameter of the same name.
    * @param framesPerBuffer
    *   Same as [[Pa_OpenStream]] parameter of the same name.
    * @param streamCallback
    *   Same as [[Pa_OpenStream]] parameter of the same name.
    * @param userData
    *   Same as [[Pa_OpenStream]] parameter of the same name.
    */

  def Pa_OpenDefaultStream(
      stream: Ptr[Ptr[PaStream]],
      numInputChannels: CInt,
      numOutputChannels: CInt,
      sampleFormat: PaSampleFormat,
      sampleRate: Double,
      framePerBuffer: Long,
      streamCallback: Ptr[?],
      userData: Ptr[?]
  ): PaError

  def Pa_CloseStream(stream: Ptr[PaStream]): PaError

  // def Pa_SetStreamFinishedCallback

  def Pa_StartStream(stream: Ptr[PaStream]): PaError
  def Pa_StopStream(stream: Ptr[PaStream]): PaError
  def Pa_AbortStream(stream: Ptr[PaStream]): PaError

  /** Determine whether the stream is stopped. A stream is considered to be
    * stopped prior to a successful call to Pa_StartStream and after a
    * successful call to Pa_StopStream or Pa_AbortStream. If a stream callback
    * returns a value other than paContinue the stream is NOT considered to be
    * stopped.
    * @return
    *   Returns one (1) when the stream is stopped, zero (0) when the stream is
    *   running or, a PaErrorCode (which are always negative) if PortAudio is
    *   not initialized or an error is encountered.
    */
  def Pa_IsStreamStopped(stream: Ptr[PaStream]): PaError

  /** Determine whether the stream is active. A stream is active after a
    * successful call to [[Pa_StartStream]], until it becomes inactive either as
    * a result of a call to [[Pa_StopStream]] or [[Pa_AbortStream]], or as a
    * result of a return value other than paContinue from the stream callback.
    * In the latter case, the stream is considered inactive after the last
    * buffer has finished playing.
    * @return
    *   Returns one (1) when the stream is active (ie playing or recording
    *   audio), zero (0) when not playing or, a PaErrorCode (which are always
    *   negative) if PortAudio is not initialized or an error is encountered.
    */
  def Pa_IsStreamActive(stream: Ptr[PaStream]): PaError
  def Pa_GetStreamInfo(stream: Ptr[PaStream]): Ptr[PaStreamInfo]
  def Pa_GetStreamTime(stream: Ptr[PaStream]): PaTime
  def Pa_GetStreamCpuLoad(stream: Ptr[PaStream]): Double
  def Pa_ReadStream(
      stream: Ptr[PaStream],
      buffer: Ptr[Any],
      frames: Long
  ): PaError
  def Pa_WriteStream(
      stream: Ptr[PaStream],
      buffer: Ptr[Any],
      frames: Long
  ): PaError
  def Pa_GetStreamReadAvailable(stream: Ptr[PaStream]): Long
  def Pa_GetStreamWriteAvailable(stream: Ptr[PaStream]): Long

  // Miscellaneous utilities
  def Pa_GetSampleSize(format: PaSampleFormat): PaError
  def Pa_Sleep(msec: Long): Unit
object libportaudio:

  import fr.hammons.slinc.runtime.given
  final val instance = FSet.instance[libportaudio]

  import types.*
  final val paFloat32: PaSampleFormat = 0x00000001
  final val paInt32: PaSampleFormat = 0x00000002
  final val paInt24: PaSampleFormat = 0x00000004
  final val paInt16: PaSampleFormat = 0x00000008
  final val paInt8: PaSampleFormat = 0x00000010
  final val paUInt8: PaSampleFormat = 0x00000020
  final val paCustomFormat: PaSampleFormat = 0x00010000
  final val paNonInterleaved: PaSampleFormat = 0x80000000

  final val paNoFlag: PaStreamFlags = 0
  final val paClipOff: PaStreamFlags = 0x00000001
  final val paDitherOff: PaStreamFlags = 0x00000002
  final val paNeverDropInput: PaStreamFlags = 0x00000004
  final val paPrimeOutputBuffersUsingStreamCallback: PaStreamFlags = 0x00000008
  final val paPlatformSpecificFlags: PaStreamFlags = 0xffff0000

  final val paInputUnderflow: PaStreamCallbackFlags = 0x00000001
  final val paInputOverflow: PaStreamCallbackFlags = 0x00000002
  final val paOutputUnderflow: PaStreamCallbackFlags = 0x00000004
  final val paOutputOverflow: PaStreamCallbackFlags = 0x00000008
  final val paPrimingOutput: PaStreamCallbackFlags = 0x00000010

  final val paFramesPerBufferUnspecified = 0

