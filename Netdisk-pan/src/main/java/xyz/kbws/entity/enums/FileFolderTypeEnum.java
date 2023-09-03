package xyz.kbws.entity.enums;

public enum FileFolderTypeEnum {
    FILE(0,"文档"),
    FOLDER(1,"目录");

    private Integer type;
    private String desc;

    FileFolderTypeEnum(Integer type, String desc){
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
