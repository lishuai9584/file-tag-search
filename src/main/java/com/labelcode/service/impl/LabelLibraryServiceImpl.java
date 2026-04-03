package com.labelcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labelcode.dto.LabelDTO;
import com.labelcode.entity.FileTagRelation;
import com.labelcode.entity.LabelLibrary;
import com.labelcode.mapper.FileTagRelationMapper;
import com.labelcode.mapper.LabelLibraryMapper;
import com.labelcode.service.ILabelLibraryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class LabelLibraryServiceImpl extends ServiceImpl<LabelLibraryMapper, LabelLibrary>
        implements ILabelLibraryService {

    private final FileTagRelationMapper fileTagRelationMapper;
    private final com.labelcode.mapper.FileMetaMapper fileMetaMapper;

    public LabelLibraryServiceImpl(FileTagRelationMapper fileTagRelationMapper, com.labelcode.mapper.FileMetaMapper fileMetaMapper) {
        this.fileTagRelationMapper = fileTagRelationMapper;
        this.fileMetaMapper = fileMetaMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelLibrary createLabel(LabelDTO labelDTO) {
        // 检查标签名是否已存在
        LabelLibrary existing = getByName(labelDTO.getLabelName());
        if (existing != null) {
            throw new RuntimeException("标签名称已存在: " + labelDTO.getLabelName());
        }

        LabelLibrary label = new LabelLibrary();
        BeanUtils.copyProperties(labelDTO, label);
        label.setCreatedAt(LocalDateTime.now());
        label.setUpdatedAt(LocalDateTime.now());

        // 设置默认值
        if (label.getColor() == null || label.getColor().isEmpty()) {
            label.setColor("#4F46E5");
        }
        if (label.getSortOrder() == null) {
            label.setSortOrder(0);
        }
        if (label.getTagCount() == null) {
            label.setTagCount(0L);
        }

        save(label);
        return label;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelLibrary updateLabel(Long id, LabelDTO labelDTO) {
        LabelLibrary label = getByIdWithCheck(id);

        // 检查标签名是否被其他标签使用
        LabelLibrary existing = getByName(labelDTO.getLabelName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new RuntimeException("标签名称已存在: " + labelDTO.getLabelName());
        }

        label.setLabelName(labelDTO.getLabelName());
        label.setColor(labelDTO.getColor());
        label.setSortOrder(labelDTO.getSortOrder());
        label.setDescription(labelDTO.getDescription());
        label.setUpdatedAt(LocalDateTime.now());

        updateById(label);
        return label;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLabel(Long id) {
        LabelLibrary label = getByIdWithCheck(id);

        // 检查是否有文件关联
        Long tagCount = fileTagRelationMapper.selectCount(new LambdaQueryWrapper<FileTagRelation>()
                .eq(FileTagRelation::getLabelId, id));

        if (tagCount > 0) {
            throw new RuntimeException("该标签仍有关联文件，无法删除。请先移除所有关联或使用合并功能。");
        }

        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLabels(List<Long> ids) {
        for (Long id : ids) {
            try {
                deleteLabel(id);
            } catch (Exception e) {
                throw new RuntimeException("删除标签失败: " + id, e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeTags(Long oldLabelId, Long newLabelId) {
        if (oldLabelId.equals(newLabelId)) {
            throw new RuntimeException("目标标签和源标签不能相同");
        }

        LabelLibrary oldLabel = getByIdWithCheck(oldLabelId);
        LabelLibrary newLabel = getByIdWithCheck(newLabelId);

        // 1. 处理关联关系表 (file_tag_relation) - ID 层面
        fileTagRelationMapper.deleteConflictsOnMerge(oldLabelId, newLabelId);

        fileTagRelationMapper.update(null,
                new LambdaUpdateWrapper<FileTagRelation>()
                        .eq(FileTagRelation::getLabelId, oldLabelId)
                        .set(FileTagRelation::getLabelId, newLabelId));

        // 2. 处理冗余字段 (file_meta.tags JSONB) - 回退到同步版本以追求最高吞吐量
        fileMetaMapper.updateJsonTagsOnMerge(oldLabelId, newLabelId);

        // 3. 删除旧标签
        baseMapper.deleteById(oldLabelId);

        // 4. 刷新统计计数
        baseMapper.refreshTagCount(newLabelId);
    }

    @Override
    public LabelLibrary getByIdWithCheck(Long id) {
        LabelLibrary label = getById(id);
        if (label == null) {
            throw new RuntimeException("标签不存在: " + id);
        }
        return label;
    }

    @Override
    public List<LabelLibrary> listAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public LabelLibrary getByName(String labelName) {
        return baseMapper.selectByName(labelName);
    }

    @Override
    public List<LabelLibrary> getStatistics() {
        // 使用原生SQL查询标签统计
        return baseMapper.selectList(new LambdaQueryWrapper<LabelLibrary>()
                .orderByDesc(LabelLibrary::getTagCount)
                .orderByAsc(LabelLibrary::getSortOrder));
    }
}
