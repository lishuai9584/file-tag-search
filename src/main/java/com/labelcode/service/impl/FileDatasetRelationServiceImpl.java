package com.labelcode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labelcode.entity.FileDatasetRelation;
import com.labelcode.mapper.FileDatasetRelationMapper;
import com.labelcode.service.IFileDatasetRelationService;
import org.springframework.stereotype.Service;

/**
 * 文件数据集关联实现类
 */
@Service
public class FileDatasetRelationServiceImpl extends ServiceImpl<FileDatasetRelationMapper, FileDatasetRelation> 
        implements IFileDatasetRelationService {

    @Override
    public void bindDataset(Long fileId, Long datasetId) {
        if (fileId == null || datasetId == null) return;
        // 1. 检查是否存在 (可选：取决于是否允许一个文件跨多个数据集)
        // 2. 插入关联实现路由
        this.save(new FileDatasetRelation(fileId, datasetId));
    }
}
