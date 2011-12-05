#Ant task for JSHint

Easily automate JSHint validation on your Javascript code base.

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
options     | a comma separated list of jshint options. E.g. evil=true,forin=true | no
optionsFile | a java properties file containing a list of jshint options. You may prefer this for managing larger numbers of options. The options parameter will override options in the options file | no
reportFile  | a file to output a report of jshint results to | no
fail        | Instructs the task to fail the build if an jshint errors are found | no (default true)

The task is an implicit fileset. See http://ant.apache.org/manual/Types/fileset.html for more parameters used for file matching or see the usage examples below.