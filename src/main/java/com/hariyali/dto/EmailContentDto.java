/**
 * 
 */
package com.hariyali.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmailContentDto {
	private String fromAddress;
    private List<String> toAddress;
    private List<String> ccAddress;
    private List<String> bccAddress;
    private String subject;
    private String message;
    private List<MultipartFile> attachments;
    private String appId;
    private boolean filtering;
    private String title;
}
