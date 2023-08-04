//package com.hariyali.controller;
//import java.util.List;
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.dto.StoriesRequest;
//import com.hariyali.service.StoriesService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@RequestMapping("/api/v1")
//public class StoriesController {
//	private static final Logger logger = LoggerFactory.getLogger(StoriesController.class);
//
//	@Autowired
//	private StoriesService storiesService;
//
//	
//	// delete user story
//	@DeleteMapping("/deleteStories/{storyId}")
//	public ResponseEntity<ApiResponse<StoriesRequest>> deleteUser(@PathVariable String storyId) {
//
//		return new ResponseEntity<>(this.storiesService.deleteStory(Integer.parseInt(storyId)), HttpStatus.OK);
//	}
//
//	// Add Stories
//
//	@PostMapping("/AddStories")
//	public ResponseEntity<ApiResponse<StoriesRequest>> addStories(@RequestBody StoriesRequest storiesRequest,
//			HttpServletRequest request) {
//
//		return new ResponseEntity<>(this.storiesService.saveStory(storiesRequest, request), HttpStatus.OK);
//
//	}
//
//	@GetMapping("/getStoryDataByUserId")
//	public ResponseEntity<ApiResponse<List<StoriesRequest>>> getTransactionByUserId(HttpServletRequest request) {
//
//		return new ResponseEntity<>(this.storiesService.getAllStoryByUserId(request), HttpStatus.OK);
//
//	}
//}