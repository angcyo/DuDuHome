package com.dudu.rest.common;

/**
 * Created by Administrator on 2016/2/18.
 */

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Body;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * A converter which removes known content lengths to force chunking when {@code @Chunked} is
 * present on {@code @Body} params.
 */
public class ChunkingConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        boolean isBody = false;
        boolean isChunked = false;
        for (Annotation annotation : parameterAnnotations) {
            isBody |= annotation instanceof Body;
            isChunked |= annotation instanceof Chunked;
        }
        if (!isBody || !isChunked) {
            return null;
        }

        // Look up the real converter to delegate to.
        final Converter<Object, RequestBody> delegate =
                retrofit.nextRequestBodyConverter(this, type, parameterAnnotations, methodAnnotations);
        // Wrap it in a Converter which removes the content length from the delegate's body.
        return new Converter<Object, RequestBody>() {
            @Override public RequestBody convert(Object value) throws IOException {
                final RequestBody realBody = delegate.convert(value);
                return new RequestBody() {
                    @Override public MediaType contentType() {
                        return realBody.contentType();
                    }

                    @Override public void writeTo(BufferedSink sink) throws IOException {
                        realBody.writeTo(sink);
                    }
                };
            }
        };
    }

    @Target(PARAMETER)
    @Retention(RUNTIME)
    @interface Chunked {
    }
}


