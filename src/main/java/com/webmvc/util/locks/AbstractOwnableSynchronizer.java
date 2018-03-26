package com.webmvc.util.locks;

public class AbstractOwnableSynchronizer  implements java.io.Serializable {


	private static final long serialVersionUID = 4289216149355104320L;


    protected AbstractOwnableSynchronizer() { }


    private transient Thread exclusiveOwnerThread;


    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

}
