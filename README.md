# Android application LazyBird

This application is written in Kotlin.

## Requirements
- Android version 29
- an emulator or a physical device to connect with this application

## Starting options
In `app/src/main/java/com/example/blog_e/Config.kt`, you can set the settings for if you want to connect with a local backend or the backend at https://mvsp-api.ncmg.eu/

## Modularization
File structure from `app/src/main/java/com/example/blog_e`:

```
blog_e
└─── /data                  # data layer
└─── /di                    # dependency injection with hilt
└─── /ui                    # ui layer
└─── /uitls                 # utility classes and methods that are used across the application
│   BlogEApplication.kt     # base application
│   Config.kt               # configuration settings e.g. select backend
│   MainActivity.kt         # main screen & main activity for the application
```

### Note
Due to reconsideration of the applications name, you the name is referred to in some files as `BlogE` whereas the new and display