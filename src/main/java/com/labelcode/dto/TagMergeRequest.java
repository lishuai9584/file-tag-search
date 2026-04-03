package com.labelcode.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 标签合并请求DTO
 */
@Data
public class TagMergeRequest {

    /**
     * 要合并的旧标签ID
     */
    @NotNull(message = "旧标签ID不能为空")
    private Long oldLabelId;

    /**
     * 目标标签ID（合并后保留的标签）
     */
    @NotNull(message = "目标标签ID不能为空")
    private Long newLabelId;
}
