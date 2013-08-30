if(typeof this.console === "undefined") {
    this.console = {};
    this.console.log = function() {};
    this.console.warn = function() {};
    this.console.error = function() {};
    this.console.trace = function() {};
}

//these global vars are predefined before this file is loaded
this.currentFile = this.currentFile || "No file";
this.currentCode = this.currentCode || null;

this.jsHintOpts = this.jsHintOpts || {};
this.defaultOpts = {
	bitwise: true,
	browser: true,
	curly: true,
	eqeqeq: true,
	forin: true,
	noarg: true,
	noempty: true,
	nonew: true,
	strict: true,
	undef: true
};
//extend the default opts
for(var opt in this.defaultOpts) {
	if(this.defaultOpts.hasOwnProperty(opt) && !this.jsHintOpts.hasOwnProperty(opt)) {
		this.jsHintOpts[opt] = this.defaultOpts[opt];
	}
}

this.jsHintGlobals = this.jsHintGlobals || {};

//errors are added to this array
this.errors = this.errors || [];

//run jshint
var result = JSHINT(this.currentCode, this.jsHintOpts, this.jsHintGlobals);
if(!result) {
	for (var i = 0, err; err = JSHINT.errors[i]; i++) {

		this.errors.push({
			file: this.currentFile,
			reason: err.reason,
			line: new String(err.line),
			character: err.character,
			evidence: err.evidence || "",
			code: new String("jshint." + err.code),
			severity: "error"
		});
	}

	var warning = JSHINT.data();

	if (warning.implieds) {
		for (var j = 0; j < warning.implieds.length ; j++) {
			var line = (typeof warning.implieds[j].line == "object") ? warning.implieds[j].line.join() : new String(warning.implieds[j].line);
			this.errors.push({
				file: this.currentFile,
				reason: new String("Implied global '" + warning.implieds[j].name + "'"),
				line: line,
				character: 0,
				evidence: '',
				code: new String("jshint.implied-globals"),
				severity: "warning"
			});
		};
	}

	if (warning.unused) {
		for (var j = 0; j < warning.unused.length ; j++) {
			var line = (typeof warning.unused[j].line == "object") ? warning.unused[j].line.join() : new String(warning.unused[j].line);

			this.errors.push({
				file: this.currentFile,
				reason: new String("Unsed variable '" + warning.unused[j].name + "'"),
				line: line,
				character: 0,
				evidence: '',
				code: "jshint.implied-unuseds",
				severity: "warning"
			});
		};
	}
}