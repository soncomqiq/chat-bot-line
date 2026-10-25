package site.nuttachai.line_bot.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LineConstant {
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
    public static final String CLIENT_ASSERTION_TYPE_PARAM = "client_assertion_type";
    public static final String CLIENT_ASSERTION_PARAM = "client_assertion";
    public static final String JWT_BEARER_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
}
