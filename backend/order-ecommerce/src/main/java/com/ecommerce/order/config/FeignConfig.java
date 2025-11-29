package com.ecommerce.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author amoghavarshakm
 */
@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();

			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();

				forwardHeader(request, requestTemplate, "Authorization");
				forwardHeader(request, requestTemplate, "X-User-Id");
				forwardHeader(request, requestTemplate, "X-Username");
			}
		};
	}

	private void forwardHeader(HttpServletRequest request, RequestTemplate template, String header) {
		String headerValue = request.getHeader(header);
		if (headerValue != null) {
			template.header(header, headerValue);
		}
	}
}
