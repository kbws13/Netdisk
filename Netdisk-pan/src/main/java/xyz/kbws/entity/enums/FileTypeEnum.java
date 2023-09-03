package xyz.kbws.entity.enums;

import org.apache.commons.lang3.ArrayUtils;

public enum FileTypeEnum {
    //1.视频 2.音频 3.图片 4.PDF 5.Word 6.Excel 7.TXT 8.Code 9.ZIP 10.其它文件
    VIDEO(FileCategoryEnum.VIDEO, 1, new String[]{".mp4",".avi",".rmvb",".mkv",".mov"},"视频"),
    MUSIC(FileCategoryEnum.MUSIC, 2, new String[]{".mp3",".wav",".wma",".mp2",".flac",".midi",".ra",
            ".ape",".aac",".cda"},"音频"),
    IMAGE(FileCategoryEnum.IMAGE, 3, new String[]{".jpeg",".jpg",".png",".gif",".bmp",".dds",".psd",
            ".pdt",".webp",".xmp",".svg",".tiff"},"图片"),
    PDF(FileCategoryEnum.DOC, 4, new String[]{".pdf"},"PDF"),
    WORD(FileCategoryEnum.DOC, 5, new String[]{".doc",".docx"},"Word"),
    EXCEL(FileCategoryEnum.DOC, 6, new String[]{".xlsx",".xls"},"Excel"),
    TXT(FileCategoryEnum.DOC, 7, new String[]{".txt"},"txt文本"),
    PROGRAME(FileCategoryEnum.OTHERS, 8, new String[]{".h",".c",".hpp",".hpp",".hxx",".cpp",".cc",
            ".c++",".cxx", ".m",".o",".s",".dll",".cs",".java",".class",".js",".ts",".scss",".css",".vue",
            ".jsx",".sql",".md",".json",".html",".xml",".py",".go",".rs"},"CODE"),
    ZIP(FileCategoryEnum.OTHERS, 9, new String[]{".rar",".zip",".7z",".cab",".arj",".lzh",".tar",".gz",
            ".ace",".uue",".bz",".jar",".iso","mpq"},"压缩包"),
    OTHERS(FileCategoryEnum.OTHERS, 10, new String[]{".mp4",".avi",".rmvb",".mkv",".mov"},"其它");

    private FileCategoryEnum category;
    private Integer type;
    private String[] suffixs;
    private String desc;

    FileTypeEnum(FileCategoryEnum category, Integer type, String[] suffixs, String desc){
        this.category = category;
        this.type = type;
        this.suffixs = suffixs;
        this.desc = desc;
    }

    public static FileTypeEnum getFileTypeBySuffix(String suffix){
        for (FileTypeEnum item : FileTypeEnum.values()){
            if (ArrayUtils.contains(item.getSuffixs(), suffix)){
                return item;
            }
        }
        return FileTypeEnum.OTHERS;
    }

    public static FileTypeEnum getByType(Integer type){
        for (FileTypeEnum item : FileTypeEnum.values()){
            if (item.getType().equals(type)){
                return item;
            }
        }
        return null;
    }

    public FileCategoryEnum getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String[] getSuffixs() {
        return suffixs;
    }

    public String getDesc() {
        return desc;
    }
}
