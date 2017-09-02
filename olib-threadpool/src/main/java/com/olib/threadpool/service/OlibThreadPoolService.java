package com.olib.threadpool.service;

import java.util.List;

import com.olib.threadpool.model.OlibThreadData;
import com.olib.threadpool.model.OlibThreadPool;
/*
 * 작성자 : ossang 
 * 버전 : 1.0.0
 */
public class OlibThreadPoolService {

	private OlibThreadPool threadPool;
	
	public OlibThreadPoolService(
			String poolName,
			int maxThreadSize){
		
		this.threadPool = new OlibThreadPool(
				poolName,
				maxThreadSize);
	}
	
	private void initializeThread(
			Thread thread, 
			String key, 
			int coreThreadMax){
		
		if(!threadPool.isInitialize(key)){
			threadPool.addThread(key, new OlibThreadData(thread,key, coreThreadMax));
		}else{
			threadPool.setCoreThreadMax(key, coreThreadMax);
		}
	}
//------------------------------------------------------------------------------
// thread : 실행할 쓰레드 객체
// key : 쓰레드의 키값
// max : 실행할 쓰레드 최대 수	
//------------------------------------------------------------------------------	
	public List<String> start(
			Thread thread, 
			String key, 
			int max) {
		
		initializeThread(thread, key, max);				
		return threadPool.start(key);
	}
	
	public List<String> start(
			Thread thread, 
			String key) {
		
		return start(thread, key, 1);
	}
	
	public int getRunningThreadCount(){
		return threadPool.getCurrentThreadNameList().size();
	}
	
	public long getRunningThreadCount(String key){
		return threadPool.getThreadCount(key);
	}

	public List<String> getCurrentThreadNameList() {
		return threadPool.getCurrentThreadNameList();
	}

	public void removeThread(String key) {
		this.threadPool.removeThread(key);
	}
}
