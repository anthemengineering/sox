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
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    public void foo() {
        String testString = "This is a test";
        byte[] something = "This is a test".getBytes(Charset.defaultCharset());
        // 1 for the null-terminating character
        ByteBuffer bb = ByteBuffer.allocateDirect(something.length + 1).put(something);

        Pointer pointer = Native.getDirectBufferPointer(bb);
        String output = pointer.getString(0);
        assertThat(output).isEqualTo(testString);

        Memory memory = new Memory(something.length + 1);
        memory.write(0, something, 0, something.length);
        assertThat(memory.getString(0)).isEqualTo(testString);
    }

    @Test
    public void shouldAllowByteBufferToBeUsedAsSource() throws IOException {

        Path path = testPath("shouldAllowByteBufferToBeUsedAsSource.wav");
        deleteSafe(path);

        SoxEffectsChain.builder()
                .source(new InMemory().buffer(ascendingFifths.asByteArray()))
                .sink(new FileSink().path(path).allowOverwrite())
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger())
                .build()
                .flowEffects()
                .close();

        byte[] buffer = Files.readAllBytes(path);

        assertThat(buffer)
                .startsWith("RIFF".getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    public void shouldAllowByteBufferToBeUsedAsSink() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(ascendingFifths.size());
        testPath("shouldAllowByteBufferToBeUsedAsSink.wav");

        SoxEffectsChain.builder()
                .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                .sink(new InMemory().buffer(buffer, ascendingFifths.size()))
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger())
                .build()
                .flowEffects();

        byte[] bufferBytes = new byte[ascendingFifths.size()];
        buffer.rewind();
        buffer.get(bufferBytes);

        assertThat(bufferBytes)
                .startsWith("RIFF".getBytes(StandardCharsets.US_ASCII));
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
