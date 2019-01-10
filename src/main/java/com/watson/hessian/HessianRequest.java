package com.watson.hessian;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.watson.hessian.utils.HessianUtil;
import com.watson.hessian.utils.HttpClientPool;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * HessianRequest
 * <p>
 * Created by watson on 10/01/2019.
 */
public class HessianRequest extends AbstractJavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String remoteUrl = javaSamplerContext.getParameter("remoteUrl");
        System.out.println("remoteUrl:" + remoteUrl);
        String remoteMethod = javaSamplerContext.getParameter("remoteMethod");
        System.out.println("remoteMethod:" + remoteMethod);
        String note = javaSamplerContext.getParameter("note");
        System.out.println("note:" + note);

        String jsonParam = javaSamplerContext.getParameter("jsonParam");
        Map<String, Object> params = JSON.parseObject(jsonParam, new TypeReference<Map<String, Object>>() {});

        SampleResult sr = new SampleResult();
        sr.setSampleLabel(note);

        try {
            sr.sampleStart(); // jmeter 开始统计响应时间标记
            Object object = HessianUtil.invoke(remoteUrl, remoteMethod, 15000, new Object[]{params});
            String resultData = JSON.toJSONString(object);
            if (resultData != null && resultData.length() > 0) {
                sr.setResponseData(resultData, null);
                sr.setDataType(SampleResult.TEXT);
            }
            sr.setSuccessful(true);
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exStr = sw.toString();
            sr.setResponseData(exStr, null);
            sr.setSuccessful(false);
            e.printStackTrace();
            System.out.println(exStr);
        } finally {
            sr.sampleEnd(); // jmeter 结束统计响应时间标记
        }
        return sr;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        HttpClientPool.getInstance().init(500, 200);
    }
}