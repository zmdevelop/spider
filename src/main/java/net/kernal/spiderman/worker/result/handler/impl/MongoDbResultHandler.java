package net.kernal.spiderman.worker.result.handler.impl;

import org.apache.commons.lang3.StringUtils;

import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.kit.Properties;
import net.kernal.spiderman.tools.MongoDBUtil;
import net.kernal.spiderman.worker.extract.ExtractResult;
import net.kernal.spiderman.worker.result.ResultTask;
import net.kernal.spiderman.worker.result.handler.ResultHandler;

public class MongoDbResultHandler implements ResultHandler {

	@Override
	public void handle(ResultTask task, Counter c) {
		// TODO Auto-generated method stub
		final ExtractResult er = task.getResult();
		String pageName = er.getPageName();
		System.out.println("------------" + pageName + "-----------");
		if (pageName.endsWith("_LinkList")) {// 是列表页 不处理
			return;
		}
		final String url = task.getRequest().getUrl();
		// final String json = JSON.toJSONString(er.getFields(), true);
		Properties fields = er.getFields();
		fields.put("url", url);
		if (er.getContentType() != null && er.getContentType().startsWith("1")) {
			String tableName = er.getContentType().split("-")[1];
			if (!StringUtils.isEmpty(tableName)) {
				MongoDBUtil.instance.save(tableName, fields);
			}
		} else {
			MongoDBUtil.instance.save("cmsContent_" + pageName, fields);
		}

	}

}
