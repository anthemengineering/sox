package com.anthemengineering.sox.hrtf;

import com.anthemengineering.sox.SoxException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class KemarHrtfTest {
    @Test
    public void buildResourceName() {
        assertThat(KemarHrtf.builder()
                            .elevation(60)
                            .azimuth(150)
                            .buildResourceName())
                .isEqualTo("kemar/elev60/H60e150a.wav");

        assertThat(KemarHrtf.builder()
                            .elevation(-30)
                            .azimuth(0)
                            .buildResourceName())
                .isEqualTo("kemar/elev-30/H-30e000a.wav");

        assertThat(KemarHrtf.builder()
                            .elevation(0)
                            .azimuth(0)
                            .buildResourceName())
                .isEqualTo("kemar/elev0/H0e000a.wav");
    }

    @Test(expected = SoxException.class)
    public void buildResourceNameRequiresElevation() {
        KemarHrtf.builder()
                 .azimuth(0)
                 .buildResourceName();
    }

    @Test(expected = SoxException.class)
    public void buildResourceNameRequiresAzimuth() {
        KemarHrtf.builder()
                 .elevation(0)
                 .buildResourceName();
    }

    @Test
    public void buildInputStream() throws IOException {
        try (InputStream inputStream = KemarHrtf.builder()
                                                .elevation(60)
                                                .azimuth(150)
                                                .buildInputStream()) {
            assertThat(inputStream).isNotNull();
        }
    }

    @Test(expected = SoxException.class)
    public void buildInputStreamNameThrowsExceptionIfNoAssociatedDataFile() {
        KemarHrtf.builder()
                 .elevation(100000)
                 .azimuth(0)
                 .buildInputStream();
    }

    @Test
    public void readLeftFir() throws IOException {
        KemarHrtf hrtf = KemarHrtf.builder()
                                  .elevation(0)
                                  .azimuth(45)
                                  .build();

        assertThat(hrtf.getLeftFir()).hasSize(128);
    }
}
