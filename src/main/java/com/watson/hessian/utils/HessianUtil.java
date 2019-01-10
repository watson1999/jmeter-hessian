package com.watson.hessian.utils;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * HessianUtil
 * <p>
 * Created by manji on 10/01/2019.
 */
public class HessianUtil {

    public static Object invoke(String url, String method, int timeout, Object[] args) throws Throwable {

        CloseableHttpResponse response = null;
        InputStream input = null;
        Hessian2Input h2i = null;
        try {

            HttpPost post = new HttpPost(url);
            post.setConfig(RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Hessian2Output h2o = new Hessian2Output(output);
            h2o.call(method, args);
            h2o.flush();
            h2o.close();
            byte[] bytes = output.toByteArray();
            post.setEntity(new ByteArrayEntity(bytes));
            post.setHeader("Content-Type", "x-application/hessian");
            response = HttpClients.custom().setConnectionManager(HttpClientPool.getInstance().POOL).build().execute(post);
            HttpEntity entity = response.getEntity();
            input = entity.getContent();
            if (response.getStatusLine().getStatusCode() == 200) {
                input.read();
                input.read();
                input.read();
                h2i = new Hessian2Input(input);
                return h2i.readReply(Object.class);
            } else {

                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                throw new HessianConnectionException(result.toString(StandardCharsets.UTF_8.name()));
            }
        } catch (Throwable e) {
            throw new HessianRuntimeException(e);
        } finally {
            if (input != null)
                input.close();
            if (h2i != null)
                h2i.close();
            if (response != null)
                response.close();
        }
    }
}