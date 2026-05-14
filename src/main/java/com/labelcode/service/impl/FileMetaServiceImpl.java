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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件元数据服务实现类
 */
@Service
public class FileMetaServiceImpl extends ServiceImpl<FileMetaMapper, FileMeta> implements IFileMetaService {

    private static final Logger log = LoggerFactory.getLogger(FileMetaServiceImpl.class);

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
    // 移除 @Transactional 强制由 Parallel 子任务自理会话设置 (避免单个连接阻塞并行作业)
    public IPage<FileMeta> advancedSearch(IPage<FileMeta> page, String fileName, String fileType, Long datasetId, List<Long> tags, Map<String, Object> metadata, String startDate, String endDate) {
        long baseStart = System.currentTimeMillis();
        log.info("[性能监控] 启动并行进阶搜索模式...");

        // 1. 预处理元数据检索串 (解决 CONCAT 导致三元组索引失效的问题)
        final Map<String, String> metaPatterns = new HashMap<>();
        if (metadata != null) {
            metadata.forEach((k, v) -> {
                // 格式：%"key":%"val"%
                metaPatterns.put(k, "%\"" + k + "\":%\"" + v + "\"%");
            });
        }

        // 2. 并行 Count (封顶 10000)
        CompletableFuture<Long> countFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            try {
                baseMapper.setLocalWorkMem();
                long count = baseMapper.countFilesByAdvancedSearch(fileName, fileType, datasetId, tags, metaPatterns, startDate, endDate);
                log.info("[性能监控] 并行 Count 耗时: {}ms, 结果: {}", (System.currentTimeMillis() - start), count);
                return count;
            } catch (Exception e) {
                log.error("Count 并行执行失败", e);
                return 0L;
            }
        });

        // 3. 并行 FindRecords
        CompletableFuture<IPage<FileMeta>> findFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            try {
                baseMapper.setLocalWorkMem();
                IPage<FileMeta> result = baseMapper.findFilesByAdvancedSearch(page, fileName, fileType, datasetId, tags, metaPatterns, startDate, endDate);
                log.info("[性能监控] 并行 FindRecords 耗时: {}ms, 行数: {}", (System.currentTimeMillis() - start), result.getRecords().size());
                return result;
            } catch (Exception e) {
                log.error("FindRecords 并行执行失败", e);
                return null;
            }
        });

        try {
            CompletableFuture.allOf(countFuture, findFuture).join();
            
            Long total = countFuture.get();
            IPage<FileMeta> resultPage = findFuture.get();
            if (resultPage == null) {
                throw new RuntimeException("数据库查询超时或异常");
            }
            resultPage.setTotal(total);

            // 4. 标签渲染增强 (按需提取)
            long enrichStart = System.currentTimeMillis();
            if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
                Set<Long> allTagIds = new HashSet<>();
                for (FileMeta file : resultPage.getRecords()) {
                    try {
                        List<Long> ids = objectMapper.readValue(file.getTags(), 
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                        allTagIds.addAll(ids);
                    } catch (Exception ignored) {}
                }
                
                Map<Long, LabelLibrary> labelMap = new HashMap<>();
                if (!allTagIds.isEmpty()) {
                    labelMap = labelLibraryMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LabelLibrary>()
                            .in("id", allTagIds)
                    ).stream().collect(Collectors.toMap(LabelLibrary::getId, l -> l));
                }
                
                for (FileMeta file : resultPage.getRecords()) {
                    enrichFileTags(file, labelMap);
                }
            }
            log.info("[性能监控] 标签渲染完成耗时: {}ms", (System.currentTimeMillis() - enrichStart));
            log.info("[性能监控] 并行业务总耗时: {}ms", (System.currentTimeMillis() - baseStart));
            
            return resultPage;
        } catch (Exception e) {
            log.error("聚合搜索结果失败", e);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }
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
        
        if (fileMeta.getCreatedAt() == null) {
            fileMeta.setCreatedAt(java.time.LocalDateTime.now());
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
