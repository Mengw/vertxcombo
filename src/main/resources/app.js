/*
This verticle contains the configuration for our application and co-ordinates
start-up of the verticles that make up the application.
 */

var container = require('vertx/container');

var console = require('vertx/console');

var comboConfig = {
    comboDiskRoot : "c:/staticyui",
    syncRead : false,
    maxMem : 1024 * 1024 * 64,
    defaultMaxAge : 600,
    versionedMaxAge : 365 * 24 * 60 * 60,
    listenPort : 8093
};
container.deployVerticle('com.m3958.vertxio.vertxcombo.ComboHandlerVerticle',
        comboConfig, 3, function(err, deployID) {
            if (!err) {
                console.log("The verticle has been deployed, deployment ID is "
                        + deployID);
            } else {
                console.log("Deployment failed! " + err.getMessage());
            }
        });

container.deployVerticle("com.m3958.vertxio.vertxcombo.MonitorVerticle",
        comboConfig, 1, function(err, deployID) {
            if (!err) {
                console.log("The verticle has been deployed, deployment ID is "
                        + deployID);
            } else {
                console.log("Deployment failed! " + err.getMessage());
            }
        });