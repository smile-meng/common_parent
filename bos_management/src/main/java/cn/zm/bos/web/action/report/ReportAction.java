package cn.zm.bos.web.action.report;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.take_delivery.WayBill;
import cn.zm.bos.service.take_delivery.WayBillService;
import cn.zm.bos.utils.FileUtils;
import cn.zm.bos.web.action.common.BaseAction;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ReportAction extends BaseAction<WayBill> {

    @Autowired
    private WayBillService wayBillService;
    
    //导出Excel报表
    @Action(value = "report_exportXls")
    public String exportXls() throws IOException {
        //查出满足条件的数据
        try {
            List<WayBill> wayBills = wayBillService.findWayBills(model);

            //生成Excel
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
//        HSSFSheet sheet = hssfWorkbook.createSheet("运单数据");
            //创建工作簿
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet sheet = xssfWorkbook.createSheet("运单数据");
            //创建Row
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("运单号");
            row.createCell(1).setCellValue("寄件人");
            row.createCell(2).setCellValue("寄件人电话");
            row.createCell(3).setCellValue("寄件人地址");
            row.createCell(4).setCellValue("收件人");
            row.createCell(5).setCellValue("收件人电话");
            row.createCell(6).setCellValue("收件人地址");

            for (WayBill wayBill : wayBills) {
                XSSFRow xssfRow = sheet.createRow(sheet.getLastRowNum()+1);
                xssfRow.createCell(0).setCellValue(wayBill.getWayBillNum());
                xssfRow.createCell(1).setCellValue(wayBill.getSendName());
                xssfRow.createCell(2).setCellValue(wayBill.getSendMobile());
                xssfRow.createCell(3).setCellValue(wayBill.getSendAddress());
                xssfRow.createCell(4).setCellValue(wayBill.getRecName());
                xssfRow.createCell(5).setCellValue(wayBill.getRecMobile());
                xssfRow.createCell(6).setCellValue(wayBill.getRecAddress());
            }

            //下载导出
            //设置头信息
            ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
            String filename = "运单数据.xlsx";
            String agent = ServletActionContext.getRequest().getHeader("user-agent");
            filename = FileUtils.encodeDownloadFilename(filename, agent);
            //处理中文乱码
            //String filename = new String(filename.getBytes(),"ISO-8859-1");
            ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename="+filename);



            ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();

            xssfWorkbook.write(outputStream);

            //关闭
            xssfWorkbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NONE;
    }
    
    
    //导出PDF报表
    @Action(value="report_exportPdf")
    public String exportPdf() throws Exception{
    	//查询出满足当前条件结果数据
    	List<WayBill> wayBills = wayBillService.findWayBills(model);
    	
    	//下载导出
    	//设置头信息
    	ServletActionContext.getResponse().setContentType("application/pdf");
    	String filename = "运单数据.pdf";
    	filename = new String(filename.getBytes(),"ISO-8859-1");
    	ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename="+filename);
    	
    	//生成PDF文件
    	Document document = new Document();
    	ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
    	PdfWriter.getInstance(document, outputStream);
    	document.open();
    	
    	//写PDF数据
    	//想document 生成pdf表格
    	Table table = new Table(7);
    	table.setWidth(80);	//宽度
    	table.setBorder(1); //边框
    	table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);//水平对齐方式
    	table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); //垂直对齐方式
    	
    	//设置表格字体
    	BaseFont baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
    	Font font = new Font(baseFont,10,Font.NORMAL,Color.RED);
    	
    	//写表头
    	table.addCell(buildCell("运单号",font));
    	table.addCell(buildCell("寄件人", font));
		table.addCell(buildCell("寄件人电话", font));
		table.addCell(buildCell("寄件人地址", font));
		table.addCell(buildCell("收件人", font));
		table.addCell(buildCell("收件人电话", font));
		table.addCell(buildCell("收件人地址", font));

		// 表格数据
		for (WayBill wayBill : wayBills) {
			table.addCell(buildCell(wayBill.getWayBillNum(), font));
			table.addCell(buildCell(wayBill.getSendName(), font));
			table.addCell(buildCell(wayBill.getSendMobile(), font));
			table.addCell(buildCell(wayBill.getSendAddress(), font));
			table.addCell(buildCell(wayBill.getRecName(), font));
			table.addCell(buildCell(wayBill.getRecMobile(), font));
			table.addCell(buildCell(wayBill.getRecAddress(), font));
		}

		// 向文档添加表格
		document.add(table);
		
		document.close();
    	
    	return NONE;
    }


    private Cell buildCell(String content, Font font) throws BadElementException {
		Phrase phrase = new Phrase(content, font);
		return new Cell(phrase);
	}
    
    
    //结合模板导出PDF文件
    @Action(value="report_exportJasperPdf")
    public String exportJasperPdf() throws Exception{
    	//查询出满足当前条件结果数据
    	List<WayBill> wayBills = wayBillService.findWayBills(model);
    	
    	//下载导出
    	//设置头信息
    	ServletActionContext.getResponse().setContentType("application/pdf");
    	String filename = "运单数据.pdf";
    	filename = new String(filename.getBytes(),"ISO-8859-1");
    	ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename="+filename);
    	
    	//根据jasperReport模板 生成pdf
    	String jrxml = ServletActionContext.getServletContext().getRealPath("/WEB-INF/jasper/waybill.jrxml");
    	JasperReport report = JasperCompileManager.compileReport(jrxml);
    	
    	//设置模板数据
    	//Parameter变量
    	Map<String,Object> parameters = new HashMap<String, Object>();
    	parameters.put("company", "腾讯");
    	//Field变量
    	JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters,new JRBeanCollectionDataSource(wayBills));
    	
    	System.out.println(wayBills);
    	
    	//生成PDF客户端
    	JRPdfExporter exporter = new JRPdfExporter();
    	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
    	exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ServletActionContext.getResponse().getOutputStream());
    	
    	exporter.exportReport();//导出
    	
    	ServletActionContext.getResponse().getOutputStream().close();
    	return NONE;
    }
}
