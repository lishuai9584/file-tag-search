package com.labelcode.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelcode.dto.LabelDTO;
import com.labelcode.dto.TagMergeRequest;
import com.labelcode.entity.LabelLibrary;
import com.labelcode.service.ILabelLibraryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签管理Controller
 * 提供标签的CRUD操作
 */
@RestController
@RequestMapping("/labels")
public class LabelLibraryController {

    private final ILabelLibraryService labelLibraryService;

    public LabelLibraryController(ILabelLibraryService labelLibraryService) {
        this.labelLibraryService = labelLibraryService;
    }

    /**
     * 创建标签
     */
    @PostMapping
    public ResponseEntity<LabelLibrary> createLabel(@Valid @RequestBody LabelDTO labelDTO) {
        LabelLibrary label = labelLibraryService.createLabel(labelDTO);
        return ResponseEntity.ok(label);
    }

    /**
     * 查询标签列表
     * 支持分页
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listLabels(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<LabelLibrary> pageParam = new Page<>(page, size);
        IPage<LabelLibrary> result = labelLibraryService.page(pageParam);
        return ResponseEntity.ok(Map.of(
                "data", result.getRecords(),
                "total", result.getTotal(),
                "page", page,
                "size", size
        ));
    }

    /**
     * 根据ID获取标签
     */
    @GetMapping("/{id}")
    public ResponseEntity<LabelLibrary> getLabel(@PathVariable Long id) {
        LabelLibrary label = labelLibraryService.getByIdWithCheck(id);
        return ResponseEntity.ok(label);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public ResponseEntity<LabelLibrary> updateLabel(
            @PathVariable Long id,
            @Valid @RequestBody LabelDTO labelDTO) {
        LabelLibrary label = labelLibraryService.updateLabel(id, labelDTO);
        return ResponseEntity.ok(label);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelLibraryService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除标签
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteLabels(@RequestBody List<Long> ids) {
        labelLibraryService.batchDeleteLabels(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 合并标签
     */
    @PostMapping("/merge")
    public ResponseEntity<Void> mergeTags(@Valid @RequestBody TagMergeRequest request) {
        labelLibraryService.mergeTags(request.getOldLabelId(), request.getNewLabelId());
        return ResponseEntity.ok().build();
    }

    /**
     * 标签统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<LabelLibrary>> getStatistics() {
        List<LabelLibrary> statistics = labelLibraryService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
