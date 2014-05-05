package net.signicat.sbid.app.business;

/**
 * Created by Martin on 30.04.14.
 */
public class Constants {
    public static String TAG_SBID_AUTH = "SBID_AUTH";

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
