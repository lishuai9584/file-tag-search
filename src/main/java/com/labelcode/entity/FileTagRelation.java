package com.labelcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件标签关联实体类
 * 对应数据库表：file_tag_relation
 */
@Data
@TableName("file_tag_relation")
public class FileTagRelation {

    /* 复合主键：由 file_id 和 label_id 共同组成，无需定义 ID 列 */


    /**
     * 文件ID
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 标签ID
     */
    @TableField("label_id")
    private Long labelId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
