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
			line: err.line, 
			character: err.character,
			evidence: err.evidence || ""
		});
	}
}