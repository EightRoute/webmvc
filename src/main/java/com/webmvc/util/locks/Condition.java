package com.webmvc.util.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.TimeUnit;
import java.util.Date;


public interface Condition {

    /**
     * 进入等待状态直到被通知或中断
     */
    void await() throws InterruptedException;

    /**
     * 进入等待状态直到被通知，对中断不敏感
     */
    void awaitUninterruptibly();

    /**
     * 进入等待状态直到被通知或中断或超时
     */
    boolean await(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 进入等待状态直到被通知或中断或到某个时间
     */
    boolean awaitUntil(Date deadline) throws InterruptedException;

    /**
     * 唤醒一个等待在Condition上的线程
     */
    void signal();

    /**
     * 唤醒所有等待在Condition上的线程
     */
    void signalAll();
}
