package com.labelcode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dataset_definition")
public class DatasetConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String datasetName;
    private String datasetCode;
    private LocalDateTime createdAt;
}
