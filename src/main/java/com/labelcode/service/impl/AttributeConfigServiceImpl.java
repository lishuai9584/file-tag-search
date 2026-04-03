package com.labelcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labelcode.entity.AttributeConfig;
import com.labelcode.mapper.AttributeConfigMapper;
import com.labelcode.service.AttributeConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeConfigServiceImpl extends ServiceImpl<AttributeConfigMapper, AttributeConfig> implements AttributeConfigService {

    @Override
    public List<AttributeConfig> findByDatasetId(Long datasetId, Boolean includeGlobal) {
        LambdaQueryWrapper<AttributeConfig> query = new LambdaQueryWrapper<AttributeConfig>()
                .eq(AttributeConfig::getDatasetId, datasetId);
        
        if (Boolean.TRUE.equals(includeGlobal)) {
            query.or().eq(AttributeConfig::getIsGlobal, true);
        }
        
        return baseMapper.selectList(query);
    }

    @Override
    public List<AttributeConfig> findGlobalAttributes() {
        return baseMapper.selectList(new LambdaQueryWrapper<AttributeConfig>()
                .eq(AttributeConfig::getIsGlobal, true));
    }
}
