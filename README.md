# Vert.x ComboHandler

It's a simple Combo Handler.

it will be continuous improvement when I know more about vertx and java asynchronous.

## about test

when you look at test code,there are code as below:

	  public static String comboRoot = "c:/staticyui";
	
	  private boolean skipTest() {
	    if (vertx.fileSystem().existsSync(comboRoot)) {
	      return false;
	    } else {
	      Assert.assertTrue(true);
	      testComplete();
	      return true;
	    }
	  }

if your machine hasn't this folder,test will be skip.

## supported url pattern

single file: 3.10.0/build/array-invoke/array-invoke-coverage.js,or versioned 3.10.0/build/array-invoke/array-invoke-coverage.js?345

minify style1: /min/f=3.10.0/build/arraylist-filter/arraylist-filter-min.js,
minify style2: /b=3.13.0/build&130727&f=

yuicombo style: /combo/345?3.10.0/build/color-harmony/color-harmony.js&3.10.0/build

## about cache control

if url contains a version number, an etag base on url will be send. max-age can be configed,according to the version number's existence. defaultMaxAge and versionedMaxAge. 

## limitations

It's not a general purpuse combohandler.It cache all file in memory,so you must know how much size of files to server.
you can set an maxMem config item,when reach this limit,cached file will be remove in FIFO order.

when url has a version number,max-age and etag header will be added.

	var comboConfig = {
	    comboDiskRoot : "c:/staticyui",
	    syncRead : false,
	    maxMem : 1024 * 1024 * 64
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

##中文说明
这不是一个通用的combo handler，因为所有被请求的文件都缓存在内存里面，所以您必须预先知道需要合并的文件的大小。
它也许很快，不过未经测试，如果您有时间测试，将非常欢迎。

