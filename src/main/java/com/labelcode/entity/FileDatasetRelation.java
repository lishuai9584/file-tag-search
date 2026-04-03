package com.labelcode.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 文件与数据集关联实体类 (2亿级物理分区映射)
 */
@Data
@TableName("file_dataset_relation")
public class FileDatasetRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 文件ID */
    private Long fileId;
    
    /** 数据集ID */
    private Long datasetId;
    
    public FileDatasetRelation() {}
    
    public FileDatasetRelation(Long fileId, Long datasetId) {
        this.fileId = fileId;
        this.datasetId = datasetId;
    }
}
