//package com.hariyali.interceptor;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.stream.Collectors;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.HandlerMapping;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class HariyaliServiceInterceptors implements HandlerInterceptor{
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		long startTime =  System.currentTimeMillis();
//	    request.setAttribute("startTime", startTime);
//	    
//	    Map<String, String[]> requestParams = request.getParameterMap();
//	    Map<String, String> requestParamsMap = new HashMap<>();
//	    Map<String, String> headerMap = new HashMap<>();
//
//		Enumeration headerNames = request.getHeaderNames();
//		while (headerNames.hasMoreElements()) {
//			String key = (String) headerNames.nextElement();
//			String value = request.getHeader(key);
//			headerMap.put(key, value);
//		}
//		for(Entry<String, String[]> entry : requestParams.entrySet()) {
//			String key = entry.getKey();
//			String value = request.getParameter(entry.getKey());
//			requestParamsMap.put(key, value);
//		}
//	    final Map<String, String> pathVariables = (Map<String, String>) request
//                  .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//		
//		log.info("Headers :"+headerMap+" || "+"Path Variables :"+pathVariables+" || "+"RequestParams :"+requestParamsMap+" || "+"Request Body :"+ request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
//		return true;
//	}
//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//	}
//
//	@Override
//	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//			throws Exception {
//		
//	}
//
////	@Override
////	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
////			throws ServletException, IOException {
////		ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
////      ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);
////
////      // Execution request chain
////      filterChain.doFilter(req, resp);
////      
////      // Get Cache
////      byte[] responseBody = resp.getContentAsByteArray();
////                
////      // Finally remember to respond to the client with the cached data.
////      resp.copyBodyToResponse();
////      long startTime = (Long) request.getAttribute("startTime");
////		long endTime = System.currentTimeMillis();
////		long executeTime = endTime - startTime;
////      log.info("Response body ="+ new String(responseBody, StandardCharsets.UTF_8)+ " Total Reponse Time :"+executeTime+" ms");
////
////	}
//}
