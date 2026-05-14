package com.labelcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labelcode.entity.AttributeConfig;

import java.util.List;

public interface AttributeConfigService extends IService<AttributeConfig> {
    List<AttributeConfig> findByDatasetId(Long datasetId, Boolean includeGlobal);
    List<AttributeConfig> findGlobalAttributes();
}
