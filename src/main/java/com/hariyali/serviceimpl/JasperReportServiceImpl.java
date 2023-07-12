//package com.hariyali.serviceimpl;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ResourceUtils;
//
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.dto.DonorInfoRequest;
//import com.hariyali.service.DonationCertificateInterface;
//import com.hariyali.service.JasperReportService;
//import com.hariyali.service.TransactionService;
//
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperCompileManager;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import com.hariyali.Enum;
//@Service
//public class JasperReportServiceImpl implements JasperReportService{
//
//	@Autowired
//	private TransactionService transactionservice;
//	
//	@Autowired
//	private DonationCertificateInterface donationCertificateInterface;
//	
//	
//	@Override
//	public ApiResponse<String> generatePdf(String reciptNo) throws FileNotFoundException, JRException {
//		String title;
//		double value;
//		System.out.println("hhh");
//		
//		String path = "C:\\Users\\DELL\\Desktop\\download report";
//
//		List<DonorInfoRequest> donorInfoRequest = transactionservice.getAllReportDataByReciptNo(reciptNo);
//		
//		System.out.println(donorInfoRequest);
//		System.out.println(donorInfoRequest.get(0).getWishAmount());
//		
//		if(donorInfoRequest.get(0).getWishAmount()==0.0)
//		{
//			 title="Package Price";
//			 value=donorInfoRequest.get(0).getPackagePrice();
//		}else {
//			title="Wish Amount";
//			value=donorInfoRequest.get(0).getWishAmount();
//		}
//		
//		File file = ResourceUtils.getFile("D:\\Hariyali23_2_23\\hariyali-project\\hariyali-project\\src\\main\\resources\\HariyaLandscape.jrxml");
//		
//		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
//		
//		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(donorInfoRequest);
//		
//		Map<String,Object> parameters = new HashMap<>();
//		parameters.put("title",title);
//		parameters.put("value",value);
//
//
//		JasperPrint jasperPrint=JasperFillManager.fillReport(jasperReport, parameters, dataSource);
//		
//		JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\invoice9.pdf");
//		
//		
//		
//	}
//
//	@Override
//	public ApiResponse<String> generateCertificate(HttpServletRequest request,String certiNo) throws FileNotFoundException, JRException {
//
//		String path = "C:\\Users\\DELL\\Desktop\\download certificate";
//		
//		
//		List<DonorInfoRequest> donorInfoRequest = donationCertificateInterface.getCertificateByNumber(userId, certiNo);
//		
//		System.out.println(donorInfoRequest);
//		String paymentHolderName=donorInfoRequest.get(0).getPaymentHolderName();
//		String donarAddress=donorInfoRequest.get(0).getDonarAddress();
//		Date donationDate=donorInfoRequest.get(0).getDonationDate();
//		//String certificateNo =donorInfoRequest.get(0).getCertificateNo();
//		double amountPaid = donorInfoRequest.get(0).getAmountPaid();
//		File file = ResourceUtils.getFile("D:\\Hariyali23_2_23\\hariyali-project\\hariyali-project\\src\\main\\resources\\\\Certificate_Landscape.jrxml");
//		
//		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
//		
//		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(donorInfoRequest);
//
//		Map<String,Object> parameters = new HashMap<>();
//		parameters.put("paymentHolderName",paymentHolderName);
//		parameters.put("donarAddress",donarAddress);
//		parameters.put("donationDate", donationDate);
//		parameters.put("amountPaid", amountPaid);
//		
//		JasperPrint jasperPrint=JasperFillManager.fillReport(jasperReport, parameters, dataSource);
//		
//		JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\certi"+paymentHolderName+""+certiNo+".pdf");
//	}
//
//}
