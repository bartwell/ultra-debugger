Ultra Debugger
============

Ultra Debugger is an open source modules-based android library for application debugging.

## Integration

__1.__ Add Ultra Debugger library as a dependency to your project:

```groovy
debugCompile('ru.bartwell:ultradebugger:1.0') {
    exclude group: 'ru.bartwell:ultradebugger.module'
}
```

_You can use `compile` instead of `debugCompile` if you need._

__2.__ Add libraries of modules that you need:
```groovy
debugCompile 'ru.bartwell:ultradebugger.module.reflection:1.0'
debugCompile 'ru.bartwell:ultradebugger.module.sqlite:1.0'
debugCompile 'ru.bartwell:ultradebugger.module.sharedpreferences:1.0'
```

__3.__ Initialize library. Add code below in your Application class:

```java
UltraDebugger.start(this);
```

If you need specify custom port, add it as second argument:

```java
UltraDebugger.start(this, 8081);
```

_Default port number is 8080._

## Features

Ultra Debugger is module-based tools for easy Android applications debugging. It can be useful for developers and testers. Module-based architecture allow to extend functionality and add specific functionality for your project.

Currently available modules:

* Reflection - allow call methods from current activity, see list of fields and methods in this activity
* SQLite - allow see, add, edit and delete items from databases
* Shared Preferences - allow see, add, edit and delete items from shared preferences

_New modules will added..._

## Usage

After you integrate main library and modules libraries just start application on your smartphone, open browser on your computer and type in address http://xxx.xxx.xxx.xxx:8080, where xxx.xxx.xxx.xxx - IP address of your smartphone. It mean that your computer and smartphone should connected to same network.

## How to support project

Your help is really appreciated.
* Please create your own modules and make pull requests
* Or just click star button on this page.

## Modules creation

__1.__ Create Android library project.

__2.__ Add base library as dependency:
```groovy
compile 'ru.bartwell:ultradebugger.base:1.0'
```

__3.__ Create class `Module extends BaseModule` in package `ru.bartwell.ultradebugger.module.xxx`, where `xxx` - your module name.

__4.__ Implement methods in your class:
 * `String getName()` - return human readable your module name from this method
 * `String getDescription()` - return description of your module
 * `HttpResponse handle(HttpRequest)` - handle HTTP requests in this method and return result which will sent into browser.

_You can use classes Page, Form, Table, etc. which helps to construct HTML code._

_Please feel free to see source code of another modules as example._

## TODO

* More modules: files, Realm, something else
* Pagination
* Design improvement for HTML pages
* Code quality improvements

## License

Copyright © 2017 Artem Bazhanov

Ultra Debugger is provided under an Apache 2.0 License.

Ultra Debugger uses [NanoHttpd](https://github.com/NanoHttpd/nanohttpd) to serve HTTP requests (NanoHttpd Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias All rights reserved).