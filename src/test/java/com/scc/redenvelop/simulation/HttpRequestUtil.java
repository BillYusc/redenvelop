package com.scc.redenvelop.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Http请求工具类
 *
 * @author snowfigure
 * @version v1.0.1
 * @since 2014-8-24 13:30:56
 */
public class HttpRequestUtil {
    /**
     * 参数json为json格式的字符串
     */
    public static String sendPostByJSON(String urlStr, String json) {
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");      //POST需大写
            connection.setRequestProperty("content-Type", "application/json");        //设置数据格式为json
            connection.setRequestProperty("charset", "utf-8");                        //设置编码格式为utf-8
            connection.connect();

            outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.append(json);
            outputStreamWriter.flush();

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert outputStreamWriter != null;
                outputStreamWriter.close();
                assert bufferedReader != null;
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response.toString();
    }
}

