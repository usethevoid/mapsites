#!/bin/sh
JSLIBS="runtime/assets/lib"
CSS=runtime/assets/css
cp node_modules/angular-formly/dist/formly.js $JSLIBS
cp node_modules/angular/angular.js $JSLIBS
cp node_modules/angular-ui-bootstrap/ui-bootstrap-tpls.js $JSLIBS
cp node_modules/api-check/dist/api-check.js $JSLIBS
cp node_modules/angular-formly-templates-bootstrap/dist/angular-formly-templates-bootstrap.js $JSLIBS
cp node_modules/bootstrap/dist/css/bootstrap.css $CSS
