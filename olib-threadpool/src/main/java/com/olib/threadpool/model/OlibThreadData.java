package com.olib.threadpool.model;
/*
 * 작성자 : ossang 
 * 개요 : OlibThreadPool 에서 사용되는 쓰레드의 정보 객체
 * 버전 : 1.0.0
 */
public class OlibThreadData {
	private Thread thread;							// 쓰레드 객체
	private String name;							// 쓰레드이름
	private int coreThreadMax;						// 실행할 쓰레드 수
	
	public OlibThreadData(){}
	public OlibThreadData(
			Thread thread,
			String name,
			int coreThreadMax){
		
		this.thread = thread;
		this.name = name;
		this.coreThreadMax = coreThreadMax;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public int getCoreThreadMax() {
		return coreThreadMax;
	}

	public void setCoreThreadMax(int coreThreadMax) {
		this.coreThreadMax = coreThreadMax;
	}

}
