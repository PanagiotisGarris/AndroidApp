package com.example.smokebot;

import java.util.List;

public class MessageClass {

    public static final int FROM_BOT = 0;
    public static final int FROM_USER = 1;

    // Message content.
    private String msgContent;

    // Message type.
    private int msgType;

    //QuickReplies List
    private List<String> quickR;

    public MessageClass(int msgType, String msgContent, List<String> quickR) {
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.quickR = quickR;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public List<String> getQuickR(){
        return quickR;
    }

    public void setQuickList(List<String> quickR){
        this.quickR = quickR;
    }
}
