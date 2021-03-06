package com.flytxt.utils.processor;
import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private List<Worker> workers = new ArrayList<Worker>();
	private ExecutorService executor;
	private static WatchService watcher ;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static void main(String[] args) throws Exception {
		watcher = FileSystems.getDefault().newWatchService();
		Main main = new Main();
		main.loadFromClasspath();
		watcher  = FileSystems.getDefault().newWatchService();
	}
	
	
	public void loadFromClasspath() throws Exception{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> roots = classLoader.getResources("");
		ArrayList<String> lineProcessors = new ArrayList<String>();
		while(roots.hasMoreElements()){
			URL url = (URL) roots.nextElement();
			logger.debug("url:"+url);
			File root = new File(url.getPath());
			for (File file : root.listFiles()) {
				if (file.isDirectory()) {
				    // Loop through its listFiles() recursively.
				} else {
				    String name = file.getName();
				    logger.debug("found :"+name);
				    if(name.contains("lp"))
				    	lineProcessors.add(name);
				}
			}
		}
		logger.debug("total processors: " +lineProcessors.size());
		startProcessing(lineProcessors);
	}
	private HashMap<String, LineProcessor> map = new HashMap<String, LineProcessor>();
	public static Set<String> wokerAvailablity = new HashSet<String>();
	public void startProcessing(ArrayList<String> lineProcessors ) throws Exception{
		executor = Executors.newFixedThreadPool(lineProcessors.size());
        for (int i = 0; i < lineProcessors.size(); i++) {
        	String lpStr = lineProcessors.get(i);
        	//TODO
        	LineProcessor lp = null;
        	//LineProcessor lp = compileNLoad(lpStr);
        	Path dir = Paths.get(lp.getFolder());
        	try{
        		dir.register(watcher, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE);
        	}catch(java.nio.file.NoSuchFileException e){
        		//TODO log this
        		continue;
        	}
        	map.put(lp.getFolder(), lp);
            Runnable worker = new Worker(lp);
            executor.execute(worker);
          }
        while (!executor.isTerminated()) {
        }
	}
	
	public void init6(){
		for(Worker aWorker: workers){
			aWorker.stop();
		}
		executor.shutdown();
	}
	
	private void handleEvents(){
		while (true) {
		    WatchKey key;
		    try {
		        // wait for a key to be available
		        key = watcher.take();
		    } catch (InterruptedException ex) {
		        return;
		    }
		 
		    for (WatchEvent<?> event : key.pollEvents()) {
		        WatchEvent.Kind<?> kind = event.kind();
		        @SuppressWarnings("unchecked")
		        WatchEvent<Path> ev = (WatchEvent<Path>) event;
		        Path fileName = ev.context();
		        logger.debug(kind.name() + ": " + fileName);
		 
		        if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE) {
		        	String folder = fileName.getParent().toString();
		        	if(! Main.wokerAvailablity.contains(folder)){
		        		executor.execute((Runnable) map.get(folder));
		        		Main.wokerAvailablity.add(folder);
		        	}
		        } 
		    }
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
		}
	}
}
