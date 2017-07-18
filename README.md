Ultra Debugger
============

Ultra Debugger is an open source modules-based android library for application debugging.

## Features

Ultra Debugger is module-based tools for easy Android applications debugging. It can be useful for developers and testers. Module-based architecture allow to extend functionality and add specific functionality for your project.

Currently available modules:

* Reflection - allow call methods from current activity, see list of fields and methods in this activity
* SQLite - allow see, add, edit and delete items from databases
* Shared Preferences - allow see, add, edit and delete items from shared preferences
* Info - shows information about device
* Logger - Module for logging and variables tracking (use methods `Logger.addLog()` and `Logger.saveValue()`)
* Files - allow see directories and download/remove files from device (including application directory)

_New modules will added..._

## Integration

Here is few option to integrate Ultra Debugger in your application.

##### Add for all configurations

__1.__ Add in build.gradle:
```groovy
compile 'ru.bartwell:ultradebugger:1.3'
```

__2.__ Add in Application class in onCreate() method:
```groovy
UltraDebugger.start(this, 8090);
```

##### Add only for debug configurations

__1.__ Add in build.gradle:
```groovy
debugCompile 'ru.bartwell:ultradebugger:1.3'
compile 'ru.bartwell:ultradebugger.wrapper:1.3'
```

__2.__ Add in Application class in onCreate() method:

```java
UltraDebuggerWrapper.setEnabled(BuildConfig.DEBUG);
UltraDebuggerWrapper.start(this, 8090);
```

_Default port number is 8080._

## Usage

After you integrate library just start application on your smartphone, open browser on your computer and type in address http://xxx.xxx.xxx.xxx:8080, where xxx.xxx.xxx.xxx - IP address of your smartphone. It mean that your computer and smartphone should connected to same network.

## How to support project

Your help is really appreciated.
* Please create your own modules or edit exists and make pull requests
* Or just click star button on this page.

## Modules creation

__1.__ Create Android library project.

__2.__ Add base library as dependency:
```groovy
compile 'ru.bartwell:ultradebugger.base:1.2'
```

__3.__ Create class `Module extends BaseModule` in package `ru.bartwell.ultradebugger.module.xxx`, where `xxx` - your module name.

__4.__ Implement methods in your class:
 * `String getName()` - return human readable your module name from this method
 * `String getDescription()` - return description of your module
 * `HttpResponse handle(HttpRequest)` - handle HTTP requests in this method and return result which will sent into browser.

_You can use classes Page, Form, Table, etc. which helps to construct HTML code._

_Please feel free to see source code of another modules as example._

## TODO

* More modules: Realm, something else
* Pagination
* Design improvement for HTML pages
* Code quality improvements

## License

Copyright © 2017 Artem Bazhanov

Ultra Debugger is provided under an Apache 2.0 License.

Ultra Debugger uses [NanoHttpd](https://github.com/NanoHttpd/nanohttpd) to serve HTTP requests (NanoHttpd Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias All rights reserved).