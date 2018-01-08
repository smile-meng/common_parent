package cn.zm.bos.freemarker.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerTest {
	
	@Test
	public void testOutput() throws Exception{
		//配置对象，配置模板位置
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
		configuration.setDirectoryForTemplateLoading(new File("src/main/webapp/WEB-INF/templates"));
		
		//获取模板对象
		Template template = configuration.getTemplate("hello.ftl");
		
		//动态数据对象
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("title", "美好人生");
		map.put("msg", "你好，javaee");
		
		//合并输出
		template.process(map, new PrintWriter(System.out));
	}
}
