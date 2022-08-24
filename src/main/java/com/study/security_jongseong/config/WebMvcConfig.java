package com.study.security_jongseong.config;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	@Value("${file.path}")
	private String filePath;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) { // 
		WebMvcConfigurer.super.addResourceHandlers(registry);
		registry.addResourceHandler("/image/**") // 이게 img의 src 경로가 매우 길어지기 때문에 밑의 filepath를 옆에 있는 걸로 대체 하겠다. 
		.addResourceLocations("file:///" + filePath)
		.setCachePeriod(60*60)
		.resourceChain(true)
		.addResolver(new PathResourceResolver() {
			@Override
			protected Resource getResource(String resourcePath, Resource location) throws IOException {
				resourcePath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8);
				return super.getResource(resourcePath, location);
			}
		});
	}
}
