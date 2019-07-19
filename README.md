# sox

## Dependencies
Install sox-14.4.2 onto your library path.

## Building JNA Bindings

```bash
$ wget https://repo1.maven.org/maven2/com/nativelibs4java/jnaerator/0.12/jnaerator-0.12-shaded.jar
$ java -jar jnaerator-0.12-shaded.jar -runtime JNA -mode Directory -library sox -package com.anthemengineering.sox.jna sox.h

```

## Useful Links

* https://sourceforge.net/p/sox/code/ci/sox-14.4.1/tree/src/example0.c