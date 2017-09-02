package com.olib.threadpool.model;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/*
 * 작성자 : ossang 
 * 개요 : 설정된 최대 쓰레드 수 만큼 쓰레드를 생성하여 실행하고 이외의 추가된 쓰레드는 대기열 큐로 관리한다.
 * 버전 : 1.0.0
 */
public class OlibThreadPool {

	// 풀 이름
	private String poolName;
	
	// 최대 쓰레드 생성수
	private int maxThreadSize;
	
	// 실행 대기중인 쓰레드의 키 저장공간
	private LinkedBlockingQueue<String> waitingKeyQueue;
	
	// key : 사용자 정의값 , value : OlibThreadData 객체
	private ConcurrentHashMap<String, OlibThreadData> threadPool;				
	
//------------------------------------------------------------------------------
// - 쓰레드풀 객체 생성자
// @poolName : 쓰레드 풀 명 
// @maxThreadSize : 쓰레드 풀 안에서 실행할 최대 쓰레드 크기
//------------------------------------------------------------------------------	
	public OlibThreadPool(
			String poolName,
			int maxThreadSize){
		
		this.poolName = poolName;
		this.maxThreadSize = maxThreadSize;
		this.waitingKeyQueue = new LinkedBlockingQueue<>();
		this.threadPool = new ConcurrentHashMap<>();
	}
//------------------------------------------------------------------------------
// - 대기중인 쓰레드를 먼저 실행한 후 새로운 쓰레드를 실행한다
// @key : 실행할 쓰레드 키	
// return : 실행된 쓰레드의 nameList
//------------------------------------------------------------------------------	
	public List<String> start(String key){
		List<String> threadNameList = new ArrayList<>();
		
		List<String> starting = startByKey(key);
		if(starting != null) threadNameList.addAll(starting);
		
		List<String> wait = waitingStart();
		if(wait != null) threadNameList.addAll(wait);
		
		return threadNameList;
	}
//------------------------------------------------------------------------------
// - 쓰레드풀에 등록된 쓰레드리스트를 실행
// @key : 실행할 쓰레드 키 
// return : 실행된 쓰레드의 nameList
//------------------------------------------------------------------------------
	public List<String> startByKey(String key){
		List<String> threadNameList = new ArrayList<>();
		OlibThreadData customThread = threadPool.get(key);
		if(customThread == null || customThread.getThread() == null) return null;
		
		for(int i =0; i<customThread.getCoreThreadMax(); i++){
			String name = makeThreadName(customThread.getName(),i);
			if(isRunning(name)){
				continue;
			}
			if(isRunningFull()){
				waitingKeyQueue.add(key);
				return null;
			}
			
			Thread newThread = new Thread(customThread.getThread());
			newThread.setName(name);
			newThread.start();
			
			threadNameList.add(name);
		}
		
		return threadNameList;
	}
//------------------------------------------------------------------------------
// 대기중인 쓰레드를 실행	
//------------------------------------------------------------------------------	
	private List<String> waitingStart(){
		if(waitingKeyQueue.size() == 0) return null;
		List<String> threadNameList = new ArrayList<>();
		String key = null;
		while((key = waitingKeyQueue.poll()) != null){
			if(isRunningFull()){
				sleep(100);
				waitingKeyQueue.add(key);
				continue;
			}
			List<String> nameList = startByKey(key);
			if(nameList == null) return null;
			threadNameList.addAll(nameList);
		}
		return threadNameList;
	}
	
	private void sleep(long millis){
		try {Thread.sleep(millis);} catch (InterruptedException e) {}
	}
//------------------------------------------------------------------------------
// poolName 으로 시작하는 쓰레드를 체크하여 최대 설정값과 비교후 최대 설정값 이상이면 true 리턴 	
//------------------------------------------------------------------------------	
	public boolean isRunningFull(){
		List<String> currentThreadNameList = getCurrentThreadNameList();
		if(currentThreadNameList != null 
			&& (maxThreadSize > 0 && maxThreadSize <= currentThreadNameList.size())){
			return true;
		}
		return false;
	}
//------------------------------------------------------------------------------
// key 값에 해당하는 쓰레드가 현재 동작중인지 판별 (리턴값 = true : 실행중)	
//------------------------------------------------------------------------------	
	public boolean isRunning(String key){
		return getCurrentThreadNameList().contains(key);
	}
//------------------------------------------------------------------------------
// 현재 JVM 에서 poolName 으로 시작하는 쓰레드의 리스트를 리턴한다.	
//------------------------------------------------------------------------------	
	public List<String> getCurrentThreadNameList(){
		return Thread.getAllStackTraces().keySet().stream()
				.map(Thread::getName)
				.filter(name-> name.startsWith(poolName))
				.collect(Collectors.toList());
	}
//------------------------------------------------------------------------------
// 쓰레드 추가
// - key : 쓰레드 키값
// - thread : OlibThreadData 객체	
//------------------------------------------------------------------------------
	public boolean addThread(String key, OlibThreadData thread){
		if(key == null || thread == null) return false;
		threadPool.put(key,thread);
		return true;
	}
//------------------------------------------------------------------------------
// threadName 으로 실행중인 쓰레드 개수 반환	
//------------------------------------------------------------------------------	
	public long getThreadCount(String threadName){
		return getCurrentThreadNameList().stream()
			.filter(t->t.startsWith(threadName))
			.count();
	}
//------------------------------------------------------------------------------
// key 값에 해당하는 쓰레드 초기화 여부	
//------------------------------------------------------------------------------	
	public boolean isInitialize(String key){
		if(threadPool.containsKey(key)) return true;
		return false;
	}
	
//------------------------------------------------------------------------------
// remove Thread
//------------------------------------------------------------------------------
	public void removeThread(String key) {
		this.threadPool.remove(key);
		if(waitingKeyQueue.contains(key)){
			waitingKeyQueue.remove(key);
		}
	}
//------------------------------------------------------------------------------
// get & set	
//------------------------------------------------------------------------------
	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public int getLimitThreadSize() {
		return maxThreadSize;
	}

	public void setLimitThreadSize(int limitThreadSize) {
		this.maxThreadSize = limitThreadSize;
	}
	
	public String makeThreadName(String key,int index){
		return String.format("%s-%s-%d", poolName,key,index);
	}
	
	public boolean setCoreThreadMax(String key, int coreThreadMax){
		if(!threadPool.containsKey(key)) return false;
		threadPool.get(key).setCoreThreadMax(coreThreadMax);
		return true;
	}
}
