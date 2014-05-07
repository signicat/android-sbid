package net.signicat.sbid.app.business;

/**
 * Created by Martin on 05.05.14.
 */
public class ConfigConstants {

    private static final String DEFAULT_AUTH_URL = "https://beta.signicat.com/std/method/signicat?id=sbid2014::";
    private static final String DEFAULT_API_KEY = "1234567890";

    public static String RP_AUTH_URL = "https://beta.signicat.com/std/method/signicat?id=sbid2014::";
    public static String RP_VALIDATE_SAML_URL = "https://labs.signicat.com/catwalk/saml/getattributes";

    public static String SIGNICAT_API_KEY = "1234567890";
    public static String TARGET = "https://labs.signicat.com/catwalk/saml/getattributes";

    public static String getDEFAULT_AUTH_URL() {
        return DEFAULT_AUTH_URL;
    }

    public static String getDEFAULT_API_KEY() {
        return DEFAULT_API_KEY;
    }
}
