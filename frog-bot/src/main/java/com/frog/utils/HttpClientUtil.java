package com.frog.utils;

import com.alibaba.fastjson.JSONObject;
import com.frog.config.BotConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Http工具类
 */
public class HttpClientUtil {

    /**
     * /**
     * 发送GET方式请求
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static String doGet(String url, Map<String, String> paramMap) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String result = "";
        CloseableHttpResponse response = null;

        try {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    builder.addParameter(key, paramMap.get(key));
                }
            }
            URI uri = builder.build();

            //创建GET请求
            HttpGet httpGet = new HttpGet(uri);

            //发送请求
            response = httpClient.execute(httpGet);

            //判断响应状态
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 发送GET方式请求
     *
     * @param url
     * @param payload
     * @param promptWav
     * @param mode
     * @return
     */
    public static String doGet(String url, Map<String, String> payload, String promptWav, String mode) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            URIBuilder uriBuilder = new URIBuilder(url + mode);
            payload.forEach(uriBuilder::addParameter);
            if (mode.equals("zero_shot") || mode.equals("cross_lingual")) {
                try (InputStream audioStream = ResourceReader.class.getClassLoader().getResourceAsStream(promptWav)) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = audioStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }
                    byte[] fileContent = buffer.toByteArray();
                    String base64FileContent = Base64.getEncoder().encodeToString(fileContent);
                    uriBuilder.addParameter("prompt_wav", base64FileContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());

            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 创建参数列表
            if (paramMap != null) {
                List<NameValuePair> paramList = new ArrayList();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    public static String doPost4Json(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                //构造json格式数据
                JSONObject jsonObject = new JSONObject();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    jsonObject.put(param.getKey(), param.getValue());
                }
                StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    public static String doPost4Json(String url, Map<String, String> paramMap, MultipartFile file) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 构建multipart请求体
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 添加表单参数
            for (Map.Entry<String, String> param : paramMap.entrySet()) {
                builder.addTextBody(param.getKey(), param.getValue(),
                        ContentType.create("text/plain", "UTF-8"));
            }

            if (!ObjectUtils.isEmpty(file)) {
                // 添加文件
                builder.addBinaryBody("prompt_image", file.getInputStream(),
                        ContentType.create("application/octet-stream"), "prompt_image");
            }

            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);
            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    public static CloseableHttpResponse doPost4JsonStream(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                //构造json格式数据
                JSONObject jsonObject = new JSONObject();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    jsonObject.put(param.getKey(), param.getValue());
                }
                StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            CloseableHttpResponse response = httpClient.execute(httpPost);

            return response;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static CloseableHttpResponse doPostStream(String url, Map<String, String> paramMap, MultipartFile file) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 构建multipart请求体
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 添加表单参数
            for (Map.Entry<String, String> param : paramMap.entrySet()) {
                builder.addTextBody(param.getKey(), param.getValue(),
                        ContentType.create("text/plain", "UTF-8"));
            }

            if (!ObjectUtils.isEmpty(file)) {
                // 添加文件
                builder.addBinaryBody("prompt_image", file.getInputStream(),
                        ContentType.create("application/octet-stream"), "prompt_image");
            }

            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);
            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            return httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @param paramMap
     * @param promptWav
     * @param mode
     * @return
     * @throws IOException
     */
    public static byte[] doPostByte(String url, Map<String, String> paramMap, String promptWav, String mode) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        byte[] wavByte = null;

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 构建multipart请求体
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 添加表单参数
            if (paramMap != null) {
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    builder.addTextBody(param.getKey(), param.getValue(),
                            ContentType.create("text/plain", "UTF-8"));
                }
            }

            if (mode.equals("zero_shot") || mode.equals("cross_lingual")) {
                // 添加音频文件
                if (promptWav != null) {
                    // 添加文件
                    builder.addBinaryBody("prompt_wav", ResourceReader.class.getClassLoader().getResourceAsStream(promptWav),
                            ContentType.create("application/octet-stream"), "prompt_wav");
                }
            }

            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);
            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                responseEntity.writeTo(baos);
                wavByte = baos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wavByte;
    }

    /**
     * 发送POST方式请求
     *
     * @param url
     * @param paramMap
     * @param promptWav
     * @return
     * @throws IOException
     */
    public static byte[] doPostBytes(String url, Map<String, String> paramMap, String promptWav, String mode) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        byte[] wavByte = null;

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 构建multipart请求体
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            // 添加表单参数
            if (paramMap != null) {
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    StringBody contentBody = new StringBody(param.getValue(),
                            "text/plain", Charset.forName("UTF-8"));
                    entity.addPart(param.getKey(), contentBody);
                }
            }

            if (mode.equals("zero_shot") || mode.equals("cross_lingual")) {
                // 添加音频文件
                if (promptWav != null) {
                    // 添加文件
                    ByteArrayBody audioBody = new ByteArrayBody(
                            IOUtils.toByteArray(ResourceReader.class.getClassLoader().getResourceAsStream(promptWav)),
                            ContentType.APPLICATION_OCTET_STREAM,
                            "prompt_wav"
                    );
                    entity.addPart("prompt_wav", audioBody);
                }
            }

            httpPost.setEntity(entity);
            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                responseEntity.writeTo(baos);
                wavByte = baos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wavByte;
    }

    /**
     * Post
     *
     * @param url
     * @param paramMap
     * @param promptWav
     * @return
     * @throws IOException
     */
    public static byte[] doPostRestTemplateByte(String url, Map<String, String> paramMap, String promptWav, String mode) throws IOException {
        // 创建配置化的RestTemplate
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build();

        // 创建multipart/form-data请求体
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        // 添加表单参数
        if (paramMap != null && !paramMap.isEmpty()) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                params.add(entry.getKey(), entry.getValue());
            }
        }

        if (mode.equals("zero_shot") || mode.equals("cross_lingual")) {
            // 添加音频文件
            if (promptWav != null) {
                Resource resource = new ClassPathResource(promptWav);
                params.add("prompt_wav", resource);
            }
        }

        // 发送POST请求并获取响应
        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, params, byte[].class);

        // 返回响应体字节数组
        return response.getBody();
    }

    private static RequestConfig builderRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(BotConfig.getAiTimeoutMsec())
                .setConnectionRequestTimeout(BotConfig.getAiTimeoutMsec())
                .setSocketTimeout(BotConfig.getAiTimeoutMsec()).build();
    }

}
