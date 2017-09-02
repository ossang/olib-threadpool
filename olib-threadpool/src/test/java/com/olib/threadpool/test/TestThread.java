package com.olib.threadpool.test;

public class TestThread extends Thread{

	public TestThread(){}
	
	public void run(){
		System.out.println("start : "+this.currentThread().getName());
		try {Thread.sleep(5000);} catch (InterruptedException e) {}
		System.out.println("end : "+this.currentThread().getName());
	}
}
