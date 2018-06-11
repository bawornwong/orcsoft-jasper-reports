package jasper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRExporterParameter;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.JasperRunManager;
//import net.sf.jasperreports.engine.export.JRPdfExporter;
//import net.sf.jasperreports.engine.util.JRLoader;

@Controller
public class JasperController {

	@Autowired
	JdbcTemplate jdbcTemplate; 
	
	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		
		return "welcome";
	}

	@RequestMapping("/export")
	public ResponseEntity<ByteArrayResource> export(HttpSession session) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("message", this.message);
		
		String reportName = "Orcsoft2018";
		byte[] bytes = generateReport(reportName, params, jdbcTemplate.getDataSource().getConnection());
		ByteArrayResource resource = new ByteArrayResource(bytes);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=\"" + reportName + ".pdf\"");

		return ((ResponseEntity.BodyBuilder) ResponseEntity.ok().headers(headers)).contentLength(bytes.length)
				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);
	}

	private byte[] generateReport(String reportName, Map<String, Object> params, Connection connection) throws Exception {
		byte[] bytes = null;
		try {
			// Get Compiled Report File
			File reportFile = new File("D:\\OrcsoftInternalTraining2018\\JasperReports\\orcsoft.jasper");
			
			/*FileInputStream inputStream = new FileInputStream(reportFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, connection);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();*/
		    
			/*
		    // 1. JRPdfExporter
		    JRPdfExporter exporter = new JRPdfExporter();
		    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportName + ".pdf");
		    exporter.exportReport();
		    */
		    
		    /*
		    // 2. JasperExportManager
		    JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
			bytes = baos.toByteArray();
			*/
			
			JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportFile.getPath());
			bytes = JasperRunManager.runReportToPdf(jasperReport, params, new JREmptyDataSource());
//			Connection conn = DriverManager.getConnection("jdbc:h2:mem:orcsoftH2");
//			bytes = JasperRunManager.runReportToPdf(jasperReport, params, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
}