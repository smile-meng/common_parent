package cn.zm.bos.web.action.base;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.base.Area;
import cn.zm.bos.service.base.AreaService;
import cn.zm.bos.utils.PinYin4jUtils;
import cn.zm.bos.web.action.common.BaseAction;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class AreaAction extends BaseAction<Area> {

	@Autowired
	private AreaService areaService;

	private String ids; // 批量删除时所用的ids

	public void setIds(String ids) {
		this.ids = ids;
	}

	@Action(value = "area_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/area.html") })
	public String save() {
		areaService.save(model);
		return SUCCESS;
	}

	@Action(value = "area_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		// 条件查询
		Specification<Area> specification = new Specification<Area>() {

			@Override
			public Predicate toPredicate(Root root, CriteriaQuery query,
					CriteriaBuilder cb) {

				List<Predicate> list = new ArrayList<Predicate>();

				if (StringUtils.isNotBlank(model.getProvince())) {
					Predicate p1 = cb.like(root.get("province")
							.as(String.class), "%" + model.getProvince() + "%");
					list.add(p1);
				}
				if (StringUtils.isNotBlank(model.getCity())) {
					Predicate p2 = cb.like(root.get("city").as(String.class),
							"%" + model.getCity() + "%");
					list.add(p2);
				}
				if (StringUtils.isNotBlank(model.getDistrict())) {
					Predicate p3 = cb.like(root.get("district")
							.as(String.class), "%" + model.getDistrict() + "%");
					list.add(p3);
				}

				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		// 分页查询
		Pageable pageable = new PageRequest(page - 1, rows);
		Page<Area> page = areaService.pageQuery(specification, pageable);

		pushPageDateToValueStack(page);

		return SUCCESS;
	}

	@Action(value = "area_delBatch", results = { @Result(name = "success", type = "redirect", location = "./pages/base/area.html") })
	public String delBatch() {
		// 将前台返回的ids进行分割
		String[] idStrs = ids.split(",");
		// 调用业务层进行删除
		areaService.delBatch(idStrs);
		return SUCCESS;
	}

	// 一键上传的文件
	private File upload;

	public void setUpload(File upload) {
		this.upload = upload;
	}

	// 一键导入文件
	@Action(value = "area_batchImport")
	public String batchImport() throws Exception {
		List<Area> list = new ArrayList<Area>();
		// 基于.xls格式解析HSSF
		// xlsx格式解析XSSF
		// 1.加载Excel文件对象
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(upload);
		// 2.读取一个sheet
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		// 3.读取sheet每一行
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				// 第一行是名称，需要跳过
				continue;
			}
			// 跳过空行
			// row.getCell(0).第一列的属性
			// row.getCell(0).getStringCellValue())第一列的值
			if (row.getCell(0) == null
					|| StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				continue;
			}
			Area area = new Area();
			area.setId(row.getCell(0).getStringCellValue());
			area.setProvince(row.getCell(1).getStringCellValue());
			area.setCity(row.getCell(2).getStringCellValue());
			area.setDistrict(row.getCell(3).getStringCellValue());
			area.setPostcode(row.getCell(4).getStringCellValue());

			// 基于pinyin4j生成城市编码和简码
			String province = area.getProvince();
			String city = area.getCity();
			String district = area.getDistrict();
			province.substring(0, province.length() - 1);// 将省字截掉
			city.substring(0, city.length() - 1); // 将市字去掉
			district.substring(0, district.length() - 1);// 将区/县去掉

			// 生成简码
			String[] headByString = PinYin4jUtils.getHeadByString(province
					+ city + district);
			StringBuffer buffer = new StringBuffer();
			for (String str : headByString) {
				buffer.append(str);
			}
			area.setShortcode(buffer.toString());
			// 生成城市简码
			String citycode = PinYin4jUtils.hanziToPinyin(province, "");
			area.setCitycode(citycode);

			list.add(area);

		}
		// 4.
		areaService.batchImport(list);
		return null;
	}

	// 一键导出
	@Action(value = "area_batchExport")
	public String batchExport() throws Exception {

		// 查询要导出的数据
		List<Area> areas = areaService.findAll();
		// 创建工作簿
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		// 创建一个sheet
		HSSFSheet sheet = hssfWorkbook.createSheet("区域信息");
		// 创建一个行
		HSSFRow row = sheet.createRow(0);
		// 创建Cell---标题
		row.createCell(0).setCellValue("编号");
		row.createCell(1).setCellValue("省份");
		row.createCell(2).setCellValue("城市");
		row.createCell(3).setCellValue("区域");
		row.createCell(4).setCellValue("邮编");
		// 遍历areas创建行数据
		for (int i = 0; i < areas.size(); i++) {
			Area area = areas.get(i);
			HSSFRow temp = sheet.createRow(i + 1);
			temp.createCell(0).setCellValue(area.getId());
			temp.createCell(1).setCellValue(area.getProvince());
			temp.createCell(2).setCellValue(area.getCity());
			temp.createCell(3).setCellValue(area.getDistrict());
			temp.createCell(4).setCellValue(area.getPostcode());
		}

		// 提供页面下载
		OutputStream out = ServletActionContext.getResponse().getOutputStream();
		// 设置文件名称
		String filename = "区域信息.xls";

		filename = new String(filename.getBytes(), "ISO-8859-1");
		ServletActionContext.getResponse().setContentType(
				"application/vnd.ms-excel;charset=utf-8");

		// 以附件的方式下载文件
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment;filename=" + filename);
		hssfWorkbook.write(out);

		return NONE;
	}

	// 查询所有的省
	@Action(value = "queryProvince", results = { @Result(name = "success", type = "json") })
	public String findAllProvince() {
		List<String> provinces = areaService.findAllProvince();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (String province : provinces) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("province", province);
			list.add(map);
		}
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}

	// 根据省查询市
	@Action(value = "queryCity", results = { @Result(name = "success", type = "json") })
	public String findCityByProvince() {
		List<String> cities = areaService.findCityByProvince(model
				.getProvince());
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (String city : cities) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("city", city);
			list.add(map);
		}
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}

	// 根据市查询区（县）
	@Action(value = "queryDistrict", results = { @Result(name = "success", type = "json") })
	public String findDistrictByCity() {
		List<Area> area = areaService.findDistrictByCity(model.getCity());
		ActionContext.getContext().getValueStack().push(area);
		return SUCCESS;
	}
}
