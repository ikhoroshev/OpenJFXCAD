# OpenJFXCAD
JavaFX 3D Printing IDE based on [JCSG](https://github.com/miho/JCSG) and 3DViewer
## How to Build OpenJFXCAD

### Requirements

- Java >= 1.8
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE (with Gradle Plugin)

Open the `OpenJFXCAD` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with IntelliJ IDEA 2016.2) and build it
by calling the `assemble` task.

### IDE (without Gradle Plugin)

#### Eclipse

Call the `eclipse` task from the command line and import the project to your workspace.

#### IntelliJ

Call the `idea` task from the command line and open the project.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/OpenJFXCAD`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    sh gradlew assemble
    
#### Windows (CMD)

    gradlew assemble

