package com.labelcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelcode.entity.FileDatasetRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文件数据集关联Mapper (物理分区路由)
 */
@Mapper
public interface FileDatasetRelationMapper extends BaseMapper<FileDatasetRelation> {
    
    /**
     * 为文件绑定数据集 (如果已存在则忽略或更新)
     */
    void insertRelation(@Param("fileId") Long fileId, @Param("datasetId") Long datasetId);
}
