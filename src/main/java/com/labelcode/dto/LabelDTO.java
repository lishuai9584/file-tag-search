package com.labelcode.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 标签DTO
 */
@Data
public class LabelDTO {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称（唯一）
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 100, message = "标签名称不能超过100个字符")
    private String labelName;

    /**
     * 标签颜色
     */
    @Size(max = 7, message = "颜色值不能超过7个字符")
    private String color;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 标签描述
     */
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
}
