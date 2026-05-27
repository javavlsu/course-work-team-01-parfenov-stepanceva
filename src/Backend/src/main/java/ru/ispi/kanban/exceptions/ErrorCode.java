package ru.ispi.kanban.exceptions;

public final class ErrorCode {

    private ErrorCode() {
    }

    // AU — Auth
    public static final String AUTH_COOKIE_MISSING = "AU0001";
    public static final String AUTH_TOKEN_EXPIRED = "AU0002";
    public static final String AUTH_REFRESH_INVALID = "AU0003";
    public static final String AUTH_REFRESH_EXPIRED = "AU0004";
    public static final String AUTH_UNAUTHENTICATED = "AU0005";
    public static final String AUTH_FORBIDDEN = "AU0006";

    // US — User
    public static final String USER_NOT_FOUND_BY_EMAIL = "US0001";
    public static final String USER_NOT_FOUND_BY_ID = "US0002";
    public static final String USER_ALREADY_EXISTS = "US0003";
    public static final String USER_INVALID_PASSWORD = "US0004";

    // BD — Board
    public static final String BOARD_ACCESS_DENIED = "BD0001";

    // CL — Column
    public static final String COLUMN_NOT_FOUND = "CL0001";

    // TS — Task
    public static final String TASK_NOT_FOUND = "TS0001";

    // CM — Comment
    public static final String COMMENT_NOT_FOUND = "CM0001";
    public static final String COMMENT_ACCESS_DENIED = "CM0002";

    // AT — Attachment
    public static final String ATTACHMENT_NOT_FOUND = "AT0001";

    // GR — Group
    public static final String GROUP_NOT_ADMIN = "GR0001";
    public static final String GROUP_NOT_MEMBER = "GR0002";
    public static final String GROUP_MEMBER_EXISTS = "GR0003";

    // IN — Invitation
    public static final String INVITATION_NOT_FOUND = "IN0001";
    public static final String INVITATION_EXPIRED = "IN0002";

    // VA — Validation
    public static final String VALIDATION_FAILED = "VA0001";
    public static final String VALIDATION_BAD_REQUEST = "VA0002";
    public static final String VALIDATION_BAD_ENUM = "VA0003";

    // FL — File
    public static final String FILE_EMPTY = "FL0001";
    public static final String FILE_TOO_LARGE = "FL0002";
    public static final String FILE_INVALID_TYPE = "FL0003";
    public static final String FILE_STORAGE_ERROR = "FL0004";
    public static final String FILE_INVALID_PATH = "FL0005";

    // EN — Entity
    public static final String ENTITY_NOT_FOUND = "EN0001";

    // SY — System / fallback
    public static final String INTERNAL_ERROR = "SY0001";
}
