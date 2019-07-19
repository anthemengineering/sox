# sox

## JNA

```bash
$ wget https://repo1.maven.org/maven2/com/nativelibs4java/jnaerator/0.12/jnaerator-0.12-shaded.jar
$ java -jar jnaerator-0.12-shaded.jar -runtime JNA -mode Directory -library sox -package com.anthemengineering.sox.jna sox.h
```