//test should not enforce curly or forin. this file should have 1 error
var y = {};
for(var x in y) {
  alert(y[x]);
}
eval("x=1");