#Ant task for JSHint

Easily automate JSHint (http://www.jshint.com/) validation on your Javascript code base with Ant.

To get started download the ant-jshint jar file and include the following code in your Ant build file.

```xml
<!-- Define the task -->
<taskdef name="jshint" classname="com.philmander.ant.jshint.JsHintAntTask" 
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
```xml
  <jshint dir="${basedir}/src/js">
    <include name="**/*.js"/>
    <excluded name="**/*.min.js"/>
  </jshint>
```

###Setting options
```xml
  <jshint dir="${basedir}/src/js" options="evil=true,forin=true,devel=false">
    <include name="**/*.js"/>
    <excluded name="**/*.min.js"/>
  </jshint>
```

###Setting options in an external file
```xml
  <jshint dir="${basedir}/src/js" optionsFile="${basedir}/jshint/options.properties">
    <include name="**/*.js"/>
    <excluded name="**/*.min.js"/>
  </jshint>
```
`/options.properties`
```text
evil=true
forin=true
devel=false
```

###Use for reporting purposes
```xml
  <jshint dir="${basedir}/src/js" reportFile="${basedir}/jshint/results.txt" fail="false">
    <include name="**/*.js"/>
    <excluded name="**/*.min.js"/>
  </jshint>
```