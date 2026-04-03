package com.labelcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labelcode.entity.FileDatasetRelation;

/**
 * 文件数据集关联服务接口
 */
public interface IFileDatasetRelationService extends IService<FileDatasetRelation> {
    
    /**
     * 将文件划归到特定数据集
     * @param fileId 文件ID
     * @param datasetId 数据集ID
     */
    void bindDataset(Long fileId, Long datasetId);
}
