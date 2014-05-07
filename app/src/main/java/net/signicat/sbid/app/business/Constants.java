package net.signicat.sbid.app.business;

public class Constants {
    public static String TAG_SBID_AUTH = "SBID_AUTH";
    public static String TAG_SBID_ERROR = "SBID_ERROR";

    public static String PREF_KEY = "sbidtestingprefkey";
    public static String SIGNICAT_URL_PREF_KEY = "signicaturlprefkey";
    public static String SIGNICAT_API_KEY_PREF_KEY = "signicatapipkeyrefkey";

    public enum ErrorMessageFromServer{
        ALREADY_IN_PROGRESS("ALREADY_IN_PROGRESS");

        private String value;
        private ErrorMessageFromServer(String i){
            this.value = i;
        }

        public String getValue() {
            return value;
        }
    }
}
