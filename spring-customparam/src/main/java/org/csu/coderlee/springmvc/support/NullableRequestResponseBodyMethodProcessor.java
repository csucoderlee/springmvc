package org.csu.coderlee.springmvc.support;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.util.*;

/**
 * Created by lixiang on 16/8/21.
 */
public class NullableRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor{

    public NullableRequestResponseBodyMethodProcessor(
            List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException
    {
        mavContainer.setRequestHandled(true);
        writeWithMessageConverters(returnValue, returnType, webRequest);
    }

    @Override
    protected <T> void writeWithMessageConverters(T returnValue,
                                                  MethodParameter returnType,
                                                  ServletServerHttpRequest inputMessage,
                                                  ServletServerHttpResponse outputMessage)
            throws IOException, HttpMediaTypeNotAcceptableException {

        Class<?> returnValueClass = returnValue==null? String.class: returnValue.getClass();

        List<MediaType> acceptableMediaTypes = getAcceptableMediaTypes(inputMessage);
        List<MediaType> producibleMediaTypes = getProducibleMediaTypes(inputMessage.getServletRequest(), returnValueClass);

        Set<MediaType> compatibleMediaTypes = new LinkedHashSet<MediaType>();
        for (MediaType a : acceptableMediaTypes) {
            for (MediaType p : producibleMediaTypes) {
                if (a.isCompatibleWith(p)) {
                    compatibleMediaTypes.add(getMostSpecificMediaType(a, p));
                }
            }
        }
        if (compatibleMediaTypes.isEmpty()) {
            throw new HttpMediaTypeNotAcceptableException(producibleMediaTypes);
        }

        List<MediaType> mediaTypes = new ArrayList<MediaType>(compatibleMediaTypes);
        MediaType.sortBySpecificityAndQuality(mediaTypes);

        MediaType selectedMediaType = null;
        for (MediaType mediaType : mediaTypes) {
            if (mediaType.isConcrete()) {
                selectedMediaType = mediaType;
                break;
            }
            else if (mediaType.equals(MediaType.ALL) || mediaType.equals(MEDIA_TYPE_APPLICATION)) {
                selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            }
        }

        if (selectedMediaType != null) {
            selectedMediaType = selectedMediaType.removeQualityValue();
            for (HttpMessageConverter<?> messageConverter : messageConverters) {
                if (messageConverter.canWrite(returnValueClass, selectedMediaType)) {
                    ((HttpMessageConverter<T>) messageConverter).write(returnValue, selectedMediaType, outputMessage);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Written [" + returnValue + "] as \"" + selectedMediaType + "\" using [" +
                                messageConverter + "]");
                    }
                    return;
                }
            }
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
    }

    private List<MediaType> getAcceptableMediaTypes(HttpInputMessage inputMessage) {
        try {
            List<MediaType> result = inputMessage.getHeaders().getAccept();
            return result.isEmpty() ? Collections.singletonList(MediaType.ALL) : result;
        }
        catch (IllegalArgumentException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not parse Accept header: " + ex.getMessage());
            }
            return Collections.emptyList();
        }
    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        produceType = produceType.copyQualityValue(acceptType);
        return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceType) <= 0 ? acceptType : produceType;
    }

    private static final MediaType MEDIA_TYPE_APPLICATION = new MediaType("application");
}
