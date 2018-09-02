# Mandelbrot Explorer
Explore the Mandelbrot Fractal and customise viewing parameters.
~~Currently supported for JDK 8 (will need to switch to serialization rather than JAXB for JDK 9+)~~
Currently supported for JDK 8 & 10 (assumed to also work for JDK 9).

## How to use:
* Set the parameters and click render
* Parameter groupings: fractal appearance, fractal transformation, image options  
* Move the mouse around to update the current position and click to set the saved position
* Click the Save Image button to save the rendered fractal 

## TODO:
* ~~Position finding (on rendered image) (relative mouse tracking)~~
* ~~Save image~~
* ~~Save/load settings with file/name/directory chooser~~
* ~~Add JDK 9+ compatibility~~
* Advanced colour options (choose gradient, looping, offset etc)
* Optimisation
* Code cleanup ~~(especially for reading values from the input form)~~
* Change launch GUI (groupings)
* Beautify Mandelbrot toolbar
* Add help dialogue