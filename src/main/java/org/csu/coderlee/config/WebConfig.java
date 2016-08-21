package org.csu.coderlee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.xml.transform.Source;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.csu.coderlee.

/**
 * Created by lixiang on 16/8/21.
 */
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ByteArrayHttpMessageConverter());
        ClassLoader classLoader = getClass().getClassLoader();
        if (ClassUtils.isPresent("javax.xml.bind.Binder", classLoader)) {
            converters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)) {
            converters.add(jsonMessageConverter());
        }
        else if (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", classLoader)) {
            converters.add(new MappingJacksonHttpMessageConverter());
        }

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);

        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<Source>());
        converters.add(new XmlAwareFormHttpMessageConverter());
        /*
        if (ClassUtils.isPresent("com.sun.syndication.feed.WireFeed", classLoader)) {
            converters.add(new AtomFeedHttpMessageConverter());
            converters.add(new RssChannelHttpMessageConverter());
        }
        */
    }

    private MappingJackson2HttpMessageConverter jsonMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

        // Single-purpose return value types
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(getMessageConverters()));

        // Annotation-based return value types
        handlers.add(new ModelAttributeMethodProcessor(false));
        handlers.add(new NullableRequestResponseBodyMethodProcessor(getMessageConverters()));

        // Multi-purpose return value types
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());


        // Catch-all
        if (!CollectionUtils.isEmpty(adapter.getModelAndViewResolvers())) {
            handlers.add(new ModelAndViewResolverMethodReturnValueHandler(adapter.getModelAndViewResolvers()));
        }
        else {
            handlers.add(new ModelAttributeMethodProcessor(true));
        }

        adapter.setReturnValueHandlers(handlers);
        return adapter;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver=new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setPathMatcher(new CaseInsensitiveAntPathMatcher());
        return handlerMapping;
    }
}
