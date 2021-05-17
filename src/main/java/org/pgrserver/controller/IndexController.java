/**
 * パッケージ名：org.pgrserver.controller
 * ファイル名  ：IndexController.java
 * 
 * @author mbasa
 * @since May 16, 2021
 */
package org.pgrserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 説明：
 *
 */
@Configuration
public class IndexController implements WebMvcConfigurer {

    /**
     * コンストラクタ
     *
     */
    public IndexController() {
    }

    @Value(value = "${server.allow.cors:false}")
    private boolean allowCORS;
    
    private final Logger logger = LoggerFactory.getLogger(IndexController.class);
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        logger.info("Allow CORS: " + allowCORS);
        
        if( allowCORS ) {
            registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.OPTIONS.name())
                .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, "accessToken", "CorrelationId", "source")
                .exposedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, "accessToken", "CorrelationId", "source")
                .maxAge(4800);
        }
    }
   
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

}
