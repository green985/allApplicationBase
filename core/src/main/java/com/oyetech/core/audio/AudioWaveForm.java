package com.oyetech.core.audio;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;

import com.oyetech.core.mediaInput.MediaInput;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Audio uri wave form maker...
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public final class AudioWaveForm {

    private static final String TAG = "AudioWaveForm";

    private static final int BAR_COUNT = 46;
    private static final int SAMPLES_PER_BAR = 4;

    private final Context context;

    public AudioWaveForm(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Based on decode sample from:
     * <p>
     * https://android.googlesource.com/platform/cts/+/jb-mr2-release/tests/tests/media/src/android/media/cts/DecoderTest.java
     */
    @WorkerThread
    @RequiresApi(api = 23)
    @NonNull
    public AudioFileInfo generateWaveForm(@NonNull Uri uri) throws IOException {
        try (MediaInput dataSource = new MediaInput.UriMediaInput(context, uri)) {
            long[] wave = new long[BAR_COUNT];
            int[] waveSamples = new int[BAR_COUNT];

            MediaExtractor extractor = dataSource.createExtractor();

            if (extractor.getTrackCount() == 0) {
                throw new IOException("No audio track");
            }

            MediaFormat format = extractor.getTrackFormat(0);

            if (!format.containsKey(MediaFormat.KEY_DURATION)) {
                throw new IOException("Unknown duration");
            }

            long totalDurationUs = format.getLong(MediaFormat.KEY_DURATION);
            String mime = format.getString(MediaFormat.KEY_MIME);

            if (!mime.startsWith("audio/")) {
                throw new IOException("Mime not audio");
            }

            MediaCodec codec = MediaCodec.createDecoderByType(mime);

            if (totalDurationUs == 0) {
                throw new IOException("Zero duration");
            }

            codec.configure(format, null, null, 0);
            codec.start();

            ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
            ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

            extractor.selectTrack(0);

            long kTimeOutUs = 5000;
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            boolean sawInputEOS = false;
            boolean sawOutputEOS = false;
            int noOutputCounter = 0;

            while (!sawOutputEOS && noOutputCounter < 50) {
                noOutputCounter++;
                if (!sawInputEOS) {
                    int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                    if (inputBufIndex >= 0) {
                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                        int sampleSize = extractor.readSampleData(dstBuf, 0);
                        long presentationTimeUs = 0;

                        if (sampleSize < 0) {
                            sawInputEOS = true;
                            sampleSize = 0;
                        } else {
                            presentationTimeUs = extractor.getSampleTime();
                        }

                        codec.queueInputBuffer(
                                inputBufIndex,
                                0,
                                sampleSize,
                                presentationTimeUs,
                                sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                        if (!sawInputEOS) {
                            int barSampleIndex = (int) (SAMPLES_PER_BAR * (wave.length * extractor.getSampleTime()) / totalDurationUs);
                            sawInputEOS = !extractor.advance();
                            int nextBarSampleIndex = (int) (SAMPLES_PER_BAR * (wave.length * extractor.getSampleTime()) / totalDurationUs);
                            while (!sawInputEOS && nextBarSampleIndex == barSampleIndex) {
                                sawInputEOS = !extractor.advance();
                                if (!sawInputEOS) {
                                    nextBarSampleIndex = (int) (SAMPLES_PER_BAR * (wave.length * extractor.getSampleTime()) / totalDurationUs);
                                }
                            }
                        }
                    }
                }

                int outputBufferIndex;
                do {
                    outputBufferIndex = codec.dequeueOutputBuffer(info, kTimeOutUs);
                    if (outputBufferIndex >= 0) {
                        if (info.size > 0) {
                            noOutputCounter = 0;
                        }

                        ByteBuffer buf = codecOutputBuffers[outputBufferIndex];
                        int barIndex = (int) ((wave.length * info.presentationTimeUs) / totalDurationUs);
                        long total = 0;
                        for (int i = 0; i < info.size; i += 2 * 4) {
                            short aShort = buf.getShort(i);
                            total += Math.abs(aShort);
                        }
                        if (barIndex >= 0 && barIndex < wave.length) {
                            wave[barIndex] += total;
                            waveSamples[barIndex] += info.size / 2;
                        }
                        codec.releaseOutputBuffer(outputBufferIndex, false);
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            sawOutputEOS = true;
                        }
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        codecOutputBuffers = codec.getOutputBuffers();
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        Log.d(TAG, "output format has changed to " + codec.getOutputFormat());
                    }
                } while (outputBufferIndex >= 0);
            }

            codec.stop();
            codec.release();
            extractor.release();

            float[] floats = new float[BAR_COUNT];
            byte[] bytes = new byte[BAR_COUNT];
            float max = 0;

            for (int i = 0; i < BAR_COUNT; i++) {
                if (waveSamples[i] == 0) continue;

                floats[i] = wave[i] / (float) waveSamples[i];
                if (floats[i] > max) {
                    max = floats[i];
                }
            }

            for (int i = 0; i < BAR_COUNT; i++) {
                float normalized = floats[i] / max;
                bytes[i] = (byte) (255 * normalized);
            }

            return new AudioFileInfo(totalDurationUs, bytes);
        }
    }


    public static class AudioFileInfo {
        private final long durationUs;
        private final byte[] waveFormBytes;
        private final float[] waveForm;

        private AudioFileInfo(long durationUs, byte[] waveFormBytes) {
            this.durationUs = durationUs;
            this.waveFormBytes = waveFormBytes;
            this.waveForm = new float[waveFormBytes.length];

            for (int i = 0; i < waveFormBytes.length; i++) {
                int unsigned = waveFormBytes[i] & 0xff;
                this.waveForm[i] = unsigned / 255f;
            }
        }

        public long getDuration(@NonNull TimeUnit timeUnit) {
            return timeUnit.convert(durationUs, TimeUnit.MICROSECONDS);
        }

        public float[] getWaveForm() {
            return waveForm;
        }

    }
}
