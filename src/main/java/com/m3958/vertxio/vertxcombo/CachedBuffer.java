package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

public class CachedBuffer {

	private Path comboDiskRootPath;
	
	private Vertx vertx;
	
	private VersionedFile[] files;
	
	private int currentIndex = 0;
	
	private Handler<AsyncResult<Void>> doneCb;
	
	public CachedBuffer(Vertx vertx,Handler<AsyncResult<Void>> doneCb,Path comboDiskRootPath,VersionedFile...infiles){
		this.vertx = vertx;
		this.doneCb = doneCb;
		this.setComboDiskRootPath(comboDiskRootPath);
		this.files = infiles;
	}
	
	
	public void pump(){
		pumpOne();
	}
	
	private VersionedFile nextfile(){
		if(this.files.length > currentIndex){
			return this.files[currentIndex++];
		}else{
			return null;
		}
	}
	
	
	private void pumpOne(){
		final VersionedFile  infstr = nextfile();
		ConcurrentSharedMap<String, Buffer> fbuffers = vertx.sharedData().getMap(ComboHandlerVerticle.sharedMapName);
		
		if(infstr == null){
			doneCb.handle(new VoidAsyncResult(true));
		}else{
			if(fbuffers.containsKey(infstr)){
				pumpOne();
				return;
			}
			Path infPath = this.getComboDiskRootPath().resolve(infstr.getFile());
			Buffer bf = null;
			try {
				bf = vertx.fileSystem().readFileSync(infPath.toString());
			} catch (Exception e) {
				
			}
			if(bf == null){
				doneCb.handle(new VoidAsyncResult(false));
			}else{
				fbuffers.put(infstr.toString(), bf);
				pumpOne();
			}
		}
	}


	public Path getComboDiskRootPath() {
		return comboDiskRootPath;
	}


	public void setComboDiskRootPath(Path comboDiskRootPath) {
		this.comboDiskRootPath = comboDiskRootPath;
	}
}
