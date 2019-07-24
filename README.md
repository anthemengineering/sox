# sox-jna
This project is a Java JNA binding to the [Sound eXchange(SoX)](http://sox.sourceforge.net/) audio processing library.

## Maven Coordinates

```
<dependency>
  <groupId>com.anthemengineering</groupId>
  <artifactId>sox-jna</artifactId>
  <version>${sox-jna.version}</version>
</dependency>
```

## Dependencies
Install sox-14.4.2 onto your library path.

### Mac
```bash
brew install sox
```

This installs `sox.h` to your include path at `/usr/local/Cellar/sox/__VERSION__/include/sox.h`.  Use that as the path to the `sox.h` as the jnaerator argument.

### Ubuntu
```bash
sudo apt install libsox-dev
```

You may need to also run `sudo apt install build-essential`

This installs `sox.h` to your include path at `/usr/include/sox.h`.  Use that as the path to the `sox.h` as the jnaerator argument.

## Generating JNA Bindings

In order to generate the JNA bindings you must have the 'sox.h' file on your system in a way that jnaerator can read from it.

### Via maven

The pom file is setup in a way that it will look for the 'sox.h' file:
  - on linux at `/usr/include/sox.h` (based on the `apt install` of the 'devel' sox package)
  - on mac at `/usr/local/Cellar/sox/__VERSION__/include/sox.h` (as if brew installed it)
  - manually configured via a flag given to maven: `-Dsox_header=/my/path/to/incude/sox.h`

The configuration parameters is given via the `src/main/jnaerator/config.jnaerator` maven resource file.

```bash
./mvnw process-resources  com.nativelibs4java:maven-jnaerator-plugin:generate
```

This command generates sources underneath the `target/generated-sources/jna-bindings/com/anthemengineering/sox/jna` path.

### Manual way (because knowing how things works is a good thing)
```bash
wget https://repo1.maven.org/maven2/com/nativelibs4java/jnaerator/0.12/jnaerator-0.12-shaded.jar
java -jar jnaerator-0.12-shaded.jar -runtime JNA -mode Directory -library sox -package com.anthemengineering.sox.jna sox.h
```

## Building the sox-jna wrapper
```bash
./mvnw package
```

This should then generate the jar: `target/sox-$VERSION.jar`.

## Small examples

- [SoxMain.java](src/test/java/com/anthemengineering/sox/SoxMain.java) 
    ```
    $ ./mvnw exec:java -Dexec.mainClass=com.anthemengineering.sox.SoxMain -Dexec.classpathScope=test
    ```
- [SoxEffectsChainMain.java](src/test/java/com/anthemengineering/sox/SoxEffectsChainMain.java)
    ```
    $ ./mvnw exec:java -Dexec.mainClass=com.anthemengineering.sox.SoxEffectsChainMain -Dexec.classpathScope=test
    ```

## Useful Links

* https://sourceforge.net/p/sox/code/ci/sox-14.4.1/tree/src/example0.c
