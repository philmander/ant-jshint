#Ant task for JSHint

Easily automate JSHint validation on your Javascript code base.

```xml
<taskdef name="jshint" classname="com.philmander.ant.jshint.JsHintAntTask" 
    classpath="${basedir}/jshint/ant-jshint-R0.1.jar" />

<target name="runJsHint">
  
  <jshint dir="${basedir}/src/js" includes="**/*.js" />
    
</target>
```