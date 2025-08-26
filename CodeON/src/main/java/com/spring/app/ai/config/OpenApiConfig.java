package com.spring.app.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class OpenApiConfig {
	
	@Value("classpath:/prompt.txt")
	private Resource resource;
	
   @Bean
   public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
	   
//       return chatClientBuilder.defaultSystem("당신은 그룹웨어 관리자 입니다. 문서/파일 요약을 명확하고 간단하게 설명하세요").build();
//	   return chatClientBuilder.defaultSystem(resource).build();
	   return chatClientBuilder.build();
   }

   
   
}
