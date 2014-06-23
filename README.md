# Vert.x ComboHandler

It's a simple Combo Handler.

it will be continuous improvement when I know more about vertx and java asynchronous.
##Usage

create a config file named conf.json in any folder,write content bellow:

	{
	    "comboDiskRoot" : "/opt/staticyui",
	    "maxMem" : 67108864,
	    "defaultMaxAge" : 600,
	    "versionedMaxAge" : 31536000,
	    "listenPort" : 8093,
	    "instances" : 5,
	    "charset" : "UTF-8"
	}

enter created folder,type command bellow:

vertx runmod com.m3958.vertxio~vertxcombo~0.0.xx -conf conf.json

vertx will download and start the module.

## supported url pattern

single file: 3.10.0/build/array-invoke/array-invoke-coverage.js,or versioned 3.10.0/build/array-invoke/array-invoke-coverage.js?345

minify style1: /min/f=3.10.0/build/arraylist-filter/arraylist-filter-min.js,
minify style2: /b=3.13.0/build&130727&f=

yuicombo style: /combo/345?3.10.0/build/color-harmony/color-harmony.js&3.10.0/build

## about cache control

if url contains a version number, an etag base on url will be send. max-age can be configed,according to the version number's existence. defaultMaxAge and versionedMaxAge. 


##中文说明
这不是一个通用的combo handler，因为所有被请求的文件都缓存在内存里面，所以您必须预先知道需要合并的文件的大小。
它也许很快，不过未经测试，如果您有时间测试，将非常欢迎。

