package com.labelcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import java.util.Map;

/**
 * 文件元数据实体类 (支持 2亿级 JSONB)
 */
@Data
@TableName(value = "file_meta", autoResultMap = true)
public class FileMeta implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 标签冗余存储
     */
    @TableField("tags")
    private String tags;

    /**
     * 动态元数据
     */
    @TableField("metadata")
    private String metadata;










    /**
     * 关联的标签信息列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<LabelRelation> tagRelations;

    /**
     * 标签关联信息
     */
    @Data
    public static class LabelRelation {
        private Long labelId;
        private String labelName;
        private String color;
    }
}
