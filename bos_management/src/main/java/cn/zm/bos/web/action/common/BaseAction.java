package cn.zm.bos.web.action.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class BaseAction<T> extends ActionSupport implements ModelDriven<T> {
	
	protected T model;
	
	@Override
	public T getModel() {
		return model;
	}
	//构造器 完成model实例化
	public BaseAction(){
		//构造子类Action对象，获取继承父类型的泛型
		//AreaAction extends BaseAction<Area>
		//BaseAction<Area>
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		//获取类型第一个泛型参数
		ParameterizedType  parameterizedType =  (ParameterizedType) genericSuperclass;
		Class<T> modelClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
		
		try {
			model = modelClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("模型构造失败...");
		} 
	}
	
	protected Integer page;// 当前的页码
	protected Integer rows;// 每页的条数

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
	//将分页查询的结果集数据，压入栈顶
	protected void pushPageDateToValueStack(Page<T> page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());

		// 将map集合转换为json数据返回的页面
		ActionContext.getContext().getValueStack().push(map);
	}
	
}
