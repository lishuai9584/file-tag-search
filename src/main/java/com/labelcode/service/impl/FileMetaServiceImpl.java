package com.labelcode.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labelcode.entity.FileMeta;
import com.labelcode.entity.LabelLibrary;
import com.labelcode.mapper.FileMetaMapper;
import com.labelcode.mapper.LabelLibraryMapper;
import com.labelcode.service.IFileDatasetRelationService;
import com.labelcode.service.IFileMetaService;
import com.labelcode.service.IFileTagRelationService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件元数据服务实现类
 */
@Service
public class FileMetaServiceImpl extends ServiceImpl<FileMetaMapper, FileMeta> implements IFileMetaService {

    private final IFileTagRelationService fileTagRelationService;
    private final IFileDatasetRelationService fileDatasetRelationService;
    private final LabelLibraryMapper labelLibraryMapper;
    private final ObjectMapper objectMapper;

    public FileMetaServiceImpl(IFileTagRelationService fileTagRelationService,
                             IFileDatasetRelationService fileDatasetRelationService,
                             LabelLibraryMapper labelLibraryMapper,
                             ObjectMapper objectMapper) {
        this.fileTagRelationService = fileTagRelationService;
        this.fileDatasetRelationService = fileDatasetRelationService;
        this.labelLibraryMapper = labelLibraryMapper;
        this.objectMapper = objectMapper;
    }


    @Override
    public IPage<FileMeta> advancedSearch(IPage<FileMeta> page, String fileName, String fileType, Long datasetId, List<String> tags, Map<String, Object> metadata, String startDate, String endDate) {
        List<Long> tagIds = new ArrayList<>();
        if (tags != null && !tags.isEmpty()) {
            for (Object tagObj : tags) {
                String tagName = tagObj.toString();
                LabelLibrary label = labelLibraryMapper.selectByName(tagName);
                if (label != null) {
                    tagIds.add(label.getId());
                } else {
                    // 尝试判断是否直接传了 ID
                    try {
                        tagIds.add(Long.valueOf(tagName));
                    } catch (Exception e) {
                        tagIds.add(-1L);
                    }
                }
            }
        }

        
        // ==========================================
        // 核心终极解法：彻底干掉 MyBatis-Plus 拦截器
        // 1. 我们自己调用手写的 Count，完美支持复杂语法，不会有任何报错
        long totalCount = baseMapper.countFilesByAdvancedSearch(fileName, fileType, datasetId, tagIds, metadata, startDate, endDate);
        page.setTotal(totalCount);
        

        // ==========================================
        
        IPage<FileMeta> resultPage = baseMapper.findFilesByAdvancedSearch(page, fileName, fileType, datasetId, tagIds, metadata, startDate, endDate);
        
        // 恢复渲染增强
        if (resultPage.getRecords() != null) {
            Map<Long, LabelLibrary> labelMap = labelLibraryMapper.selectList(null).stream()
                    .collect(Collectors.toMap(LabelLibrary::getId, l -> l));
            
            for (FileMeta file : resultPage.getRecords()) {
                enrichFileTags(file, labelMap);
            }
        }
        
        return resultPage;
    }



    private void enrichFileTags(FileMeta file, Map<Long, LabelLibrary> labelMap) {
        if (file.getTags() == null || file.getTags().equals("[]")) {
            file.setTagRelations(Collections.emptyList());
            return;
        }

        try {
            // 回归原始：解析 JSON 字符串 ID 数组
            List<Long> tagIds = objectMapper.readValue(file.getTags(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
            
            List<FileMeta.LabelRelation> relations = new ArrayList<>();
            for (Long id : tagIds) {
                LabelLibrary label = labelMap.get(id);
                if (label != null) {
                    FileMeta.LabelRelation rel = new FileMeta.LabelRelation();
                    rel.setLabelId(id);
                    rel.setLabelName(label.getLabelName());
                    rel.setColor(label.getColor());
                    relations.add(rel);
                }
            }
            file.setTagRelations(relations);
        } catch (Exception e) {
            file.setTagRelations(Collections.emptyList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileMeta createFileWithTags(FileMeta fileMeta, List<Long> labelIds, Long datasetId) {
        // 1. 保存文件元数据
        if (fileMeta.getId() == null) {
            fileMeta.setId(System.currentTimeMillis());
        }
        
        // 回归原始：手动序列化为 String
        if (labelIds != null && !labelIds.isEmpty()) {
            try { fileMeta.setTags(objectMapper.writeValueAsString(labelIds)); } 
            catch (JsonProcessingException e) { fileMeta.setTags("[]"); }
        } else {
            fileMeta.setTags("[]");
        }

        baseMapper.insertFileWithJsonb(fileMeta);




        // 2. 建立标签关联
        if (labelIds != null && !labelIds.isEmpty()) {
            fileTagRelationService.addTagsTo(fileMeta.getId(), labelIds);
        }

        // 3. 建立数据集物理关联 (划归分区)
        if (datasetId != null) {
            fileDatasetRelationService.bindDataset(fileMeta.getId(), datasetId);
        }

        return fileMeta;
    }

}
