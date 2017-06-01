package com.example.wangyicheng.gotopaste;

/**
 * this class is used to get the result from the server
 */

public class MsgInfo {
    private String title;
    private String shared_msg;
    private int time;
    private FileInfo[] file;
    private String result;

    public void setTitle(String msgTitle) {
        title = msgTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setSharedMsg(String sharedMsg) {
        shared_msg = sharedMsg;
    }

    public String getSharedMsg() {
        return shared_msg;
    }

    public void setTime(int msgTime) {
        time = msgTime;
    }
    public int getTime() {
        return time;
    }

    public void setFile(FileInfo[] msgFile) {
        file = msgFile;
    }

    public FileInfo[] getFile() {
        return file;
    }

    public void setResult(String infoResult) {
        result = infoResult;
    }

    public String getResult() {
        return result;
    }
}
