package com.webmvc.util.threadpool;


public interface RejectedExecutionHandler {
	 void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}