// #PACKAGE: static.modules
// #MODULE: module2
// #DEPENDS: module1, lib:jquery

$(function() {
	alert('I am module 2! And I should have been loaded after module and jQuery!');
});