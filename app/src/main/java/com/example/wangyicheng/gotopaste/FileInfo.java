package com.example.wangyicheng.gotopaste;

/**
 * this class is used to get result from HTTP
 */

public class FileInfo {
    private String file_name;
    private String url;

    public void setFileName(String name) {
        file_name = name;
    }

    public String getFileName() {
        return file_name;
    }

    public void setUrl(String fileUrl) {
        url = fileUrl;
    }

    public String getUrl() {
        return url;
    }
}
