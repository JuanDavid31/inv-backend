package ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EndPointHandler {

    private static EndPointHandler instance;
    public static Map<String, EndPoint> endPoints = new ConcurrentHashMap<>();

    public static class EndPointHandlerBuilder{
        public static void build(){
            instance = new EndPointHandler();
        }
    }

    public EndPointHandler getInstance(){
        return instance;
    }
}
