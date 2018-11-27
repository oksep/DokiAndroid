package com.dokiwa.dokidoki.center.api.convert;

import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by Septenary on 2018/11/27.
 */
public class ApiGsonConverterFactory extends Converter.Factory {
    public static ApiGsonConverterFactory create() {
        return new ApiGsonConverterFactory(new Gson());
    }

    public static ApiGsonConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new ApiGsonConverterFactory(gson);
    }

    private final Gson gson;

    private ApiGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ApiGsonRequestBodyConverter<>(gson, adapter);
    }

    //这里是关键代码，在判断 type== ObjectStringBean 时候，调用我们自定义的 ApiGsonResponseBodyConverter
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return new ApiGsonResponseBodyConverter(gson, Class.forName(type.getTypeName()));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //其他 Type 类型交给其他 Convert 处理
        return null;
    }
}
