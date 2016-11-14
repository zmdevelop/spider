package net.kernal.spiderman.worker.result.handler.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sleepycat.je.utilint.Timestamp;

import net.kernal.spiderman.kit.Context;
import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.kit.K;
import net.kernal.spiderman.kit.Properties;
import net.kernal.spiderman.logger.Logger;
import net.kernal.spiderman.logger.Loggers;
import net.kernal.spiderman.queue.Queue;
import net.kernal.spiderman.queue.Queue.Element;
import net.kernal.spiderman.worker.extract.ExtractResult;
import net.kernal.spiderman.worker.result.ResultTask;
import net.kernal.spiderman.worker.result.handler.ResultHandler;

/**
 * 结果处理器
 * @author 
 *
 */
public class JsonQueueResultHandler implements ResultHandler {

	private final static Logger logger = Loggers.getLogger(JsonQueueResultHandler.class);
	/** 结果计数器 */
	private final AtomicLong counter = new AtomicLong(0);
	
	public void handle(ResultTask task, Counter c) {
		// context对象利用了接口默认实现方法初始化获得
		final Context ctx = context.get();
		// 获取队列对象，我们要往该队列放结果
		final Queue<Element> queue = ctx.getTaskManager().getQueue("SPIDERMAN_JSON_RESULT_TEST");
		
		final String url = task.getRequest().getUrl();
		final ExtractResult result = task.getResult();
		final String pageName = result.getPageName();
		final String modelName = result.getModelName();
		final Properties fields = result.getFields();
		if (pageName.endsWith("_LinkList")) {
			return;
		} 
		Iterator<String> iterator = fields.keySet().iterator();
		final JSONObject map = new JSONObject();
        while(iterator.hasNext()){
        	String KEY = iterator.next();
        	map.put(KEY, fields.get(KEY));
        }
		final byte[] body = map.toJSONString().getBytes();
		final long count = counter.incrementAndGet();
		// 往队列放最终结果，提供给其他消费者使用
		queue.append(body);
		final String info = String.format("发布第%s个结果[page=%s, model=%s, url=%s]:\r\n %s", count, pageName, modelName, url, JSON.toJSONString(map, true));
		logger.warn(info);
	} 

}
