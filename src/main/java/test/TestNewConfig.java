package test;

import java.lang.reflect.Constructor;
import java.util.Map;

import net.kernal.spiderman.Config;
import net.kernal.spiderman.Spiderman;
import net.kernal.spiderman.kit.K;
import net.kernal.spiderman.kit.Seed;
import net.kernal.spiderman.kit.XMLConfBuilder;
import net.kernal.spiderman.worker.extract.ExtractTask;
import net.kernal.spiderman.worker.extract.extractor.Extractor;
import net.kernal.spiderman.worker.extract.extractor.impl.TextExtractor;
import net.kernal.spiderman.worker.extract.schema.Model;
import net.kernal.spiderman.worker.extract.schema.Page;
import net.kernal.spiderman.worker.extract.schema.Page.Models;
import net.kernal.spiderman.worker.extract.schema.Page.UrlMatchRules;
import net.kernal.spiderman.worker.extract.schema.rule.EqualsRule;
import net.kernal.spiderman.worker.extract.schema.rule.RegexRule;

/** 
 * 描述:测试config
 * 作者:zhao
 * 日期:2016年7月16日下午3:15:49
 * 版本:
 */
public class TestNewConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String xml = "list-page-example.xml";
		final Config conf = new XMLConfBuilder(xml).build();// 通过XMLBuilder构建CONF对象
		Config cnf = new Config();
		Seed seed = new Seed("wangyi","http://news.163.com/domestic/");
		cnf.addSeed(seed);
		cnf.set("duration", "0");
		cnf.set("scheduler.period", "0");
		cnf.set("worker.extract.size", "10");
		cnf.set("worker.result.size", "10");
		cnf.set("worker.result.limit", "100");
		cnf.set("worker.result.handler", "net.kernal.spiderman.worker.result.handler.impl.FileJsonResultHandler");
	    Class<Extractor> cls = K.loadClass("net.kernal.spiderman.worker.extract.extractor.impl.TextExtractor");
	    Class<Extractor> clsTT =K.loadClass("net.kernal.spiderman.worker.extract.extractor.impl.LinksExtractor");
	    cnf.registerExtractor("Text",cls);
		cnf.registerExtractor("Links", clsTT);
		System.out.println(cnf.getSeeds().all());
		Page page = new Page("内容页"){
			public void config(UrlMatchRules rules, Models models) {}
		};
		page.getRules().add(new RegexRule("^http://news\\.163\\.com/\\d+/\\d+/\\d+/.*\\.html#f\\=dlist$").setNegativeEnabled(false));
		final Map<String, Class<Extractor>> extractors = conf.getExtractors().all();
    	final Class<? extends Extractor> extractorClass = extractors.get("Text");
    	if (extractorClass == null) {
    		throw new Spiderman.Exception("");
    	}
    	final Constructor<? extends Extractor> ct;
		try {
			ct = extractorClass.getConstructor(ExtractTask.class, String.class, Model[].class);
		} catch (Exception e) {
			throw new Spiderman.Exception("", e);
		}
		page.setExtractorBuilder((t, p, mds) -> {
			try {
				return ct.newInstance(t, p, mds);
			} catch (Exception e) {
				throw new Spiderman.Exception("", e);
			}
		});
		Page pageList = new Page("列表页"){
			public void config(UrlMatchRules rules, Models models) {}
		};
		pageList.getRules().add(new RegexRule("^http://news\\.163\\.com/((domestic/)|(special/0001124J/guoneinews_\\d+\\.html#headList))$").setNegativeEnabled(false));
    	final Class<? extends Extractor> extractorClass2 = extractors.get("Links");
    	final Constructor<? extends Extractor> ct2;
		try {
			ct2 = extractorClass2.getConstructor(ExtractTask.class, String.class, Model[].class);
		} catch (Exception e) {
			throw new Spiderman.Exception("", e);
		}
		pageList.setExtractorBuilder((t, p, mds) -> {
			try {
				return ct2.newInstance(t, p, mds);
			} catch (Exception e) {
				throw new Spiderman.Exception("", e);
			}
		});
		
		cnf.addPage(page);
		//cnf.addPage(pageList);
		System.out.println(conf.getDownloadListener());
		new Spiderman(cnf).go();
		//new Spiderman(conf).go();//启动，别忘记看控制台信息哦，结束之后会有统计信息的
	}

}
