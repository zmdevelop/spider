package net.kernal.spiderman.worker.result.handler.impl;






import java.io.IOException;
import java.util.Iterator;

import net.kernal.spiderman.kit.Counter;
import net.kernal.spiderman.kit.Properties;
import net.kernal.spiderman.worker.extract.ExtractResult;
import net.kernal.spiderman.worker.result.ResultTask;
import net.kernal.spiderman.worker.result.handler.ResultHandler;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrResultHandler implements ResultHandler {
	
	
	@Override
	public void handle(ResultTask task, Counter c) {
		final ExtractResult er = task.getResult();
		String pageName = er.getPageName();
		if(pageName.endsWith("_LinkList")){//是列表页 不处理
			return ;
		}
		final String url = task.getRequest().getUrl();
		//final String content = er.getResponseBody();
		//final String json = JSON.toJSONString(er.getFields(), true);
		addIndex(er.getFields(),pageName,url);
		
	}

	    public void addIndex(Properties properties,String pagename, String url){  
	        SolrClient solr = new HttpSolrClient("http://localhost/solr/cms_core");  
	        SolrInputDocument document = new SolrInputDocument();  
	        document.addField("id", url+"_"+pagename);
	        document.addField("url",url);
	        Iterator<String> iterator = properties.keySet().iterator();
	        while(iterator.hasNext()){
	        	String key = iterator.next();
	        	if(key.equals("publishDate")){
	        		document.addField(key, properties.get(key).toString().replace(" ", "T")+"Z");
	        	}else{
	        	document.addField(key, properties.get(key));
	        	}
	        }
	        UpdateResponse response;
			try {
				response = solr.add(document);
				System.out.println("[添加solr索引]"+response.getElapsedTime());  
				solr.commit();  
			} catch (SolrServerException e) {
				System.err.println( "[添加solr索引失败]");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println( "[添加solr索引失败]");
				e.printStackTrace();
			}finally{
				try {
					solr.close();
				} catch (IOException e) {
					System.err.println( "[solr失败]");
					e.printStackTrace();
				}  
			}
			System.out.println("[添加solr索引完成]");
	    }  
	    
	    public void createSolrClient(){  
	         try {  
	             SolrClient solr = new HttpSolrClient("http://localhost/solr/cms_core");  
	             SolrPingResponse  response = solr.ping();  
	             //打印执行时间  
	             System.out.println(response.getElapsedTime());  
	             solr.close();  
	         } catch (Exception e) {  
	             e.printStackTrace();  
	         }  
	     } 
	    @SuppressWarnings("static-access")
		public static void main(String[] args) {
			new SolrResultHandler().addIndex(new Properties().from(
					new String[]{"-title","韩寒论电影"
							,"-content","韩寒论电影的十个要素"
							,"-origin","sina"
							,"-publishDate","1995-12-31T23:59:59Z"
							,"-url","wwww.ddd"
							,"-displayName","韩寒"})
					, "微博", "wwwhtmlssshtml");
	    	/*SolrClient solr = new HttpSolrClient("http://localhost/solr/cms_core");  
	        SolrInputDocument document = new SolrInputDocument();  
	        document.addField("id", "sdd_微博");
	        	document.addField("title","韩寒");
	        	document.addField("content","韩寒");
	        	document.addField("url","韩寒");
	        	document.addField("origin","韩寒");
	        	document.addField("displayName","韩寒");
	        	document.addField("publishDate","1995-12-31T23:59:59Z");
	        	
	        UpdateResponse response;
			try {
				response = solr.add(document);
				System.out.println("[添加solr索引]"+response.getElapsedTime());  
				solr.commit();  
			} catch (SolrServerException e) {
				System.err.println( "[添加solr索引失败]");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println( "[添加solr索引失败]");
				e.printStackTrace();
			}finally{
				try {
					solr.close();
				} catch (IOException e) {
					System.err.println( "[solr失败]");
					e.printStackTrace();
				}  
			}
			System.out.println("[添加solr索引完成]");*/
		}
}
