package com.olib.threadpool.test;

import com.olib.threadpool.service.OlibThreadPoolService;

public class OlibThreadPoolTest {

	public static void main(String[] args) {
		
		String poolName = "test-pool";
		int maxThreadPoolSize = 2;
		OlibThreadPoolService service = new OlibThreadPoolService(poolName, maxThreadPoolSize);

		String key = "testThread";
		int threadCount = 5;
		for(int i=0; i<threadCount; i++){
			TestThread thread = new TestThread();
			service.start(thread , key+i);
		}
		
		/* Result
		 
			start : test-pool-testThread0-0
			start : test-pool-testThread1-0
			end : test-pool-testThread1-0
			end : test-pool-testThread0-0
			start : test-pool-testThread2-0
			start : test-pool-testThread3-0
			end : test-pool-testThread2-0
			end : test-pool-testThread3-0
			start : test-pool-testThread4-0
			end : test-pool-testThread4-0
		*/
	}

}
