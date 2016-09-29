package com.easytesting.concurrency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程池帮助类
 * 封装了线程池的常用方法，适用于大部分情况，可以简化线程池的调用，通过CountDownLatch支持多线程同时运行
 */
public class EasyThreadPool {

	private static final Logger log = LoggerFactory.getLogger(EasyThreadPool.class);
	private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS
			.toNanos(2);

	private EasyThreadPool() {
	}

	/**
	 * 线程线程睡眠指定时间
	 * 
	 * @param millis
	 *            睡眠时间，单位millisecond
	 * @throws InterruptedException
	 */
	public final static void sleep(long millis) throws InterruptedException {
		if (millis > 0) {
			log.info("线程" + currentThreadInfo() + "睡眠 " + millis + "毫秒");
			Thread.sleep(millis);
		}
	}

	/**
	 * 通过线程池执行实现Runnable的任务，所有任务同时启动
	 * 
	 * @param taskObjList
	 *            待执行的Runnable实现类对象列表
	 * @param threadPoolSize
	 *            线程池大小
	 */
	public static void executeSimultaneously(List<? extends Runnable> taskObjList,
			int threadPoolSize) {
		if (taskObjList != null && !taskObjList.isEmpty()) {
			final CountDownLatch startGate = new CountDownLatch(1);//保证所有客户端线程同时启动
			final ThreadFactory tFactory = new ThreadFactoryImpl();
			CountDownLatch endGate = new CountDownLatch(taskObjList.size());
			try {
				ExecutorService pooledExecutor = Executors.newFixedThreadPool(
						threadPoolSize, tFactory);
				int taskSize = taskObjList.size();
				log.info("任务总数： " + taskSize);
				endGate = new CountDownLatch(taskSize);
				for (Runnable runnableObj : taskObjList) {
					//传入endGate，用于计数
					pooledExecutor.execute(new CountDownLatchedRunnable(
							runnableObj, startGate, endGate));
				}
				startGate.countDown();//发出启动信号
				endGate.await();//等待全部线程结束
				pooledExecutor.shutdown();//全部结束后关闭线程池
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("等待executors完成过程中发生异常: " + e.getMessage());
			} catch (RejectedExecutionException reex) {
				reex.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 通过线程池执行实现Runnable的任务
	 * 
	 * @param taskObjList
	 *            待执行的Runnable实现类对象列表
	 * @param threadPoolSize
	 *            线程池大小
	 */
	public static void execute(List<? extends Runnable> taskObjList,
			int threadPoolSize) {
		if (taskObjList == null || taskObjList.isEmpty() || threadPoolSize <= 0) {
			log.error("输入参数不合法");
			return;
		}
		CountDownLatch endGate = new CountDownLatch(taskObjList.size());
		if (taskObjList != null && !taskObjList.isEmpty()) {
			final ThreadFactory tFactory = new ThreadFactoryImpl();
			try {
				ExecutorService pooledExecutor = Executors.newFixedThreadPool(
						threadPoolSize, tFactory);
				int taskSize = taskObjList.size();
				log.info("任务总数： " + taskSize);
				for (Runnable runnableObj : taskObjList) {
					pooledExecutor.execute(new CountDownLatchedRunnable(
							runnableObj, endGate));
				}
				endGate.await();//等待全部线程结束
				pooledExecutor.shutdown();
			}  catch (RejectedExecutionException re) {
				re.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 线程睡眠指定时间（单位：毫秒）
	 * 
	 * @param millis
	 *            睡眠时间，单位millisecond
	 * @return true 如果睡眠时间和指定的一致则返回true false 如果线程睡眠和指定的不一致则返回false
	 */
	public final static boolean sleepFor(long millis) {
		boolean isFullySleptForSpecifiedTime = false;
		long nanoDuration = TimeUnit.MILLISECONDS.toNanos(millis);
		final long end = System.nanoTime() + nanoDuration;
		long timeLeft = nanoDuration;
		log.info("线程" + currentThreadInfo() + "睡眠 " + millis + "毫秒");
		try {
			while (timeLeft > 0) {
				// SLEEP_PRECISION睡眠时间的精度
				if (timeLeft > SLEEP_PRECISION)
					Thread.sleep(1);
				else
					Thread.sleep(0); // Thread.yield();
				timeLeft = end - System.nanoTime();

				if (Thread.interrupted())
					throw new InterruptedException();
			}
			isFullySleptForSpecifiedTime = true;
		} catch (InterruptedException e) {
			timeLeft = end - System.nanoTime();
			if (timeLeft < 0) {
				// 即使被中断，如果睡眠时间和指定时间一致，返回true
				isFullySleptForSpecifiedTime = true;
				return isFullySleptForSpecifiedTime;
			}
			log.error("线程" + currentThreadInfo()
					+ "睡眠至 {} ms 时发生中断异常. 剩余  {} ms", millis,
					TimeUnit.NANOSECONDS.toMillis(timeLeft));
			isFullySleptForSpecifiedTime = false;
		}
		return isFullySleptForSpecifiedTime;
	}

	/**
	 * 返回当前执行线程的描述，格式：线程名称-线程hashCode
	 */
	public static final String currentThreadInfo() {
		Thread thread = Thread.currentThread();
		return String.valueOf(thread.getName() + "-" + thread.hashCode());
	}

	static class CountDownLatchedRunnable implements Runnable {
		private final CountDownLatch startGate;
		private final CountDownLatch endGate;
		private final Runnable taskObj;

		public CountDownLatchedRunnable(Runnable taskObj, CountDownLatch startGate, CountDownLatch endGate) {
			this.startGate = startGate;
			this.taskObj = taskObj;
			this.endGate = endGate;
		}
		
		public CountDownLatchedRunnable(Runnable taskObj, CountDownLatch endGate) {
			this.startGate = null;
			this.taskObj = taskObj;
			this.endGate = endGate;
		}

		public void run() {
			try {
				if (startGate != null) {
					startGate.await();
				}
				if (taskObj != null) {
					taskObj.run();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				endGate.countDown();
			}
		}
	}

	/**
	 * 定制自定义线程的描述
	 */
	public static class ThreadFactoryImpl implements ThreadFactory {
		private int counter = 0;
		private String threadName = null;
		private List<String> stats = null;
		
		public ThreadFactoryImpl() {
	        stats = new ArrayList<String>();
		}
		
	    public ThreadFactoryImpl(String threadName) {  
	    	counter = 0;  
	        this.threadName = threadName;  
	        stats = new ArrayList<String>();
	    }

		public Thread newThread(Runnable runnable) {
			Thread thread = null;
			if (threadName == null) {
				thread = new Thread(runnable);  
			} else {
				thread = new Thread(runnable, threadName + "-Thread-" + counter);
			}  
			return thread;
		}

		public Object getThreadFactory() {
			return this;
		}
		
	    public String getStas() {  
	        StringBuffer buffer = new StringBuffer();  
	        Iterator<String> it = stats.iterator();  
	        while(it.hasNext()) {  
	            buffer.append(it.next());  
	            buffer.append("\n");  
	        }  
	        return buffer.toString();  
	    }  
	}

}
