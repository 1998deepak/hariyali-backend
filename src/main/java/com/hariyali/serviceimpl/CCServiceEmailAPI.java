/**
 * 
 */
package com.hariyali.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.dto.EmailContentDto;
import com.hariyali.exceptions.EmailNotConfiguredException;
import com.hariyali.exceptions.ExpiredEmailConfigurationUsageException;
import com.hariyali.exceptions.InactiveConfigurationUsageException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Service
@Slf4j
public class CCServiceEmailAPI {

	@Value("${cc_service.APP_ID}")
	String APP_ID;

	@Value("${cc_service.CC_BASE_URL}")
	String HOST;

	/* Send Mail */
	public void sendSupportMail(String toUser, String subject, String emailContent) throws EmailNotConfiguredException {
//		String mailBody = ("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + emailContent
//				+ "</body>\n" + "</html>");
		EmailContentDto emailContentDto = new EmailContentDto("support@hariyali.org.in",
				Arrays.asList(toUser.split(",")), null, null, subject, emailContent, null, null, true, null);
		try {
			sendEmail(emailContentDto, null);
		} catch (IOException e) {
			log.info(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	public void sendCorrespondenceMail(String toUser, String subject, String emailContent)
			throws EmailNotConfiguredException {
		String mailBody = ("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + emailContent
				+ "</body>\n" + "</html>");
		EmailContentDto emailContentDto = new EmailContentDto("correspondence@hariyali.org.in",
				Arrays.asList(toUser.split(",")), null, null, subject, mailBody, null, null, true, null);
		log.info("toUser:" + Arrays.asList(toUser.split(",")));
		try {
			sendEmail(emailContentDto, null);
		} catch (IOException e) {
			log.info(e.getMessage());
			throw new RuntimeException(e);			
		}

	}
	
	public void sendCorrespondenceMailwithAttachment(String toUser, String subject, String emailContent, File[] files)
			throws EmailNotConfiguredException {
		String mailBody = ("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + emailContent
				+ "</body>\n" + "</html>");
		EmailContentDto emailContentDto = new EmailContentDto("correspondence@hariyali.org.in",
				Arrays.asList(toUser.split(",")), null, null, subject, mailBody, null, null, true, null);
		log.info("toUser:" + Arrays.asList(toUser.split(",")));
		try {
			sendEmail(emailContentDto, files);
		} catch (IOException e) {
			log.info(e.getMessage());
			throw new RuntimeException(e);			
		}

	}

	public void sendPaymentsMail(String toUser, String subject, String emailContent, File[] files)
			throws EmailNotConfiguredException {
		String mailBody = ("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + emailContent
				+ "</body>\n" + "</html>");
		EmailContentDto emailContentDto = new EmailContentDto("payments@hariyali.org.in",
				Arrays.asList(toUser.split(",")), null, null, subject, mailBody, null, null, true, null);
		try {
			System.out.println(sendEmail(emailContentDto, files));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public Boolean sendEmail(EmailContentDto emailContentDto, File[] files)
			throws IOException, EmailNotConfiguredException {
//		String HOST = System.getenv("CC_BASE_URL");
//		String HOST = "https://ccservice-dev.m-devsecops.com";
		System.out.println("HOST IS " + HOST);
//		String APP_ID = System.getenv("APP_ID");
//		String APP_ID = "caf3ebf3-67c0-43f3-ad1a-7d5d3437d760";
		System.out.println("HOST IS " + HOST);
		emailContentDto.setAppId(APP_ID);
		ObjectMapper objectMapper = new ObjectMapper();
		if (files != null) {
			/**
			 * contains attachments
			 */
			HttpPost post = new HttpPost(HOST + "/emailservice/app/v1/send/attachment");
			StringBody stringBody = new StringBody(objectMapper.writeValueAsString(emailContentDto),
					ContentType.APPLICATION_JSON);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			Arrays.stream(files).parallel().forEach(file -> {
				builder.addPart("files", new FileBody(file));
			});
			builder.addPart("data", stringBody);
			HttpEntity entity = builder.build();
			post.setEntity(entity);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			CloseableHttpResponse response;
			try {
				response = httpClient.execute(post);
			} catch (IOException e) {
				log.info(e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
			return handleResponse(response);
		} else {
			/**
			 * without attachments
			 */
			StringEntity requestEntity = new StringEntity(objectMapper.writeValueAsString(emailContentDto),
					ContentType.APPLICATION_JSON);

			HttpPost postMethod = new HttpPost(HOST + "/emailservice/app/v1/send/");
			postMethod.setEntity(requestEntity);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			return handleResponse(httpClient.execute(postMethod));
		}
	}

	private Boolean handleResponse(CloseableHttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		String result;
		if (entity != null) {
			InputStream inputStream = entity.getContent();
//	            result = convertStreamToString(inputStream);
			result = EntityUtils.toString(entity);
			System.out.println("RESPONSE: " + result);
			inputStream.close();
			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				try {
					JSONObject jsonObject = new JSONObject(result);
					String errorType = String.valueOf(jsonObject.get("message"));
					if (errorType.equals("EmailNotConfiguredException")) {
						throw new EmailNotConfiguredException();
					} else if (errorType.equals("ExpiredEmailConfigurationUsageException")) {
						throw new ExpiredEmailConfigurationUsageException();
					} else if (errorType.equals("InactiveConfigurationUsageException")) {
						throw new InactiveConfigurationUsageException();
					} else {
						throw new Exception(errorType);
					}
				} catch (Exception e) {
					log.error("Exception = {}", e);
					throw new EmailNotConfiguredException(result);
				}
			}

		}
		return null;
	}

}
