# sox
This project is a Java JNA binding to the [Sound eXchange(SoX)](http://sox.sourceforge.net/) audio processing library.

## Dependencies
Install sox-14.4.2 onto your library path.

### Ubuntu
```
sudo apt install libsox-dev
```

You may need to also run `sudo apt install build-essential`

This installs `sox.h` to your include path at `/usr/include/sox.h`.  Use that as the path to the `sox.h` as the jnaerator argument.

## Generating JNA Bindings

```bash
$ wget https://repo1.maven.org/maven2/com/nativelibs4java/jnaerator/0.12/jnaerator-0.12-shaded.jar
$ java -jar jnaerator-0.12-shaded.jar -runtime JNA -mode Directory -library sox -package com.anthemengineering.sox.jna sox.h

```

## Building the sox wrapper
```
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
