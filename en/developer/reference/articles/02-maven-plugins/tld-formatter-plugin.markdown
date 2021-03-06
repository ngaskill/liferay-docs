---
header-id: tld-formatter-plugin
---

# TLD Formatter Plugin

[TOC levels=1-4]

The TLD Formatter plugin lets you format a project's TLD files.

## Usage

To use the plugin, include it in your project's root `pom.xml` file:

```xml
<build>
    <plugins>
    ...
        <plugin>
            <groupId>com.liferay</groupId>
            <artifactId>com.liferay.tld.formatter</artifactId>
            <version>1.0.5</version>
            <configuration>
            </configuration>
        </plugin>
    ...
    </plugins>
</build>
```

You can view an example POM containing the TLD Formatter configuration
[here](https://github.com/liferay/liferay-portal/blob/master/modules/util/tld-formatter/samples/pom.xml).

## Goals

The plugin adds one Maven goal to your project:

Name | Description
---- | -----------
`tld-formatter:format` | Runs the Liferay TLD Formatter to format files.

## Available Parameters

You can set the following parameters in the `<configuration>` section of the
POM:

Parameter Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`baseDirName` | `String` | `"./"` | The base directory to begin searching for TLD files to format.
`plugin` | `boolean` | `true` | Whether to format all the TLD files contained in the working directory. If `false`, all `liferay-portlet-ext.tld` files are ignored.
