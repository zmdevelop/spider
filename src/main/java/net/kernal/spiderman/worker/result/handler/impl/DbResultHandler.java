package net.kernal.spiderman.worker.result.handler.impl;






import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.kit.Properties;
import net.kernal.spiderman.worker.extract.ExtractResult;
import net.kernal.spiderman.worker.result.ResultTask;
import net.kernal.spiderman.worker.result.handler.ResultHandler;

public class DbResultHandler implements ResultHandler {
	
	
	@Override
	public void handle(ResultTask task, Counter c) {
		// TODO Auto-generated method stub
		final ExtractResult er = task.getResult();
		final String url = task.getRequest().getUrl();
		final String json = JSON.toJSONString(er.getFields(), true);
		System.out.println(json);
		insertDb(er.getFields());
	}

	private void insertDb(Properties field) {
		// TODO Auto-generated method stub
		 Connection conn = connectMysql();
		 String id = UUID.randomUUID().toString().replace("-", "");
		    String sql = "insert into spider_content (id,title,content,url) values(?,?,?,?)";
		    PreparedStatement pstmt;
		    try {
		        pstmt = (PreparedStatement) conn.prepareStatement(sql);
		        pstmt.setString(1, id);
		        pstmt.setString(2, field.getString("title"));
		        pstmt.setString(3, field.getString("text"));
		        pstmt.setString(4, field.getString("url"));
		        pstmt.executeUpdate();
		        pstmt.close();
		        conn.close();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
	
	private Connection connectMysql()
	{
		 String driver = "com.mysql.jdbc.Driver";
		    String url = "jdbc:mysql://127.0.0.1:3306/spider";
		    String username = "root";
		    String password = "root";
		    Connection conn = null;
		    try {
		        Class.forName(driver); //classLoader,加载对应驱动
		        conn = (Connection) DriverManager.getConnection(url, username, password);
		    } catch (ClassNotFoundException e) {
		        e.printStackTrace();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return conn;
	}

}
