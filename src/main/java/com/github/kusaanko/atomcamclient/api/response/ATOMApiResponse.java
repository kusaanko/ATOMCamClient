package com.github.kusaanko.atomcamclient.api.response;

public class ATOMApiResponse {
    public static final String SUCCESS = "1";
    public static final String SERVER_INTERNAL_ERROR = "1000";
    public static final String PARAMETER_FORMAT_ERROR = "1001";
    public static final String HTTP_FORMAT_ERROR = "1002";
    public static final String INVALID_REQUEST_CONTENT_TYPE = "1003";
    public static final String SIGNATURE_ERROR = "1004";
    public static final String TIMEOUT = "1005";
    public static final String ACCESS_SV_SC_ERROR = "1006";
    public static final String INVALID_APP = "1007";
    public static final String WRONG_USERNAME_PASSWORD = "2000";
    public static final String ACCESS_TOKEN_ERROR = "2001";
    public static final String REFRESH_TOKEN_ERROR = "2002";
    public static final String ACCOUNT_REGISTRATION = "2003";
    public static final String LOCKED_ACCOUNT = "2004";
    public static final String VALIDATION_CODE_ERROR = "2005";
    public static final String VALIDATION_CODE_EXPIRED = "2006";
    public static final String VALIDATION_CODE_REACHED_MAX = "2007";
    public static final String FAILED_TO_SEND_VALIDATION_CODE = "2008";
    public static final String USER_DONT_EXIST = "2009";
    public static final String OLD_PASSWORD = "2010";
    public static final String INVALID_PASSWORD_ON_CHANGING_USERNAME = "2011";
    public static final String NO_DEVICE_INFOMATION = "3000";
    public static final String INVALID_BINDING_TOKEN = "3004";
    public static final String FAILED_TO_ACCEPT_SHAREING = "3005";
    public static final String UNAUTHORIZED_OPERATION = "3006";
    public static final String DEVICE_IS_OFFLINE = "3019";
    public static final String USERNAME_EXISITS = "3025";
    public static final String INVALID_PHONE_NUMBER = "3027";
    public static final String USED_PHONE_NUMBER = "3028";
    public static final String NO_MASTER_PHONE = "3029";
    public static final String HAVE_BACKUP_PHONE_NUMBER = "3030";
    public static final String NO_BACKUP_PHONE_NUMBER = "3031";
    public static final String AUTHORIZED_BY_2FA = "3032";
    public static final String REQUIRED_2FA = "3033";
    public static final String REACHED_MAX_VIDEO = "3037";
    public static final String SMART_DELETED = "3038";
    public static final String ALEXA_SERVER_INTERNAL_ERROR = "3044";
    public static final String DEFAULT_ADDRESS_CANT_BE_DELETED = "4002";
    public static final String CART_IS_EMPTY = "4003";

    private String msg;
    private String code;
    private long ts;

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public long getTs() {
        return ts;
    }
}
