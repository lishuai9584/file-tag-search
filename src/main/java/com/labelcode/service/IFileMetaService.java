package com.labelcode.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.labelcode.entity.FileMeta;

import java.util.List;
import java.util.Map;

/**
 * 文件元数据服务接口
 */
public interface IFileMetaService extends IService<FileMeta> {

    /**
     * 高级搜索 (支持分页)
     */
    IPage<FileMeta> advancedSearch(IPage<FileMeta> page, String fileName, String fileType, Long datasetId, List<Long> tags, Map<String, Object> metadata, String startDate, String endDate);



    /**
     * 创建文件并关联标签和数据集 (2亿级合规架构)
     */
    FileMeta createFileWithTags(FileMeta fileMeta, List<Long> labelIds, Long datasetId);

}
