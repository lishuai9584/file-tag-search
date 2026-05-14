package com.labelcode.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelcode.entity.FileMeta;
import com.labelcode.service.IFileDatasetRelationService;
import com.labelcode.service.IFileMetaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labelcode.service.IFileTagRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 文件元数据管理Controller
 */
@RestController
@RequestMapping("/files")
public class FileMetaController {



    private final IFileMetaService fileMetaService;
    private final IFileTagRelationService fileTagRelationService;
    private final IFileDatasetRelationService fileDatasetRelationService;
    private final ObjectMapper objectMapper;

    public FileMetaController(IFileMetaService fileMetaService, 
                              IFileTagRelationService fileTagRelationService,
                              IFileDatasetRelationService fileDatasetRelationService,
                              ObjectMapper objectMapper) {
        this.fileMetaService = fileMetaService;
        this.fileTagRelationService = fileTagRelationService;
        this.fileDatasetRelationService = fileDatasetRelationService;
        this.objectMapper = objectMapper;
    }


    /**
     * 获取所有文件 (支持分页以防 OOM)
     */
    @GetMapping
    public ResponseEntity<IPage<FileMeta>> getAllFiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FileMeta> pageParam = new Page<>(page, size);
        return ResponseEntity.ok(fileMetaService.page(pageParam));
    }

    /**
     * 高级搜索：多字段、多标签交集 (支持分页)
     */
    @PostMapping("/search")
    public ResponseEntity<IPage<FileMeta>> advancedSearch(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String datasetId,
            @RequestBody Map<String, Object> query) {
        
        // 兼容前端传来的 "null" 字符串或 0
        Long dsId = (datasetId == null || "null".equals(datasetId) || "0".equals(datasetId)) ? null : Long.valueOf(datasetId);
        
        String fileName = (String) query.get("fileName");
        String fileType = (String) query.get("fileType");
        List<Long> tags = (List<Long>) query.get("tags");
        Map<String, Object> metadata = (Map<String, Object>) query.get("metadata");
        String startDate = (String) query.get("startDate");
        String endDate = (String) query.get("endDate");

        Page<FileMeta> pageParam = new Page<>(page, size);
        // 2. 告诉 MyBatis-Plus：“你少管闲事”，直接跳过它的 `JSqlParser`
        pageParam.setSearchCount(false);
        return ResponseEntity.ok(fileMetaService.advancedSearch(pageParam, fileName, fileType, dsId, tags, metadata, startDate, endDate));
    }




    /**
     * 新增文件及其标签关联
     */
    @PostMapping
    public ResponseEntity<FileMeta> createFile(@RequestBody Map<String, Object> payload) throws JsonProcessingException {
        FileMeta fileMeta = new FileMeta();
        fileMeta.setFileName((String) payload.get("fileName"));
        fileMeta.setFilePath((String) payload.get("filePath"));
        fileMeta.setFileSize(payload.containsKey("fileSize") ? Long.valueOf(payload.get("fileSize").toString()) : 0L);
        fileMeta.setFileType((String) payload.get("fileType"));
        
        if (payload.containsKey("metadata")) {
            try {
                fileMeta.setMetadata(objectMapper.writeValueAsString(payload.get("metadata")));
            } catch (JsonProcessingException e) {
                fileMeta.setMetadata("{}");
            }
        }
        
        // 1. 提取并转换标签 ID (防止类型擦除导致的 ClassCastException)
        List<?> labelIdsRaw = (List<?>) payload.get("labelIds");
        List<Long> labelIds = new ArrayList<>();
        if (labelIdsRaw != null) {
            labelIds = labelIdsRaw.stream()
                .map(o -> Long.valueOf(o.toString()))
                .toList();
        }
        
        try {
            fileMeta.setTags(objectMapper.writeValueAsString(labelIds));
        } catch (JsonProcessingException e) {
            fileMeta.setTags("[]");
        }


        // 2. 提取数据集 ID
        Long datasetId = payload.containsKey("datasetId") && payload.get("datasetId") != null 
                         ? Long.valueOf(payload.get("datasetId").toString()) : null;

        return ResponseEntity.ok(fileMetaService.createFileWithTags(fileMeta, labelIds, datasetId));
    }



    /**
     * 原子接口1：仅为文件关联标签 (单对多)
     */
    @PostMapping("/{id}/tags")
    public ResponseEntity<Void> associateTags(@PathVariable Long id, @RequestBody List<Long> labelIds) {
        if (labelIds != null && !labelIds.isEmpty()) {
            fileTagRelationService.addTagsTo(id, labelIds);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 原子接口2：仅为文件划归数据集 (单对一，物理分区路由)
     */
    @PostMapping("/{id}/dataset")
    public ResponseEntity<Void> linkDataset(@PathVariable Long id, @RequestParam Long datasetId) {
        // 使用注入的 IFileDatasetRelationService
        fileDatasetRelationService.bindDataset(id, datasetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileMetaService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}

