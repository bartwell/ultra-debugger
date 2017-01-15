Ultra Debugger
============

Ultra Debugger is an open source modules-based android library for application debugging.

## Integration

__1.__ Add Ultra Debugger library as a dependency to your project (compileDebug option is recommended).

__2.__ Add libraries of modules that you need as a dependency to your project (compileDebug option is recommended).

__3.__ Initialize library. Add code below in your Application class:

```java
UltraDebugger.start(this);
```

If you need specify custom port, use this code:

```java
UltraDebugger.start(this, 8080);
```

Default port number is 8080.

## Usage

After you integrate main library and modules libraries just start application on your smartphone, open browser on your computer and type in address http://xxx.xxx.xxx.xxx:8080, where xxx.xxx.xxx.xxx - IP address of your smartphone. It mean that your computer and smartphone should connected to same network.

## TODO

* Add more information into readme
* More modules: files, Realm, something else
* Design improvement for HTML pages
* Code quality improvements

## License

Copyright © 2017 Artem Bazhanov

Ultra Debugger is provided under an Apache 2.0 License.

Ultra Debugger uses [NanoHttpd](https://github.com/NanoHttpd/nanohttpd) to serve HTTP requests (NanoHttpd Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias All rights reserved.).