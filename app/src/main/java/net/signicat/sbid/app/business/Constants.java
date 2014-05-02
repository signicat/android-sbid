package net.signicat.sbid.app.business;

/**
 * Created by Martin on 30.04.14.
 */
public class Constants {

    public static String TAG_TESTING = "SBID_TESTING";
    public static String TAG_SBID_AUTH = "SBID_AUTH";

    public static String RP_AUTH_URL = "https://sbid.com";
    public static String RP_COLLECT_URL = "";
    public static String RP_VALIDATE_SAML_URL = "";

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

    public enum ErrorMessageToUser{
        RFA3("Action cancelled. Please try again.");
        private String message;
        private ErrorMessageToUser(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
