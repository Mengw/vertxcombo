/*
This verticle contains the configuration for our application and co-ordinates
start-up of the verticles that make up the application.
 */

var container = require('vertx/container');

var console = require('vertx/console');

var comboConfig = {
        comboDiskRoot: "c:/staticyui"
};
container.deployVerticle(
        'com.m3958.vertxio.vertxcombo.ComboHandlerVerticle',
        comboConfig,
        function(err,deployID){
            if (!err) {
                console.log("The verticle has been deployed, deployment ID is " + deployID);
              } else {
                console.log("Deployment failed! " + err.getMessage());
              }
        }
    );