package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.SoxEffectsChain;
import com.anthemengineering.sox.SoxException;
import com.anthemengineering.sox.TestResource;
import com.anthemengineering.sox.effects.Flanger;
import com.anthemengineering.sox.effects.HighpassFilter;
import com.anthemengineering.sox.effects.utils.Filter;
import com.anthemengineering.sox.format.FileSink;
import com.anthemengineering.sox.format.FileSource;
import com.anthemengineering.sox.format.InMemory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

// TODO: Perform real tests
public class SoxEffectsChainApiTest {
    private static final TestResource ascendingFifths = new TestResource("/ascending-fifths.wav");

    @BeforeClass
    public static void setup() throws IOException {
        Files.createDirectories(Paths.get("target/test-output"));
    }


    @Test
    public void shouldMakeBuilder() {
        SoxEffectsChain.Builder builder = SoxEffectsChain.builder()
                .source(new FileSource().path(Paths.get("something")))
                .sink(new FileSink().path("something").allowOverwrite())
                .sink(new InMemory().buffer(new byte[1024]).buffer(ByteBuffer.allocateDirect(1024), 1024))
                .effect("highpass")
                .effect("highpass", "1000")
                .effect(new HighpassFilter()
                        .frequency("1000")
                        .pole(Filter.Pole.DOUBLE_POLE)
                        .width("10"));

        assertThat(builder)
                .describedAs("The builder should be returned, ready to have its chain built.")
                .isNotNull();
    }

    @Test
    public void shouldThrowWithoutArguments() {
        try {
            SoxEffectsChain.builder().build().flowEffects();

            failBecauseExceptionWasNotThrown(SoxException.class);
        } catch (SoxException e) {
            assertThat(e).hasMessage("Source is required to be specified");
        }
    }

    @Test
    public void shouldAllowReadAndWriteFiles() throws IOException {
        Path outPath = testPath("shouldAllowReadAndWriteFiles.wav");
        deleteSafe(outPath);

        SoxEffectsChain.builder()
                .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                .sink(new FileSink().path(outPath.toAbsolutePath()).allowOverwrite())
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger())
                .build()
                .flowEffects()
                .close();

        // TODO: better test
        assertThat(Files.size(outPath)).isEqualTo(1071148);
    }

    @Test
    public void shouldAllowByteBufferToBeUsedAsSink() {
        byte[] buffer = new byte[ascendingFifths.size()];
        testPath("shouldAllowByteBufferToBeUsedAsSink.wav");

        SoxEffectsChain.builder()
                .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                .sink(new InMemory().buffer(buffer))
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger())
                .build()
                .flowEffects()
                .close();

        assertThat(buffer)
                .startsWith(60, 70, 80, 90);
    }

    private void deleteSafe(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path testPath(String p) {
        return Paths.get("target/test-output/", p);
    }

}
