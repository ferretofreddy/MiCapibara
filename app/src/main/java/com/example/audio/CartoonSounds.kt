package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generador de efectos de sonido sintetizados estilo cartoon 100% en código.
 * Utiliza AudioTrack con síntesis por onda senoidal y envolventes ADSR sin requerir archivos externos.
 */
object CartoonSounds {
  private val soundScope = CoroutineScope(Dispatchers.Default)
  private var activePlayJob: Job? = null
  private const val SAMPLE_RATE = 22050

  /**
   * Sonido "Ding" brillante, dulce y agudo estilo campanita mágica para cuando cambia el color.
   */
  fun playDing() {
    playSoundAsync { generateDingSound() }
  }

  /**
   * Sonido "Pop" saltarín y caricaturesco con pitch ascendente rápido para prendas, accesorios y fondos.
   */
  fun playPop() {
    playSoundAsync { generatePopSound() }
  }

  private fun playSoundAsync(soundGenerator: () -> ShortArray) {
    activePlayJob?.cancel()
    activePlayJob = soundScope.launch {
      try {
        val audioData = soundGenerator()
        val minBufferSize = AudioTrack.getMinBufferSize(
          SAMPLE_RATE,
          AudioFormat.CHANNEL_OUT_MONO,
          AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = audioData.size * 2
        val finalBufferSize = bufferSize.coerceAtLeast(minBufferSize)

        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(SAMPLE_RATE)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(finalBufferSize)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        audioTrack.write(audioData, 0, audioData.size)
        audioTrack.play()

        // Dejar que termine de reproducir antes de liberar recursos
        val durationMs = (audioData.size * 1000L) / SAMPLE_RATE + 50L
        kotlinx.coroutines.delay(durationMs)
        try {
          audioTrack.stop()
          audioTrack.release()
        } catch (_: Exception) {}
      } catch (_: Exception) {}
    }
  }

  /**
   * Genera un "Ding" brillante (acorde armónico con caída suave)
   */
  private fun generateDingSound(): ShortArray {
    val durationSec = 0.22f
    val numSamples = (SAMPLE_RATE * durationSec).toInt()
    val samples = ShortArray(numSamples)
    
    val baseFreq = 880.0 // Nota La5 brillante
    val harmonicFreq = 1760.0 // Octava superior para brillo cristalino

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples

      // Envolvente de caída exponencial suave (Decay)
      val envelope = (1.0 - progress) * (1.0 - progress)

      // Mezcla de tonos armónicos puros
      val wave1 = sin(2.0 * PI * baseFreq * t) * 0.7
      val wave2 = sin(2.0 * PI * harmonicFreq * t) * 0.3
      val sampleValue = (wave1 + wave2) * envelope * 0.8

      samples[i] = (sampleValue * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return samples
  }

  /**
   * Genera un "Pop" elástico y saltarín (curva de frecuencia rápida ascendente con caída percusiva)
   */
  private fun generatePopSound(): ShortArray {
    val durationSec = 0.12f
    val numSamples = (SAMPLE_RATE * durationSec).toInt()
    val samples = ShortArray(numSamples)

    val startFreq = 280.0
    val endFreq = 720.0

    var currentPhase = 0.0

    for (i in 0 until numSamples) {
      val progress = i.toDouble() / numSamples
      // Frecuencia con subida rápida en curva (pitch bend upward)
      val currentFreq = startFreq + (endFreq - startFreq) * (progress * progress)
      
      currentPhase += 2.0 * PI * currentFreq / SAMPLE_RATE

      // Envolvente percusiva rápida con ataque inmediato
      val envelope = (1.0 - progress) * (1.0 - progress) * (1.0 - progress)
      val sampleValue = sin(currentPhase) * envelope * 0.9

      samples[i] = (sampleValue * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return samples
  }
}
