# Guicer

Guicer is an alternative Domain Specific Language (DSL) for configuring Guice
injectors which is simpler and more concise than the original Guice DSL.

## Documentation

An introduction to the Guicer DSL is available at 
<https://christian-schlichtherle.github.io/guicer>.
This page also contains information about how to contact the project author / team.

## Building

Guicer is built with Apache Maven.
However, there is no distribution yet, so you need to build it yourself.
For building, you need Java SE 6 and Apache Maven 3.0.4 or higher installed.
Change to the repository directory and run:

    $ mvn clean install

## Using

After building, you can simply copy the artifacts from the `target` directory
or add a dependency in your own project like this:

    <project ...>
        ...
        <dependencies>
            <dependency>
                <groupId>net.java.guicer</groupId>
                <artifact>guicer</artifact>
                <version>${project.version}</version>
            </dependency>
            ...
        </dependencies>
    </project>

Note that if you do this, then your project depends on the snapshot build in
your local Maven repository.
There really should be an official release to Maven Central soon.

## License

This project is covered by the [Eclipse Software License, Version 1.0][ESL-1.0].

[ESL-1.0]: http://www.eclipse.org/legal/epl-v10.html
[Guice]: http://code.google.com/p/google-guice/
[Source Code Repository]: http://github.com/christian-schlichtherle/guicer
