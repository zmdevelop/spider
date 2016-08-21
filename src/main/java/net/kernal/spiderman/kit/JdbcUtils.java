package net.kernal.spiderman.kit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

public class JdbcUtils {

	private JdbcUtils()
	{
		
	}
	private static JdbcUtils jdbcUtils = new JdbcUtils();
	public static JdbcUtils getInstance()
	{
		return jdbcUtils;
	}
	
	public int insert(String sql,Properties field,Connection conn)
	{
		String id = UUID.randomUUID().toString().replace("-", "");
		PreparedStatement pstmt;
		int num;
	    try {
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.setString(1, id);
	        pstmt.setString(2, field.getString("title"));
	        pstmt.setString(3, field.getString("sub_title"));
	        pstmt.setString(4, field.getString("content"));
	        pstmt.setString(5, field.getString("publis_time"));
	        pstmt.setString(6, field.getString("origin"));
	        pstmt.setString(7, field.getString("url"));
	        pstmt.setString(8, field.getString("author"));
	        pstmt.setDate(9, new Date(System.currentTimeMillis()));
	        num=pstmt.executeUpdate();
	        pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return 0;
	    }
		return num;
	}
	
	public int insertSelf(Properties field,Connection conn,String tableName)
	{
		String id = UUID.randomUUID().toString().replace("-", "");
		PreparedStatement pstmt;
		StringBuffer sql = new StringBuffer("insert into "+tableName+ "(id,");
        List<String> keys = new ArrayList<String>(field.keySet());
        StringBuffer args =  new StringBuffer(" values (?,");
		for(int i=0;i<keys.size();i++)
        {
        	if(i<keys.size()-1)
        	{
        	sql.append(keys.get(i)).append(",");
        	args.append("?,");
        	}
        	else
        	{
        		sql.append(keys.get(i)).append(")");
        		args.append("?)");
        	}
        }
		sql.append(args);
		int num;
	    try {
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql.toString());
	        pstmt.setString(1, id);
	        for(int i=0;i<keys.size();i++)
	        {
	        	 pstmt.setString(i+2, field.getString((keys.get(i))));
	        }
	        num=pstmt.executeUpdate();
	        pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return 0;
	    }
		return num;
	}
	
	public boolean createTable(Connection conn,String createSql)
	{
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(createSql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws SQLException {
		DataSource dataSource = TomcatDataSource.getDataSource();
		String sql ="CREATE TABLE `ccc` ("+
			  " `id` int(11) NOT NULL AUTO_INCREMENT,"+
			  "`title` varchar(255) DEFAULT NULL,"+
			  "`author` varchar(255) DEFAULT NULL,"+
			  "`publishTime` datetime DEFAULT NULL,"+
			  "`updateTime` datetime DEFAULT NULL,"+
			  "`mainLink` varchar(255) DEFAULT NULL,"+
			  "`selfLink` varchar(255) DEFAULT NULL,"+
			  "`content` text,"+
			  "`mainLinkMd5` varchar(255) DEFAULT NULL,"+
			  "`selfLinkMd5` varchar(255) DEFAULT NULL,"+
			  "`isTopic` bit(1) DEFAULT NULL,"+
			  "`savetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,"+
			  "`tid` varchar(255) DEFAULT NULL,"+
			  "PRIMARY KEY (`Id`),"+
			   "UNIQUE KEY `selfLink` (`selfLink`)"+
			  ")";
		System.out.println(sql);
		getInstance().createTable(dataSource.getConnection(),sql);
		System.out.println("end");
		/*Properties field = new Properties();
		field.put("title", "123");
		field.put("author", "123");
		field.put("selfLink", "123");
		getInstance().insertSelf(field, dataSource.getConnection(), "conversation");*/
	}
	
}
