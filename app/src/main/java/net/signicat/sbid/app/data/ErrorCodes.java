package net.signicat.sbid.app.data;

import java.util.Dictionary;
import java.util.HashMap;

public class ErrorCodes {
    public static final HashMap<String, String> errorHashMap;
    static{
        errorHashMap = new HashMap<String, String>();
        errorHashMap.put("INVALID_PARAMETERS", null);
        errorHashMap.put("ALREADY_IN_PROGRESS", "RFA3");
        errorHashMap.put("INTERNAL_ERROR", "RFA5");
        errorHashMap.put("RETRY", "RFA5");
        errorHashMap.put("ACCESS_DENIED_RP", null);
        errorHashMap.put("Internal system error", null);
    }

}
