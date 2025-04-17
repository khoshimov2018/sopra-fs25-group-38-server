package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${spring.servlet.multipart.max-file-size:10MB}")
  private String maxFileSize;

  @Value("${spring.servlet.multipart.max-request-size:10MB}")
  private String maxRequestSize;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000", // Local development frontend
                "https://sopra-fs25-group-38-client.vercel.app" // Production frontend
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Content-Type")
            .allowCredentials(false) // Set to false since we're using JWT tokens, not cookies
            .maxAge(3600); // Cache preflight requests for 1 hour
  }
  
  /**
   * Configure multipart handling for larger profile picture uploads
   */
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.parse(maxFileSize));
    factory.setMaxRequestSize(DataSize.parse(maxRequestSize));
    return factory.createMultipartConfig();
  }
  
  @Bean
  public org.springframework.web.filter.CommonsRequestLoggingFilter requestLoggingFilter() {
    org.springframework.web.filter.CommonsRequestLoggingFilter loggingFilter = new org.springframework.web.filter.CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setMaxPayloadLength(64000);
    return loggingFilter;
  }
  
  /**
   * Configure the JSON message converter to handle larger payloads
   */
  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setDefaultCharset(java.nio.charset.StandardCharsets.UTF_8);
    return converter;
  }
}
