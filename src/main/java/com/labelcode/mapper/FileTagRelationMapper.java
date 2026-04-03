package com.labelcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelcode.entity.FileTagRelation;
import com.labelcode.entity.FileMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 文件标签关联Mapper接口
 */
@Mapper
public interface FileTagRelationMapper extends BaseMapper<FileTagRelation> {

    /**
     * 合并标签时，直接在数据库层面删除那些同时拥有新旧标签的冲突记录
     */
    void deleteConflictsOnMerge(@Param("oldId") Long oldId, @Param("newId") Long newId);

    /**
     * 查询文件的标签列表
     *
     * @param fileId 文件ID
     * @return 标签关联列表
     */
    List<FileTagRelation> selectByFileId(@Param("fileId") Long fileId);

    /**
     * 查询具有指定标签的文件列表
     *
     * @param labelId 标签ID
     * @return 标签关联列表
     */
    List<FileTagRelation> selectByLabelId(@Param("labelId") Long labelId);

    /**
     * 批量查询文件标签关联
     *
     * @param fileIds 文件ID集合
     * @return 标签关联列表
     */
    List<FileTagRelation> selectByFileIds(@Param("fileIds") Collection<Long> fileIds);

    /**
     * 查询文件的标签名称数组（用于JSONB查询）
     *
     * @param fileId 文件ID
     * @return 标签名称数组
     */
    List<String> selectTagNamesByFileId(@Param("fileId") Long fileId);

    /**
     * 批量查询标签ID列表
     *
     * @param fileId 文件ID
     * @return 标签ID列表
     */
    List<Long> selectLabelIdsByFileId(@Param("fileId") Long fileId);

    /**
     * 多标签交集查询（PostgreSQL JSONB查询）
     *
     * @param sql 查询SQL
     * @return 文件ID列表
     */
    List<Long> selectFileIdsByIntersection(@Param("sql") String sql);
}
