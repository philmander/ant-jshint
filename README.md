#Ant task for JSHint

Easily automate JSHint (http://www.jshint.com/) validation on your Javascript code base with Apache Ant.

To get started download the ant-jshint jar file and include the following code in your Ant build file.

```xml
<!-- Define the task -->
<taskdef name="jshint" classname="com.philmander.ant.JsHintAntTask" 
    classpath="${basedir}/jshint/ant-jshint-R0.1.jar" />

<target name="runJsHint">
  
  <!-- Lint the code -->
  <jshint dir="${basedir}/src/js" includes="**/*.js" />
    
</target>
```

##Parameters

Attribute   | Description | Required
----------- | ----------- | ------------------
dir         | The directory to scan for files to validate | yes
options     | A comma separated list of jshint options. E.g. evil=true,forin=true | no
optionsFile | A java properties file containing a list of jshint options. You may prefer this for managing a larger amount of options. The options parameter will override options specified in the options file | no
reportFile  | A file to write a report of jshint results to | no
fail        | Instructs the task to fail the build if any jshint errors are found | no (defaults to true)
jshintSrc   | The task is packaged with jshint embedded, but an alternative jshint src file can be specified here | no

The task is an implicit fileset. See http://ant.apache.org/manual/Types/fileset.html for more parameters used for file matching or see the usage examples below.

##Usage examples

###Typical fileset
Lint all JS except minimized source files

```xml
  <jshint dir="${basedir}/src/js">
    <include name="**/*.js"/>
    <exclude name="**/*.min.js"/>
  </jshint>
```

###Setting JSHint options

```xml
  <jshint dir="${basedir}/src/js" options="evil=true,forin=true,devel=false">
    <include name="**/*.js"/>
    <exclude name="**/*.min.js"/>
  </jshint>
```

###Setting options in an external file

```xml
  <jshint dir="${basedir}/src/js" optionsFile="${basedir}/jshint/options.properties">
    <include name="**/*.js"/>
    <exclude name="**/*.min.js"/>
  </jshint>
```
`jshint/options.properties`:
 
    evil=true
    forin=true
    devel=false


###Use for reporting purposes
The task will not fail upon jshint errors and will write results to a text file:
```xml
  <jshint dir="${basedir}/src/js" fail="false" reportFile="${basedir}/jshint/results.txt">
    <include name="**/*.js"/>
    <exclude name="**/*.min.js"/>
  </jshint>
```

## Fork and run locally ##

Ant-Jshint is built using Apache Maven. 

To run tests against your code run `mvn test`

To create a jar file run `mvn package`