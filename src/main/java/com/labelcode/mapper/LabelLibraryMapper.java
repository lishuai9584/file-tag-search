package com.labelcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelcode.entity.LabelLibrary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 标签库Mapper接口
 */
@Mapper
public interface LabelLibraryMapper extends BaseMapper<LabelLibrary> {

    /**
     * 根据标签名称查询（忽略大小写）
     *
     * @param labelName 标签名称
     * @return 标签对象
     */
    LabelLibrary selectByName(@Param("labelName") String labelName);

    /**
     * 批量查询标签
     *
     * @param ids 标签ID集合
     * @return 标签列表
     */
    List<LabelLibrary> selectBatchLabelsByIds(@Param("ids") Collection<Long> ids);

    /**
     * 刷新标签的使用统计
     */
    void refreshTagCount(@Param("labelId") Long labelId);
}
