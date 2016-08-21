package net.kernal.spiderman.worker.result.handler.impl;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import net.kernal.spiderman.kit.Context;
import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.kit.JdbcUtils;
import net.kernal.spiderman.kit.Properties;
import net.kernal.spiderman.kit.TomcatDataSource;
import net.kernal.spiderman.worker.extract.ExtractResult;
import net.kernal.spiderman.worker.result.ResultTask;
import net.kernal.spiderman.worker.result.handler.ResultHandler;

public class DbResultHandler implements ResultHandler {
	
	
	@Override
	public void handle(ResultTask task, Counter c) {
		// TODO Auto-generated method stub
		final ExtractResult er = task.getResult();
		String pageName = er.getPageName();
		System.out.println("------------"+pageName+"-----------");
		if(pageName.endsWith("_LinkList")){//是列表页 不处理
			System.out.println("------------"+er.getContentType()+"-----------");
			return ;
		}
		//final String url = task.getRequest().getUrl();
		//final String json = JSON.toJSONString(er.getFields(), true);
        if(er.getContentType()!=null && er.getContentType().startsWith("1"))
        {
        	String tableName = er.getContentType().split("-")[1];
        	if(!StringUtils.isEmpty(tableName))
        	{
        		 insertCustom(er.getFields(),tableName);
        	}
        }
        else
        {
		insertDb(er.getFields());
        }
	}
    /**
     * 标准插入sql
     * @param field
     */
	private void insertDb(Properties field) {
		String sql = "insert into spider_content (id,title,sub_title,content,publis_time,origin,url,author,create_time) VALUES(?,?,?,?,?,?,?,?,?)";
		Connection conn;
		System.out.println(field.getString("url"));
		System.out.println(field);
		try {
			conn = TomcatDataSource.getDataSource().getConnection();
			JdbcUtils.getInstance().insert(sql, field, conn);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 自定义插入
	 */
	private void insertCustom(Properties field,String tableName)
	{
		Connection conn;
		try {
			conn = TomcatDataSource.getDataSource().getConnection();
			JdbcUtils.getInstance().insertSelf(field, conn, tableName);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
