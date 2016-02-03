title=Target Platforms
date=2016-02-01
type=post
tags=tour, lense
status=published
~~~~~~

# Target Platforms

Lense compiles for each target platform in way you an use it directly in that platform.
For this reason some API libraries are only available for a specific platform or a group of platforms.
Only the core Lense module is guaranteed to exist and compile for all platforms.

For a status on the platform support completion see the [project status page](status.html). 

## Java Virtual Machine

Lense can compile to .class files. More specifically to .jar files. Each Lense module corresponds to a .jar file.
This modules are executable directly in a Java Virtual Machine. 


## JavaScript (Embedded in a Browser)

Lense can compile to .js files that can be executed inside a browser.  Each Lense module corresponds to a .js file.
Possibly in practice would be better to compile to some more hight level script like TypeScript or CoffeScript to better support Object Oriented features.

Please keep in mind that being able to compile to JavaScript does not mean that's all it is necessary to build a web application. You would still create you application
with your preferred client-side technology  (like, Angular, for example) and use Lense where you would use JavaScript.

## Common Language Runtime  (Under consideration)

Lense could compile to CRL (the .NET runtime). More specifically to .dll files.
This is a possibility at the moment and care is being taken in the design so feature can be compatible with this runtime, however creating 
a backend compiler to CRL will be an effort to pursue in the far future. 

If you are interested in a Lense CRL runtime please, feel free to contribute

## Android Runtime (Under consideration)

Lense could compile to the Android platform. This could be an effort related with compiling to java .class first and then use android's tools to convert from that to .dex files, or 
we could compile directly to .dex files without compiling to java. 

If you are interested in a Lense Android runtime please, feel free to contribute

## Dart Runtime (Under consideration)

Lense could compile to the Dart platform. Dart has a higher level language with a concept similar to JavaScript to run in a Dart VM. Dart code can even be translated to javascript so
compiling to Dart can be a way to also compile to javascript.

If you are interested in a Lense Dart runtime please, feel free to contribute