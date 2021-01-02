package ir.mahdi.moradi.messagingapplication.Utils;

public class ServerConfig {
    public static String baseServerURL="https://mahdi-moradi.ir/messagingapp/index.php/";
    /*public static String baseServerURL="http://192.168.1.101/mahdisite/public/";*/
    public static String socketServerURL="";
    public static String apiKey = "123";

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        ServerConfig.apiKey = apiKey;
    }


    public static String getBaseServerURL() {
        return baseServerURL;
    }

    public static void setBaseServerURL(String baseServerURL) {
        ServerConfig.baseServerURL = baseServerURL;
    }

    public static String getSocketServerURL() {
        return socketServerURL;
    }

    public static void setSocketServerURL(String socketServerURL) {
        ServerConfig.socketServerURL = socketServerURL;
    }
}
