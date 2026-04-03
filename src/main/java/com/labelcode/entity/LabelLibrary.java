package com.labelcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签库实体类
 * 对应数据库表：label_library
 */
@Data
@TableName("label_library")
public class LabelLibrary {

    /**
     * 标签ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称（唯一）
     */
    @TableField("label_name")
    private String labelName;

    /**
     * 标签颜色
     */
    @TableField("color")
    private String color;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 标签描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联文件数
     */
    @TableField("tag_count")
    private Long tagCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
