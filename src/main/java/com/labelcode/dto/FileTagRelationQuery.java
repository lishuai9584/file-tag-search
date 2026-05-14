package com.labelcode.dto;

import lombok.Data;

import java.util.List;

/**
 * 文件标签关联查询DTO
 */
@Data
public class FileTagRelationQuery {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 标签ID列表（用于多标签交集查询）
     */
    private List<Long> labelIds;
}
