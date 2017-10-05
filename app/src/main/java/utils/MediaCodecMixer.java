package utils;

/*
 * Created by exploitr on 05-10-2017.
 *
 *  TODO Under development!! Not included in release! =until 1.0.1) !! Just to support under API Level 21 !!
 */

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.SparseIntArray;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaCodecMixer {

    private void convertAudio(String filename) throws IOException {

        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/converted.m4a";

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(filename);

        int trackCount = extractor.getTrackCount();

        MediaMuxer mediaMuxer;
        mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        SparseIntArray indexMap = new SparseIntArray(trackCount);
        for (int i = 0; i < trackCount; i++) {
            extractor.selectTrack(i);
            MediaFormat format = extractor.getTrackFormat(i);
            format.setString(MediaFormat.KEY_MIME, CustomMediaCodec.MIMETYPE_AUDIO_AMR_NB);

            int dstIndex = mediaMuxer.addTrack(format);
            indexMap.put(i, dstIndex);
        }

        boolean sawEOS = false;
        int bufferSize = 32000;
        int offset = 100;
        ByteBuffer dstBuf = ByteBuffer.allocate(bufferSize);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();


        mediaMuxer.start();
        while (!sawEOS) {
            bufferInfo.offset = offset;
            bufferInfo.size = extractor.readSampleData(dstBuf, offset);
            if (bufferInfo.size < 0) {

                sawEOS = true;
                bufferInfo.size = 0;
            } else {
                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
                int trackIndex = extractor.getSampleTrackIndex();
                mediaMuxer.writeSampleData(indexMap.get(trackIndex), dstBuf,
                        bufferInfo);
                extractor.advance();
            }
        }
        mediaMuxer.stop();
        mediaMuxer.release();
    }
}
