package com.labelcode.controller;

import com.labelcode.dto.FileTagRelationQuery;
import com.labelcode.entity.FileMeta;
import com.labelcode.entity.FileTagRelation;
import com.labelcode.service.IFileTagRelationService;
//import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件标签关联Controller
 * 提供文件与标签的关联管理
 */
@RestController
@RequestMapping("/file-tags")
public class FileTagRelationController {

    private final IFileTagRelationService fileTagRelationService;

    public FileTagRelationController(IFileTagRelationService fileTagRelationService) {
        this.fileTagRelationService = fileTagRelationService;
    }

    /**
     * 为文件添加标签
     */
    @PostMapping
    public ResponseEntity<FileTagRelation> addTagTo(
            @RequestParam Long fileId,
            @RequestParam Long labelId) {
        FileTagRelation relation = fileTagRelationService.addTagTo(fileId, labelId);
        return ResponseEntity.ok(relation);
    }

    /**
     * 批量为文件添加标签
     */
    @PostMapping("/batch")
    public ResponseEntity<Void> addTagsTo(
            @RequestParam Long fileId,
            @RequestBody List<Long> labelIds) {
        fileTagRelationService.addTagsTo(fileId, labelIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 从文件移除标签
     */
    @DeleteMapping("/{fileId}/{labelId}")
    public ResponseEntity<Void> removeTagFrom(
            @PathVariable Long fileId,
            @PathVariable Long labelId) {
        fileTagRelationService.removeTagFrom(fileId, labelId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量从文件移除标签
     */
    @DeleteMapping("/{fileId}/batch")
    public ResponseEntity<Void> removeTagsFrom(
            @PathVariable Long fileId,
            @RequestBody List<Long> labelIds) {
        fileTagRelationService.removeTagsFrom(fileId, labelIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询文件的标签列表
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<List<FileTagRelation>> getTagsByFile(@PathVariable Long fileId) {
        List<FileTagRelation> relations = fileTagRelationService.getTagsByFile(fileId);
        return ResponseEntity.ok(relations);
    }

    /**
     * 查询具有指定标签的文件列表
     */
    @GetMapping("/files-by-label/{labelId}")
    public ResponseEntity<List<FileTagRelation>> getFilesByLabel(@PathVariable Long labelId) {
        List<FileTagRelation> relations = fileTagRelationService.getFilesByLabel(labelId);
        return ResponseEntity.ok(relations);
    }

    /**
     * 查询文件的标签信息（包含标签详情）
     */
    @GetMapping("/file/{fileId}")
    public ResponseEntity<FileMeta> getFileWithTags(@PathVariable Long fileId) {
        FileMeta fileMeta = fileTagRelationService.getFileWithTags(fileId);
        return ResponseEntity.ok(fileMeta);
    }

    /**
     * 批量查询文件的标签信息
     */
    @GetMapping("/files")
    public ResponseEntity<List<FileMeta>> getFilesWithTags(@RequestParam List<Long> fileIds) {
        List<FileMeta> fileMetas = fileTagRelationService.getFilesWithTags(fileIds);
        return ResponseEntity.ok(fileMetas);
    }

    /**
     * 多标签交集查询
     * 查询同时具有多个标签的文件
     */
    @PostMapping("/search/intersection")
    public ResponseEntity<List<Long>> findFilesByTagIntersection(
//            @Valid @RequestBody FileTagRelationQuery query) {
             @RequestBody FileTagRelationQuery query) {
        List<Long> fileIds = fileTagRelationService.findFilesByTagIntersection(query.getLabelIds());
        return ResponseEntity.ok(fileIds);
    }

    /**
     * 统计标签的使用次数
     */
    @GetMapping("/count/{labelId}")
    public ResponseEntity<Map<String, Object>> countByLabelId(@PathVariable Long labelId) {
        Long count = fileTagRelationService.countByLabelId(labelId);
        return ResponseEntity.ok(Map.of(
                "labelId", labelId,
                "count", count
        ));
    }

    /**
     * 批量统计标签的使用次数
     */
    @GetMapping("/count/batch")
    public ResponseEntity<List<com.labelcode.entity.LabelLibrary>> countByLabelIds(@RequestParam List<Long> labelIds) {
        List<com.labelcode.entity.LabelLibrary> labels = fileTagRelationService.countByLabelIds(labelIds);
        return ResponseEntity.ok(labels);
    }
}
