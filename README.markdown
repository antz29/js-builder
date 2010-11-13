JS Builder
==========

JS Builder is a custom [[Apache ANT|http://ant.apache.org]] task for optimising your JS and managing dependencies between modules. You add simple header comments to each of your JavaScript files giving each file a module name and listing any other modules they are dependent on.

JS Builder will parse all your JS files, resolve dependencies between module, merge and optimise the files into one, always ensuring that the correct load order is maintained relative to any dependencies. You can also build in 'dev' mode where it will output all the files separately.

It also possible to write your own 'Renderers' for JS builder, this allows you total flexibility with how you output your JavaScript. As an example there is a built in tool for generating [[Dominoes JS|http://code.google.com/p/javascript-dominoes/]] rules based on the configured dependencies. 

This is a very new project and the current code base is somewhat mucky, but it is doing the job as a proof of concept. Will be improving it over the coming days and would be good to see what people think! 