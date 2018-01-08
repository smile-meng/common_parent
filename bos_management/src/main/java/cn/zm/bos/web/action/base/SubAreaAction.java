package cn.zm.bos.web.action.base;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.base.Area;
import cn.zm.bos.domain.base.FixedArea;
import cn.zm.bos.domain.base.SubArea;
import cn.zm.bos.service.base.AreaService;
import cn.zm.bos.service.base.FixedAreaService;
import cn.zm.bos.service.base.SubAreaService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Actions
@Controller
@Scope("prototype")
public class SubAreaAction extends BaseAction<SubArea> {

	@Autowired
	private SubAreaService subAreaService;

	// 添加或修改分区
	@Action(value = "subArea_save", results = { @Result(name = "success", type = "redirect", location = "pages/base/sub_area.html") })
	public String save() {
		subAreaService.save(model);
		return SUCCESS;
	}

	// 有无条件分页查询
	@Action(value = "subArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		Pageable pageable = new PageRequest(page - 1, rows);

		Specification<SubArea> specification = new Specification<SubArea>() {

			@Override
			public Predicate toPredicate(Root<SubArea> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 将查询的Predicate添加到集合中
				List<Predicate> list = new ArrayList<Predicate>();
				// 单表查询
				// 关键字模糊查询
				if (StringUtils.isNotBlank(model.getKeyWords())) {
					Predicate p1 = cb.like(root.get("keyWords")
							.as(String.class), "%" + model.getKeyWords() + "%");
					list.add(p1);
				}
				// 多表关联
				// 使用SubArea(Root),关联fixedArea 进行定区的编码进行模糊查询
				if (model.getFixedArea() != null
						&& StringUtils.isNotBlank(model.getFixedArea().getId())) {
					Join<Object, Object> subAreaRoot = root.join("fixedArea",
							JoinType.INNER);// 设置内连接进行关联
					Predicate p2 = cb.equal(
							subAreaRoot.get("id").as(String.class), model
									.getFixedArea().getId());
					list.add(p2);
				}
				// 使用subArea(Root),关联Area进行区域的省市区县进行模糊查询
				Join<Object, Object> areaRoot = root.join("area",
						JoinType.INNER);// 设置内连接进行关联
				// 省模糊查询
				if (model.getArea() != null
						&& StringUtils
								.isNotBlank(model.getArea().getProvince())) {

					Predicate p3 = cb.like(
							areaRoot.get("province").as(String.class), "%"
									+ model.getArea().getProvince() + "%");
					list.add(p3);
				}
				// 市模糊查询
				if (model.getArea() != null
						&& StringUtils.isNotBlank(model.getArea().getCity())) {
					Predicate p4 = cb.like(areaRoot.get("city")
							.as(String.class), "%" + model.getArea().getCity()
							+ "%");
					list.add(p4);
				}
				// 区（县）模糊查询
				if (model.getArea() != null
						&& StringUtils
								.isNotBlank(model.getArea().getDistrict())) {
					Predicate p5 = cb.like(
							areaRoot.get("district").as(String.class), "%"
									+ model.getArea().getDistrict() + "%");
					list.add(p5);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		Page<SubArea> page = subAreaService.pageQuery(specification, pageable);

		pushPageDateToValueStack(page);

		return SUCCESS;
	}

	private String ids;// 要进行删除的分区ID

	public void setIds(String ids) {
		this.ids = ids;
	}

	// 批量删除分区
	@Action(value = "subArea_delBatch", results = { @Result(name = "success", type = "redirect", location = "pages/base/sub_area.html") })
	public String delBatch() {
		String[] idStrs = ids.split(",");
		subAreaService.delBatch(idStrs);
		return SUCCESS;
	}

	private File upload;

	public void setUpload(File upload) {
		this.upload = upload;
	}

	// 一键导入功能
	@Action(value = "subArea_batchImport")
	public String batchImport() throws Exception {
		List<SubArea> list = new ArrayList<SubArea>();
		// 创建解析excel文件的对象
		// xlsx格式解析式需要XSSF
		// xls格式解析需要HSSF
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(upload);
		// 找到工作簿
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
		// 读取每一行
		for (Row row : xssfSheet) {
			// 判断第一行是名称跳过
			if (row.getRowNum() == 0) {
				continue;
			}
			// 跳过行
			if (row.getCell(0) == null
					|| StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				continue;
			}
			SubArea subArea = new SubArea();
			subArea.setId(row.getCell(0).getStringCellValue());
			FixedArea fixedArea = new FixedArea();
			fixedArea.setId(row.getCell(1).getStringCellValue());
			subArea.setFixedArea(fixedArea);
			Area area = new Area();
			area.setId(row.getCell(2).getStringCellValue());
			subArea.setArea(area);
			subArea.setKeyWords(row.getCell(3).getStringCellValue());
			subArea.setStartNum(row.getCell(4).getStringCellValue());
			subArea.setEndNum(row.getCell(5).getStringCellValue());
			subArea.setSingle(row.getCell(6).getStringCellValue().charAt(0));
			subArea.setAssistKeyWords(row.getCell(7).getStringCellValue());

			list.add(subArea);
		}
		subAreaService.batchImport(list);
		return NONE;
	}

	// 一键导出
	@Action(value = "subArea_batchExport")
	public String batchExport() throws Exception {

		// 查询要导出的数据
		List<SubArea> areas = subAreaService.findAll();
		// 创建工作簿
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		// 创建一个sheet
		HSSFSheet sheet = hssfWorkbook.createSheet("区域信息");
		// 创建一个行
		HSSFRow row = sheet.createRow(0);
		// 创建Cell---标题
		row.createCell(0).setCellValue("分区编号");
		row.createCell(1).setCellValue("定区编码");
		row.createCell(2).setCellValue("区域编码");
		row.createCell(3).setCellValue("关键字");
		row.createCell(4).setCellValue("起始号");
		row.createCell(5).setCellValue("结束号");
		row.createCell(6).setCellValue("单双号");
		row.createCell(7).setCellValue("位置信息");
		// 遍历areas创建行数据
		for (int i = 0; i < areas.size(); i++) {
			SubArea subArea = areas.get(i);
			HSSFRow temp = sheet.createRow(i + 1);
			temp.createCell(0).setCellValue(subArea.getId());
			temp.createCell(1).setCellValue(subArea.getFixedArea().getId());
			temp.createCell(2).setCellValue(subArea.getArea().getId());
			temp.createCell(3).setCellValue(subArea.getKeyWords());
			temp.createCell(4).setCellValue(subArea.getStartNum());
			temp.createCell(5).setCellValue(subArea.getEndNum());
			temp.createCell(6).setCellValue(subArea.getSingle());
			temp.createCell(7).setCellValue(subArea.getAssistKeyWords());
		}

		// 提供页面下载
		OutputStream out = ServletActionContext.getResponse().getOutputStream();
		// 设置文件名称
		String filename = "分区信息.xls";

		filename = new String(filename.getBytes(), "ISO-8859-1");
		ServletActionContext.getResponse().setContentType(
				"application/vnd.ms-excel;charset=utf-8");

		// 以附件的方式下载文件
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment;filename=" + filename);
		hssfWorkbook.write(out);

		return NONE;
	}

}
