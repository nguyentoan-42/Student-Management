package com.example.studentmanagement.frontend.service;

import java.io.*;
import java.net.*;

public class ApiService {

    // POST/PUT/DELETE: Gửi dữ liệu (Thêm, Sửa, Xóa)
    public static String sendPostOrPut(String targetUrl, String method, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = urlParameters.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line).append("\n");
            }
            rd.close();
            return response.toString().trim();

        } catch (Exception e) {
            return "ERROR";
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    // GET: Lấy dữ liệu (Đọc danh sách, Đọc thông tin)
    public static String sendGet(String targetUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line).append("\n");
            }
            rd.close();
            return response.toString().trim();

        } catch (Exception e) {
            return "ERROR";
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    // Hàm hỗ trợ mã hóa tiếng Việt có dấu
    public static String encode(String value) {
        try {
            if (value == null) return "";
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}