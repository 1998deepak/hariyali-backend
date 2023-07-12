//package com.hariyali.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.entity.FileContent;
//import com.hariyali.service.FileContentService;
//
//import org.springframework.http.MediaType;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//@RestController
//@RequestMapping("/api/v1")
//public class FileContentController {
//
//private static final Logger logger = LoggerFactory.getLogger(FileContentController.class);
//	
//	@Autowired
//	private FileContentService fileContentService;
//	
//	// Create an api to Upload doc
//			@PostMapping("/uploadFileDocument")
//			public ResponseEntity<Object> uploadBulletinDocument(@RequestPart("file") MultipartFile file) {
//				ApiResponse<FileContent> res = new ApiResponse<FileContent>();
//				res = fileContentService.saveFile(file);
//				if (res == null) 
//				{
//					String message = "File Document not found with id ";
//					String details = "Please check Document id";
//					logger.error(message + "," + details);
//					res.setStatus("ERROR");
//					res.setMessage("Something went Wrong ");
//					res.setStatusCode(HttpStatus.BAD_REQUEST);
//					return new ResponseEntity<>(res, res.getStatusCode());
//				}
//				
//				return new ResponseEntity<>(res, res.getStatusCode());
//			}
//			
//			
//			// Create an api to Get the all Bulletin Document
//			@GetMapping("/getFileDocuments")
//			public ResponseEntity<Object> getFileDocuments(@RequestParam(defaultValue = "1") Integer pageNo,
//				@RequestParam(defaultValue = "2") Integer pageSize) {
//				ApiResponse<Map<String, Object>> file =this.fileContentService.getAllFileDocument(pageNo, pageSize);
//				return new ResponseEntity<>(file, file.getStatusCode());
//			}	
//			
//
//			// Create an api to Downlod the  Bulletin Document
//			@GetMapping(value = "/downloadFileDocument/{fileId}", produces = MediaType.ALL_VALUE)
//			public ResponseEntity<Object> downloadBulletinDoc(@PathVariable Integer fileId) {
//				ApiResponse<FileContent> res = new ApiResponse<FileContent>();
//				FileContent file = this.fileContentService.getFileDocumentByDocId(fileId);
//				if (file == null) {
//					res.setStatus("ERROR");
//					res.setMessage("Something went Wrong ");
//					res.setStatusCode(HttpStatus.BAD_REQUEST);
//					return new ResponseEntity<>(res, res.getStatusCode());
//				}
//				try {
//					String filePath = file.getFilePath();
//					File fileContent = new File(filePath);
//					System.out.println(fileContent);
//					InputStreamResource resource = new InputStreamResource(new FileInputStream(fileContent));
//					HttpHeaders headers = new HttpHeaders();
//					headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileContent.getName()));
//					headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//					headers.add("Pragma", "no-cache");
//					headers.add("Expires", "0");
//					ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(fileContent.length())
//							.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
//					return responseEntity;
//				} catch (Exception e) {
//					return new ResponseEntity<>(" File Document Not Found in Specified Location", HttpStatus.NOT_FOUND);
//				}
//			}
//}
