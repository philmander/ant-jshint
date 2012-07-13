//test should not enforce curly or forin. this file should have 2 errors (with for in set to true)
var y = {};
for(var x in y) {
  alert(y[x]);
}
eval("x=1");