package com.labelcode.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.labelcode.entity.FileMeta;
import com.labelcode.entity.FileTagRelation;
import com.labelcode.entity.LabelLibrary;
import com.labelcode.mapper.FileTagRelationMapper;
import com.labelcode.mapper.LabelLibraryMapper;
import com.labelcode.service.IFileTagRelationService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签系统验证工具类
 * 用于验证标签系统的正确性
 */
@Component
public class LabelSystemValidator {

    private final LabelLibraryMapper labelLibraryMapper;
    private final FileTagRelationMapper fileTagRelationMapper;
    private final IFileTagRelationService fileTagRelationService;

    public LabelSystemValidator(
            LabelLibraryMapper labelLibraryMapper,
            FileTagRelationMapper fileTagRelationMapper,
            IFileTagRelationService fileTagRelationService) {
        this.labelLibraryMapper = labelLibraryMapper;
        this.fileTagRelationMapper = fileTagRelationMapper;
        this.fileTagRelationService = fileTagRelationService;
    }

    /**
     * 验证系统完整性
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();

        // 1. 验证标签库
        result.addCheck("标签库表数据", checkLabelLibrary());

        // 2. 验证关联关系
        result.addCheck("文件标签关联", checkTagRelations());

        // 3. 验证统计数据
        result.addCheck("标签统计", checkStatistics());

        return result;
    }

    /**
     * 验证标签库数据
     */
    private boolean checkLabelLibrary() {
        long count = labelLibraryMapper.selectCount(null);
        if (count < 5) { // 至少5个测试标签
            System.err.println("⚠ 警告：标签数量不足，预期至少5个");
            return false;
        }

        // 检查每个标签的统计数据
        List<LabelLibrary> labels = labelLibraryMapper.selectList(null);
        for (LabelLibrary label : labels) {
            Long actualCount = fileTagRelationMapper.selectCount(
                    new LambdaQueryWrapper<FileTagRelation>()
                            .eq(FileTagRelation::getLabelId, label.getId()));

            if (!actualCount.equals(label.getTagCount())) {
                System.err.printf("⚠ 标签 '%s' 的统计数据不一致：期望 %d，实际 %d%n",
                        label.getLabelName(), label.getTagCount(), actualCount);
                return false;
            }
        }

        System.out.println("✓ 标签库数据验证通过");
        return true;
    }

    /**
     * 验证文件标签关联
     */
    private boolean checkTagRelations() {
        long count = fileTagRelationMapper.selectCount(null);
        if (count < 5) { // 至少5个测试关联
            System.err.println("⚠ 警告：标签关联数量不足");
            return false;
        }

        // 检查每个文件ID的唯一性
        List<FileTagRelation> relations = fileTagRelationMapper.selectList(null);
        for (FileTagRelation relation : relations) {
            // 验证标签ID是否有效
            LabelLibrary label = labelLibraryMapper.selectById(relation.getLabelId());
            if (label == null) {
                System.err.printf("⚠ 无效的标签ID: %d%n", relation.getLabelId());
                return false;
            }
        }

        System.out.println("✓ 文件标签关联验证通过");
        return true;
    }

    /**
     * 验证统计数据
     */
    private boolean checkStatistics() {
        List<LabelLibrary> labels = labelLibraryMapper.selectList(null);
        for (LabelLibrary label : labels) {
            Long actualCount = fileTagRelationMapper.selectCount(
                    new LambdaQueryWrapper<FileTagRelation>()
                            .eq(FileTagRelation::getLabelId, label.getId()));

            if (!actualCount.equals(label.getTagCount())) {
                System.err.printf("⚠ 标签统计不一致: '%s' (ID: %d)%n",
                        label.getLabelName(), label.getId());
                return false;
            }
        }

        System.out.println("✓ 标签统计验证通过");
        return true;
    }

    /**
     * 打印系统状态
     */
    public void printStatus() {
        System.out.println("\n========== 标签系统状态 ==========");

        // 统计信息
        long labelCount = labelLibraryMapper.selectCount(null);
        long relationCount = fileTagRelationMapper.selectCount(null);
        long fileCount = fileTagRelationMapper.selectList(null).stream()
                .map(FileTagRelation::getFileId)
                .distinct()
                .count();

        System.out.printf("标签总数: %d%n", labelCount);
        System.out.printf("关联关系总数: %d%n", relationCount);
        System.out.printf("文件总数: %d%n", fileCount);

        // 标签使用情况
        System.out.println("\n标签使用统计:");
        List<LabelLibrary> labels = labelLibraryMapper.selectList(null);
        labels.stream()
                .sorted((a, b) -> b.getTagCount().compareTo(a.getTagCount()))
                .forEach(label -> {
                    System.out.printf("  - %s (ID: %d): %d 个文件%n",
                            label.getLabelName(), label.getId(), label.getTagCount());
                });

        System.out.println("==================================\n");
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final List<String> checks = new ArrayList<>();

        public void addCheck(String name, boolean passed) {
            checks.add(name + ": " + (passed ? "✓ 通过" : "✗ 失败"));
        }

        public boolean isValid() {
            return checks.stream().allMatch(s -> s.contains("✓"));
        }

        public void print() {
            System.out.println("\n========== 验证结果 ==========");
            checks.forEach(System.out::println);
            System.out.println("总体: " + (isValid() ? "✓ 所有检查通过" : "✗ 存在问题"));
            System.out.println("================================\n");
        }
    }
}
