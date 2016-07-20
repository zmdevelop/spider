package net.kernal.spiderman.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import net.kernal.spiderman.Spiderman;
import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.logger.Logger;
import net.kernal.spiderman.logger.Loggers;

/**
 * 工人经理，俗称包工头。
 * 1. 安排工人们开工
 * 2. 接收工人工作结果
 * 3. 结束的时候显示统计结果
 * @author 赖伟威 l.weiwei@163.com 2016-01-16
 *
 */
public abstract class WorkerManager implements Runnable {

	private final static Logger logger = Loggers.getLogger(WorkerManager.class);
	private final String childClassName;
	private int nWorkers;
	private List<Worker> workers;
	private TaskManager queueManager;
	protected TaskManager getQueueManager() {
		return queueManager;
	}
	
	private Counter counter;
	public Counter getCounter() {
		return this.counter;
	}
	
	private CountDownLatch shutdown;
	
	private List<Listener> listeners;
	public static interface Listener {
		public void shouldShutdown();
	}
	public WorkerManager addListener(Listener listener) {
		this.listeners.add(listener);
		return this;
	}
	
	/**
	 * 构造器
	 * @param nWorkers
	 * @param taskQueue
	 */
	public WorkerManager(int nWorkers, TaskManager queueManager, Counter counter) {
		nWorkers = nWorkers > 0 ? nWorkers : 1;
		this.queueManager = queueManager;
		this.nWorkers = nWorkers;
		this.counter = counter;
		this.listeners = new ArrayList<Listener>();
		this.shutdown = new CountDownLatch(1);
		this.childClassName = getClass().getName();
	}
	
	/**
	 * 获取任务，子类实现
	 */
	protected abstract Task takeTask() throws InterruptedException;
	/**
	 * 获取工人实例，子类实现
	 */
	protected abstract Worker buildWorker();
	protected abstract void clear();
	
	/**
	 * 接收工人完成工作的通知，然后调用子类去处理结果
	 */
	public void done(WorkerResult workerResult) {
		this.handleResult(workerResult);
	}
	
	/**
	 * 处理工人的工作结果, 子类实现
	 */
	protected abstract void handleResult(WorkerResult workerResult);
	
	/**
	 * 工作
	 */
	public void run() {
		if (this.queueManager == null) {
			throw new Spiderman.Exception(this.childClassName+" 缺少队列管理器");
		}
		logger.debug("["+this.childClassName+"]我这有"+nWorkers+"个兄弟上班签到");
		workers = new ArrayList<Worker>(nWorkers);
		for (int i = 0; i < nWorkers; i++) {
			final Worker worker = this.buildWorker();
			workers.add(worker);
		}
		
		this.workers.forEach(w -> w.start());
		this.counter.await();
		logger.debug("["+this.childClassName+"]我这有"+nWorkers+"个兄弟下班签退");
		this.shutdown();
	}
	
	public void shutdownAndWait() {
		try {
			this.counter.stop();
			this.shutdown.await();
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * 停工
	 */
	private void shutdown() {
		try {
			this.workers.forEach(w -> {
				logger.warn("["+this.childClassName+"]等待工人["+w.getName()+"]做收尾工作");
				w.await();//wait for the worker finished the job
			});
		} catch(Throwable e) {
		} finally {
			this.clear();
			logger.debug("["+this.childClassName+"]退出管理器...");
			// 统计结果
			final String fmt = "["+this.childClassName+"]统计结果 \r\n 耗时:%sms \r\n 计数:%s \r\n 能力:%s/秒 \r\n 工人数(%s) \r\n";
			final long qps = Math.round((counter.get()*1.0/(counter.getCost())*1000));
			final String msg = String.format(fmt, counter.getCost(), counter.get(), qps, nWorkers);
			logger.debug(msg);
			listeners.forEach(l -> { 
				l.shouldShutdown(); 
			});
			this.listeners.clear();
			this.shutdown.countDown();
		}
	}
	
}
