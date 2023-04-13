package libportaudio.types
import fr.hammons.slinc.Struct
import fr.hammons.slinc.Ptr
import fr.hammons.slinc.types.*

type PaError = Int
type PaDeviceIndex = Int
type PaHostApiTypeId = Int
type PaHostApiIndex = Int
type PaTime = Double
type PaSampleFormat = Long
type PaStream = Any
type PaStreamFlags = Long
type PaStreamCallback = (
    Ptr[Any], // input
    Ptr[Any], // output
    Long, // frameCount
    Ptr[PaStreamCallbackTimeInfo], // timeinfo
    PaStreamCallbackFlags, // statusflags
    Ptr[Any] // userdata
) => Int
type PaStreamCallbackFlags = Long

/** @param currentTime
  *   The time when the stream callback was invoked
  * @param inputBufferAdcTime
  *   The time when the first sample of the input buffer was captured at the ADC
  *   input
  * @param outputBufferDacTime
  *   The time when the first sample of the output buffer will output the DAC
  */
case class PaStreamCallbackTimeInfo(
    inputBufferAdcTime: Double,
    currentTime: Double,
    outputBufferDacTime: Double
) derives Struct

/** @param structVersion
  *   this is struct version 1
  * @param inputLatency
  *   The input latency of the stream in seconds. This value provides the most
  *   accurate estimate of input latency available to the implementation. It may
  *   differ significantly from the suggestedLatency value passed to
  *   [[Pa_OpenStream]]. The value of this field will be zero (0.) for
  *   output-only streams.
  * @param outputLatency
  *   The output latency of the stream in seconds. This value provides the most
  *   accurate estimate of output latency available to the implementation. It
  *   may differ significantly from the suggestedLatency value passed to
  *   [[Pa_OpenStream]]. The value of this field will be zero (0.) for
  *   input-only streams.
  * @param sampleRate
  *   The sample rate of the stream in Hertz (samples per second). In cases
  *   where the hardware sample rate is inaccurate and PortAudio is aware of it,
  *   the value of this field may be different from the sampleRate parameter
  *   passed to [[Pa_OpenStream]]. If information about the actual hardware
  *   sample rate is not available, this field will have the same value as the
  *   sampleRate parameter passed to [[Pa_OpenStream]].
  */
case class PaStreamInfo(
    structVersion: Int,
    inputLatency: Double,
    outputLatency: Double,
    sampleRate: Double
) derives Struct

/** @param versionMajor
  * @param versionMinor
  * @param versionSubMinor
  * @param versionControlRevision
  *   This is currently the Git revision hash but may change in the future. The
  *   versionControlRevision is updated by running a script before compiling the
  *   library. If the update does not occur, this value may refer to an earlier
  *   revision
  * @param versionText
  *   Version as a string, for example "PortAudio V19.5.0-devel, revision 1952M"
  */
case class PaVersionInfo(
    versionMajor: Int,
    versionMinor: Int,
    versionSubMinor: Int,
    versionControlRevision: Ptr[Byte],
    versionText: Ptr[Byte]
) derives Struct

/** @param deviceCount
  *   The number of devices belonging to this host API. This field may be used
  *   in conjunction with Pa_HostApiDeviceIndexToDeviceIndex() to enumerate all
  *   devices for this host API.
  * @param defaultInputDevice
  *   The default input device for this host API. The value will be a device
  *   index ranging from 0 to (Pa_GetDeviceCount()-1), or paNoDevice if no
  *   default input device is available.
  * @param defaultOutputDevice
  *   The default output device for this host API. The value will be a device
  *   index ranging from 0 to (Pa_GetDeviceCount()-1), or paNoDevice if no
  *   default output device is available.
  * @param name
  *   A textual description of the host API for display on user interfaces.
  * @param structVersion
  *   this is struct version 1
  * @param type
  *   The well known unique identifier of this host API
  */
case class PaHostApiInfo(
    structVersion: CInt,
    `type`: PaHostApiTypeId,
    name: Ptr[CChar],
    deviceCount: CInt,
    defaultInputDevice: PaDeviceIndex,
    defaultOutputDevice: PaDeviceIndex
) derives Struct

/** @param errorCode
  *   the error code returned
  * @param errorText
  *   a textual description of the error if available, otherwise a zero-length
  *   string
  * @param hostApiType
  *   the host API which returned the error code.
  */
case class PaHostErrorInfo(
    hostApiType: PaHostApiTypeId,
    errorCode: Long,
    errorText: Ptr[CChar]
) derives Struct

/** @param device
  *   A valid device index in the range 0 to ([[Pa_GetDeviceCount]] -1)
  *   specifying the device to be used or the special constant
  *   paUseHostApiSpecificDeviceSpecification which indicates that the actual
  *   device(s) to use are specified in hostApiSpecificStreamInfo. This field
  *   must not be set to paNoDevice.
  * @param channelCount
  *   The number of channels of sound to be delivered to the stream callback or
  *   accessed by [[Pa_ReadStream]] or [[Pa_WriteStream]]. It can range from 1
  *   to the value of maxInputChannels in the PaDeviceInfo record for the device
  *   specified by the device parameter.
  * @param sampleFormat
  *   The sample format of the buffer provided to the stream callback,
  *   [[a_ReadStream]] or [[Pa_WriteStream]]. It may be any of the formats
  *   described by the [[PaSampleFormat]] enumeration.
  * @param suggestedLatency
  *   The desired latency in seconds. Where practical, implementations should
  *   configure their latency based on these parameters, otherwise they may
  *   choose the closest viable latency instead. Unless the suggested latency is
  *   greater than the absolute upper limit for the device implementations
  *   should round the suggestedLatency up to the next practical value - ie to
  *   provide an equal or higher latency than suggestedLatency wherever
  *   possible. Actual latency values for an open stream may be retrieved using
  *   the inputLatency and outputLatency fields of the [[PaStreamInfo]]
  *   structure returned by [[Pa_GetStreamInfo]].
  * @param hostApiSpecificStreamInfo
  *   An optional pointer to a host api specific data structure containing
  *   additional information for device setup and/or stream processing.
  *   hostApiSpecificStreamInfo is never required for correct operation, if not
  *   used it should be set to NULL.
  */
case class PaStreamParameters(
    device: PaDeviceIndex,
    channelCount: CInt,
    sampleFormat: PaSampleFormat,
    suggestedLatency: PaTime,
    hostApiSpecificStreamInfo: Ptr[Any]
) derives Struct

/** @param structVersion
  * @param name
  * @param maxInputChannels
  * @param maxOutputChannels
  * @param defaultLowInputLatency
  *   Default latency values for interactive performance.
  *
  * @param defaultLowOutputLatency
  * @param defaultHighInputLatency
  *   Default latency values for robust non-interactive applications (eg.
  *   playing sound files).
  * @param defaultHighOutputLatency
  * @param defaultSampleRate
  */
case class PaDeviceInfo(
    structVersion: CInt,
    name: Ptr[CChar],
    hostApi: PaHostApiIndex,
    maxInputChannels: CInt,
    maxOutputChannels: CInt,
    defaultLowInputLatency: PaTime,
    defaultLowOutputLatency: PaTime,
    defaultHighInputLatency: PaTime,
    defaultHighOutputLatency: PaTime,
    defaultSampleRate: Double
) derives Struct
