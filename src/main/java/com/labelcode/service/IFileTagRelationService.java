package com.labelcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labelcode.entity.FileMeta;
import com.labelcode.entity.FileTagRelation;

import java.util.List;

/**
 * 文件标签关联服务接口
 */
public interface IFileTagRelationService extends IService<FileTagRelation> {

    /**
     * 为文件添加标签
     *
     * @param fileId  文件ID
     * @param labelId 标签ID
     * @return 创建的关联对象
     */
    FileTagRelation addTagTo(Long fileId, Long labelId);

    /**
     * 批量为文件添加标签
     *
     * @param fileId   文件ID
     * @param labelIds 标签ID列表
     */
    void addTagsTo(Long fileId, List<Long> labelIds);

    /**
     * 从文件移除标签
     *
     * @param fileId  文件ID
     * @param labelId 标签ID
     */
    void removeTagFrom(Long fileId, Long labelId);

    /**
     * 批量从文件移除标签
     *
     * @param fileId   文件ID
     * @param labelIds 标签ID列表
     */
    void removeTagsFrom(Long fileId, List<Long> labelIds);

    /**
     * 查询文件的标签列表
     *
     * @param fileId 文件ID
     * @return 标签关联列表
     */
    List<FileTagRelation> getTagsByFile(Long fileId);

    /**
     * 查询具有指定标签的文件列表
     *
     * @param labelId 标签ID
     * @return 标签关联列表
     */
    List<FileTagRelation> getFilesByLabel(Long labelId);

    /**
     * 查询文件的标签信息（包含标签详情）
     *
     * @param fileId 文件ID
     * @return 文件元数据（包含标签详情）
     */
    FileMeta getFileWithTags(Long fileId);

    /**
     * 批量查询文件的标签信息
     *
     * @param fileIds 文件ID列表
     * @return 文件元数据列表
     */
    List<FileMeta> getFilesWithTags(List<Long> fileIds);

    /**
     * 多标签交集查询
     * 查询同时具有多个标签的文件
     *
     * @param labelIds 标签ID列表
     * @return 文件ID列表
     */
    List<Long> findFilesByTagIntersection(List<Long> labelIds);

    /**
     * 统计标签的使用次数
     *
     * @param labelId 标签ID
     * @return 关联文件数
     */
    Long countByLabelId(Long labelId);

    /**
     * 批量统计标签的使用次数
     *
     * @param labelIds 标签ID列表
     * @return 标签ID和数量映射
     */
    List<com.labelcode.entity.LabelLibrary> countByLabelIds(List<Long> labelIds);
}
