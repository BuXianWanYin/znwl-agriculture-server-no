//package com.frog.IaAgriculture.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.List;
//
//public class
//WebMvcConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 指定路径
//                .allowedOrigins("*") // 允许指定域进行跨域访问
//                .allowedHeaders("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .maxAge(3600);
//    }
//
////    @Override
////    public void addInterceptors(InterceptorRegistry registry) {
////        registry.addInterceptor(this.authenticationInterceptor())
////                .excludePathPatterns("/doc.html")
////                .excludePathPatterns("/user/login")
////                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/**/favicon.ico", "/iaPartitionFoodSensorValue/**").addPathPatterns("/**");
////    }
////
////    @Bean
////    public AuthInterceptor authenticationInterceptor() {
////        return new AuthInterceptor();
////    }
//
//    /**
//     * 扩展mvc框架的消息转换器
//     *
//     * @param converters
//     */
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //创建消息转换器对象
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        //设置对象转换器，底层使用Jackson将Java对象转为json
//        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        //将上面的消息转换器对象追加到mvc框架的转换器集合中
//        converters.add(0, messageConverter);
//    }
//
//}
