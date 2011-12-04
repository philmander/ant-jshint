//these global vars are predefined before this file is loaded
this.currentFile = this.currentFile || "No file";
this.currentCode = this.currentCode || null;
this.jsHintOpts = this.jsHintOpts || {};

//errors are added to this array
this.errors = this.errors || [];

//run jshint
var result = JSHINT(this.currentCode, this.jsHintOpts);
if(!result)
{
	for (var i = 0, err; err = JSHINT.errors[i]; i++) 
	{		
		this.errors.push({
			file: this.currentFile, 
			reason: err.reason, 
			line: err.line, 
			character: err.character,
			evidence: err.evidence || ""
		});
	}
}