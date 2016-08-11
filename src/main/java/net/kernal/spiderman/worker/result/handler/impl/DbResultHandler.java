package net.kernal.spiderman.worker.result.handler.impl;
import java.sql.Connection;
import java.sql.SQLException;
import com.alibaba.fastjson.JSON;
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
		final String url = task.getRequest().getUrl();
		final String json = JSON.toJSONString(er.getFields(), true);
		System.out.println(json);
		insertDb(er.getFields());
	}

	private void insertDb(Properties field) {
		String sql = "insert into spider_content (id,title,sub_title,content,publis_time,origin,url,author,create_time) VALUES(?,?,?,?,?,?,?,?,?)";
		Connection conn;
		try {
			conn = TomcatDataSource.getDataSource().getConnection();
			JdbcUtils.getInstance().insert(sql, field, conn);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
