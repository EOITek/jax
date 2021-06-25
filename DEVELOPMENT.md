It is recommended to use IntelliJ IDEA as a development tool and install the [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea).

After installing the plug-in:

1. Open `Preferences` -> `Tools` -> `Checkstyle`, add a new item in the `Configuration File` list, and choose dev/google_checks.xml as the local Checkstyle file.

2. Open `Editor` -> `CodeStyle`, `Import schema` -> `CheckStyle Configuration`, choose dev/google_checks.xml to import.

Run the following command to check code style:

```sh
./mvnw validate
```