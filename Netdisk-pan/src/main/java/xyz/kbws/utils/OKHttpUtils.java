package xyz.kbws.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kbws.entity.enums.ResponseCodeEnum;
import xyz.kbws.exception.BusinessException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OKHttpUtils {
    public static final int TIME_OUT_SECOND = 0;

    public static final Logger logger = LoggerFactory.getLogger(OKHttpUtils.class);
    private static OkHttpClient.Builder getCulientBuilder(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false);
        builder.connectTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS).readTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS);
        return builder;
    }

    private static Request.Builder getRequestBuilder(Map<String, String> header){
        Request.Builder requestBuilder = new Request.Builder();
        if (header != null){
            for (Map.Entry<String, String> map : header.entrySet()){
                String key = map.getKey();
                String value;
                if (map.getValue() == null){
                    value = "";
                }else{
                    value = map.getValue();
                }
                requestBuilder.addHeader(key, value);
            }
        }
        return requestBuilder;
    }

    public static String getRequest(String url){
        ResponseBody responseBody = null;
        try {
            OkHttpClient.Builder clientBuilder = getCulientBuilder();
            Request.Builder requestBuilder = getRequestBuilder(null);
            OkHttpClient client = clientBuilder.build();
            Request request = requestBuilder.url(url).build();
            Response response = client.newCall(request).execute();
            responseBody = response.body();
            String responseStr = responseBody.string();
            logger.info("postRequest请求地址:{},返回信息:{}", url, responseStr);
            return responseStr;
        }catch (SocketTimeoutException | ConnectException e){
            logger.error("OKHttp POST 请求超时,url:{}",url, e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }catch (Exception e){
            logger.error("OKHtp GET 请求异常",e);
            return null;
        }finally {
            if (responseBody != null){
                responseBody.close();
            }
        }
    }
}
