package xyz.kbws.entity.enums;

public enum UploadStatusEnum {
    UPLOAD_SECONDS("upload_seconds","秒传"),
    UPLOADING("uploading","上传中"),
    UPLOAD_FINISH("upload_finish","上传中");

    private String code;
    private String desc;

    UploadStatusEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
