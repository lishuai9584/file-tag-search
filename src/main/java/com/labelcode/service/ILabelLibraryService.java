package com.labelcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labelcode.dto.LabelDTO;
import com.labelcode.entity.LabelLibrary;

import java.util.List;

/**
 * 标签服务接口
 */
public interface ILabelLibraryService extends IService<LabelLibrary> {

    /**
     * 创建标签
     *
     * @param labelDTO 标签DTO
     * @return 标签对象
     */
    LabelLibrary createLabel(LabelDTO labelDTO);

    /**
     * 更新标签
     *
     * @param id      标签ID
     * @param labelDTO 标签DTO
     * @return 更新后的标签对象
     */
    LabelLibrary updateLabel(Long id, LabelDTO labelDTO);

    /**
     * 删除标签
     * 注意：会级联删除所有关联的标签关系
     *
     * @param id 标签ID
     */
    void deleteLabel(Long id);

    /**
     * 批量删除标签
     *
     * @param ids 标签ID列表
     */
    void batchDeleteLabels(List<Long> ids);

    /**
     * 合并标签
     * 将oldLabelId的所有关联关系转移到newLabelId
     *
     * @param oldLabelId 旧标签ID
     * @param newLabelId 新标签ID
     */
    void mergeTags(Long oldLabelId, Long newLabelId);

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    LabelLibrary getByIdWithCheck(Long id);

    /**
     * 查询所有标签
     *
     * @return 标签列表
     */
    List<LabelLibrary> listAll();

    /**
     * 根据名称查询标签（忽略大小写）
     *
     * @param labelName 标签名称
     * @return 标签对象
     */
    LabelLibrary getByName(String labelName);

    /**
     * 标签统计
     * 按标签名称分组统计使用次数
     *
     * @return 统计结果
     */
    List<LabelLibrary> getStatistics();
}
