package com.labelcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.labelcode.entity.FileMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文件元数据Mapper接口
 */
@Mapper
public interface FileMetaMapper extends BaseMapper<FileMeta> {

    /**
     * 多条件、多标签交集查询 (基于PostgreSQL JSONB)
     * 支持分页
     */
    IPage<FileMeta> findFilesByAdvancedSearch(
            IPage<FileMeta> page,
            @Param("fileName") String fileName,
            @Param("fileType") String fileType,
            @Param("datasetId") Long datasetId,
            @Param("tags") List<Long> tags,
            @Param("metadata") Map<String, Object> metadata,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /**
     * 手动计算总数，彻底规避 MyBatis-Plus JSqlParser 对 PostgreSQL 特有语法的截断和报错
     */
    long countFilesByAdvancedSearch(
            @Param("fileName") String fileName,
            @Param("fileType") String fileType,
            @Param("datasetId") Long datasetId,
            @Param("tags") List<Long> tags,
            @Param("metadata") Map<String, Object> metadata,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);



    /**
     * 合并标签时，同步更新元数据表中的 JSONB 标签数组 (高性能单次更新版)
     */
    void updateJsonTagsOnMerge(@Param("oldId") Long oldId, @Param("newId") Long newId);

    /**
     * 手写强转插入：解决 MyBatis-Plus 自动生成的 save() 无法追加 ::jsonb 的问题
     */
    int insertFileWithJsonb(FileMeta fileMeta);
}

