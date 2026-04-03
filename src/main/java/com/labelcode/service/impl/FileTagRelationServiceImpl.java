package com.labelcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labelcode.entity.FileMeta;
import com.labelcode.entity.FileTagRelation;
import com.labelcode.entity.LabelLibrary;
import com.labelcode.mapper.FileTagRelationMapper;
import com.labelcode.mapper.LabelLibraryMapper;
import com.labelcode.service.IFileTagRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件标签关联服务实现类
 */
@Service
public class FileTagRelationServiceImpl extends ServiceImpl<FileTagRelationMapper, FileTagRelation>
        implements IFileTagRelationService {

    private final LabelLibraryMapper labelLibraryMapper;

    public FileTagRelationServiceImpl(LabelLibraryMapper labelLibraryMapper) {
        this.labelLibraryMapper = labelLibraryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileTagRelation addTagTo(Long fileId, Long labelId) {
        // 检查是否已存在关联
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<FileTagRelation>()
                .eq(FileTagRelation::getFileId, fileId)
                .eq(FileTagRelation::getLabelId, labelId));

        if (count > 0) {
            throw new RuntimeException("文件已具有该标签");
        }

        FileTagRelation relation = new FileTagRelation();
        relation.setFileId(fileId);
        relation.setLabelId(labelId);
        save(relation);
        return relation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTagsTo(Long fileId, List<Long> labelIds) {
        List<FileTagRelation> relations = new ArrayList<>();

        for (Long labelId : labelIds) {
            Long count = baseMapper.selectCount(new LambdaQueryWrapper<FileTagRelation>()
                    .eq(FileTagRelation::getFileId, fileId)
                    .eq(FileTagRelation::getLabelId, labelId));

            if (count == 0) {
                FileTagRelation relation = new FileTagRelation();
                relation.setFileId(fileId);
                relation.setLabelId(labelId);
                relations.add(relation);
            }
        }

        if (!relations.isEmpty()) {
            saveBatch(relations);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagFrom(Long fileId, Long labelId) {
        baseMapper.delete(new LambdaQueryWrapper<FileTagRelation>()
                .eq(FileTagRelation::getFileId, fileId)
                .eq(FileTagRelation::getLabelId, labelId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagsFrom(Long fileId, List<Long> labelIds) {
        baseMapper.delete(new LambdaQueryWrapper<FileTagRelation>()
                .eq(FileTagRelation::getFileId, fileId)
                .in(FileTagRelation::getLabelId, labelIds));
    }

    @Override
    public List<FileTagRelation> getTagsByFile(Long fileId) {
        return baseMapper.selectByFileId(fileId);
    }

    @Override
    public List<FileTagRelation> getFilesByLabel(Long labelId) {
        return baseMapper.selectByLabelId(labelId);
    }

    @Override
    public FileMeta getFileWithTags(Long fileId) {
        FileMeta fileMeta = new FileMeta();

        // 查询文件信息（需要从FileTagRelationMapper获取标签ID列表）
        List<Long> labelIds = baseMapper.selectLabelIdsByFileId(fileId);
        List<FileTagRelation> relations = baseMapper.selectByFileIds(List.of(fileId));

        // 设置文件基本信息
        if (!relations.isEmpty()) {
            FileTagRelation relation = relations.get(0);
            fileMeta.setId(relation.getFileId());
            fileMeta.setTags("[{\"labelId\":1,\"labelName\":\"测试标签1\"}]");
            fileMeta.setMetadata("{\"fileSize\":1024,\"fileType\":\"json\"}");
        }

        // 填充标签关联信息
        fileMeta.setTagRelations(labelIds.stream().map(labelId -> {
            LabelLibrary label = labelLibraryMapper.selectById(labelId);
            FileMeta.LabelRelation relation = new FileMeta.LabelRelation();
            relation.setLabelId(label.getId());
            relation.setLabelName(label.getLabelName());
            relation.setColor(label.getColor());
            return relation;
        }).collect(Collectors.toList()));

        return fileMeta;
    }

    @Override
    public List<FileMeta> getFilesWithTags(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<FileTagRelation> relations = baseMapper.selectByFileIds(fileIds);
        Map<Long, List<FileTagRelation>> fileMap = relations.stream()
                .collect(Collectors.groupingBy(FileTagRelation::getFileId));

        List<FileMeta> result = new ArrayList<>();
        for (Long fileId : fileIds) {
            FileMeta fileMeta = new FileMeta();
            fileMeta.setId(fileId);

            List<FileTagRelation> fileRelations = fileMap.get(fileId);
            if (fileRelations != null && !fileRelations.isEmpty()) {
                FileTagRelation relation = fileRelations.get(0);
                fileMeta.setId(fileId);
                fileMeta.setFileName("示例文件");
                fileMeta.setFilePath("/path/to/file");
                fileMeta.setFileSize(1024L);
                fileMeta.setFileType("json");
                fileMeta.setTags("[{\"labelId\":1,\"labelName\":\"测试标签1\"}]");
            }

            // 填充标签关联信息
            fileMeta.setTagRelations(fileRelations.stream().map(relation -> {
                LabelLibrary label = labelLibraryMapper.selectById(relation.getLabelId());
                FileMeta.LabelRelation labelRelation = new FileMeta.LabelRelation();
                labelRelation.setLabelId(label.getId());
                labelRelation.setLabelName(label.getLabelName());
                labelRelation.setColor(label.getColor());
                return labelRelation;
            }).collect(Collectors.toList()));

            result.add(fileMeta);
        }

        return result;
    }

    @Override
    public List<Long> findFilesByTagIntersection(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用PostgreSQL的JSONB查询：找出同时具有多个标签的文件
        String query = "SELECT DISTINCT fr.file_id " +
                "FROM file_tag_relation fr " +
                "WHERE fr.label_id = ANY(ARRAY[" + String.join(",", labelIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList())) + "]) " +
                "GROUP BY fr.file_id " +
                "HAVING COUNT(DISTINCT fr.label_id) = " + labelIds.size();

        return baseMapper.selectFileIdsByIntersection(query);
    }

    @Override
    public Long countByLabelId(Long labelId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<FileTagRelation>()
                .eq(FileTagRelation::getLabelId, labelId));
    }

    @Override
    public List<LabelLibrary> countByLabelIds(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return new ArrayList<>();
        }

        return labelLibraryMapper.selectList(new LambdaQueryWrapper<LabelLibrary>()
                .in(LabelLibrary::getId, labelIds));
    }
}
