package com.dudu.conn;

/**
 * Created by pc on 2015/11/7.
 */
public class ConnectionEvent {

    public static class SessionStateChange{
        public static final int SESSION_CREATE = 0;
        public static final int SESSION_OPEND = 1;
        public static final int SESSION_IDLE = 2;
        public static final int SESSION_CLOSED = 0;
        private int sessonState;
        public SessionStateChange(int sessonState){
            this.sessonState = sessonState;
        }
        public int getSessonState(){
            return  sessonState;
        }
    }

    public static class ReceivedMessage{
        private String resultJson;
        public ReceivedMessage(String resultJson){
            this.resultJson = resultJson;
        }
        public String getResultJson(){
            return resultJson;
        }
    }

    public static class SendDatasResponse{
        private String method;
        private String resultCode;
        private String resultDesc;
        public SendDatasResponse(String method,String resultCode,String resultDesc){
            this.method = method;
            this.resultCode = resultCode;
            this.resultDesc = resultDesc;
        }

        public String getMethod() {
            return method;
        }

        public String getResultCode() {
            return resultCode;
        }

        public String getResultDesc() {
            return resultDesc;
        }
    }


}
