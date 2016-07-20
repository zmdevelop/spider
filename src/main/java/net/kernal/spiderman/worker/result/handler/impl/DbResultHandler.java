package net.kernal.spiderman.worker.result.handler.impl;

import com.alibaba.fastjson.JSON;

import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.worker.extract.ExtractResult;
import net.kernal.spiderman.worker.result.ResultTask;
import net.kernal.spiderman.worker.result.handler.ResultHandler;

public class DbResultHandler implements ResultHandler{

	@Override
	public void handle(ResultTask task, Counter c) {
		// TODO Auto-generated method stub
		final ExtractResult er = task.getResult();
		final String url = task.getRequest().getUrl();
		final String json = JSON.toJSONString(er.getFields(), true);
		System.out.println(json);
	}

}
