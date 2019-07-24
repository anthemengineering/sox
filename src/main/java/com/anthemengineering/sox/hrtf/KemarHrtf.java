package com.anthemengineering.sox.hrtf;

import com.anthemengineering.sox.SoxException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static com.anthemengineering.sox.ValidationUtil.nonNull;

public final class KemarHrtf {

    // TODO: should these be float[]?
    private final short[] leftFir;
    private final short[] rightFir;

    public KemarHrtf(short[] leftFir, short[] rightFir) {
        this.leftFir = leftFir;
        this.rightFir = rightFir;
    }

    public static Builder builder() {
        return new Builder();
    }

    public short[] getLeftFir() {
        return leftFir;
    }

    public short[] getRightFir() {
        return rightFir;
    }

    public static final class Builder {
        private static final long WAVE_HEADER_BYTE_LENGTH = 44L;

        private Integer elevation;
        private Integer azimuth;

        Builder() {
            // no op
        }

        public Builder elevation(int elevation) {
            this.elevation = elevation;
            return this;
        }

        public Builder azimuth(int azimuth) {
            this.azimuth = azimuth;
            return this;
        }

        String buildResourceName() {
            nonNull(elevation, "Elevation is required to be specified");
            nonNull(azimuth, "Azimuth is required to be specified");

            return String.format(
                    "kemar/elev%d/H%de%03da.wav",
                    elevation,
                    elevation,
                    azimuth);
        }

        InputStream buildInputStream() {
            String resourceName = buildResourceName();

            return nonNull(
                    getClass().getClassLoader().getResourceAsStream(resourceName),
                    "No data file (" + buildResourceName() + ") with elevation " + elevation + " and azimuth " + azimuth + " could be found");

        }

        public KemarHrtf build() throws IOException {
            // data is stored as a 16-bit, signed, little-endian, PCM, 2-channel wave file with 128 frames
            InputStream inputStream = buildInputStream();

            // skip the wav header
            inputStream.skip(WAVE_HEADER_BYTE_LENGTH);

            short[] leftFir = new short[128];
            short[] rightFir = new short[128];

            ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]).order(ByteOrder.LITTLE_ENDIAN);
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            int bytesRead = 0;

            while (inputStream.available() != 0) {
                bytesRead += inputStream.read(byteBuffer.array());

                leftFir [(bytesRead / 4) - 1] = shortBuffer.get(0);
                rightFir[(bytesRead / 4) - 1] = shortBuffer.get(1);
            }

            // 128 frames * 2 samples per frame * 2 bytes per sample = 512 bytes
            if (bytesRead != 512) {
                throw new SoxException("Expected to read 512 bytes from hrtf data file, but only read " + bytesRead);
            }

            return new KemarHrtf(leftFir, rightFir);
        }
    }
}
