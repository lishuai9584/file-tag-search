package com.labelcode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attribute_config")
public class AttributeConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long datasetId;
    private String attrKey;
    private String attrLabel;
    private String dataType;
    private Boolean isRequired;
    private Boolean isGlobal;
    private LocalDateTime createdAt;
}
