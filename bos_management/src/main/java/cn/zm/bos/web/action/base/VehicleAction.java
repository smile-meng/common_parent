package cn.zm.bos.web.action.base;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.base.Vehicle;
import cn.zm.bos.service.base.VehicleService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class VehicleAction extends BaseAction<Vehicle>{
	
	@Autowired
	private VehicleService vehicleService;
	
	@Action(value="vehicle_save",results={@Result(name="success",type="redirect",location="./pages/base/vehicle.html")})
	public String save(){
		vehicleService.save(model);
		return SUCCESS;
	}
}
