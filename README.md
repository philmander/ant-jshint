#Ant task for JSHint

Easily automate JSHint (http://www.jshint.com/) validation on your Javascript code base with Apache Ant.

To get started download the ant-jshint jar file and include the following code in your Ant build file.

```xml
<!-- Define the task -->
<taskdef name="jshint" classname="com.philmander.jshint.JsHintAntTask" 
    classpath="${basedir}/jshint/ant-jshint-0.3-deps.jar" />

<target name="runJsHint">
  
  <!-- Lint the code -->
  <jshint dir="${basedir}/src/js" includes="**/*.js" />
    
</target>
```

##Parameters

###Task attributes

Attribute   | Description | Required
----------- | ----------- | ------------------
dir         | The directory to scan for files to validate | yes
options     | A comma separated list of jshint options. E.g. `evil: true, maxlen: 100` | no
optionsFile | A java properties file containing a list of jshint options (Files with a '.json' extension will be interpreted as JSON). You may prefer this for managing a larger amount of options. The options parameter will override options specified in the options file | no
globals     | A comma separated list of jshint globals. E.g. `jQuery: true, myApp: true` | no
globalsFile | A java properties file containing a list of jshint globals. You may prefer this for managing a larger amount of globals. The globals parameter will override options specified in the globals file | no
fail        | Instructs the task to fail the build if any jshint errors are found | no (defaults to true)
jshintSrc   | The task is packaged with jshint embedded, but an alternative jshint src file can be specified here | no

The task is an implicit fileset. See http://ant.apache.org/manual/Types/fileset.html for more parameters used for file matching or see the usage examples below.

###Nested elements

####report

Attribute    | Description | Required
------------ | ----------- | ------------------
type         | The type of report. Available values are `plain`, `xml` and `jslint-xml` (for compatibility with the JSLint plugins) | no (defaults to 'plain')
destFile     | The file to write the report to | yes


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

###Specifying global variables

```xml
<jshint dir="${basedir}/src/js" globals="$:true">
    <include name="**/*.js"/>
    <exclude name="**/*.min.js"/>
</jshint>
```

###Use for reporting purposes
The task will not fail upon jshint errors and will write results to a file in xml format:

```xml
<jshint dir="${basedir}/src/js" fail="false">
	<include name="**/*.js"/>
	<exclude name="**/*.min.js"/>
	<report type="xml" destFile="${basedir}/jshint/results.xml" />
</jshint>
```

## Running the task in Maven ##

Ant-Jshint isn't deployed to the Maven Central Repository yet, but you can install locally either by cloning the 
project and running

`mvn install`

or downloading the jar release and running

`mvn install:install-file -Dfile=/path/to/ant-jshint-0.2.deps.jar -DgroupId=com.philmander.jshint -DartifactId=ant-jshint -Dversion=0.3 -Dpackaging=jar`

Now use the antrun plugin to add jshint to your Maven build

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-antrun-plugin</artifactId>
	<version>1.7</version>
	<executions>
		<execution>
			<id>jshint</id>
			<phase>validate</phase>
			<configuration>
				<target>
					<taskdef name="jshint" classname="com.philmander.jshint.JsHintAntTask"
						classpathref="maven.plugin.classpath" />

					<jshint dir="${project.basedir}/src/js" options="evil=true,forin=true,devel=false">
						<include name="**/*.js" />
						<exclude name="**/*.min.js" />
					</jshint>
				</target>
			</configuration>
			<goals>
				<goal>run</goal>
			</goals>
		</execution>
	</executions>
	<dependencies>
		<dependency>
			<groupId>com.philmander.jshint</groupId>
			<artifactId>ant-jshint</artifactId>
			<version>0.3</version>
		</dependency>
	</dependencies>
</plugin>
```
## Fork and run locally ##

Ant-Jshint is built using Apache Maven. 

To run tests against your code run `mvn test`

To create a jar file run `mvn package`