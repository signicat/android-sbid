package net.signicat.sbid.app.business;

public class ConfigConstants {

    private static final String DEFAULT_AUTH_URL = "https://beta.signicat.com/std/method/signicat?id=sbidcava-inapp::";
    private static final String DEFAULT_API_KEY = "Bond007";

    public static String RP_AUTH_URL = "https://beta.signicat.com/std/method/signicat?id=sbidcava-inapp::";
    public static String TARGET = "https://labs.signicat.com/catwalk/saml/getattributes";

    public static String SIGNICAT_API_KEY = "Bond007";


    public static String getDEFAULT_AUTH_URL() {
        return DEFAULT_AUTH_URL;
    }

    public static String getDEFAULT_API_KEY() {
        return DEFAULT_API_KEY;
    }
}
