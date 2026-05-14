package com.labelcode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labelcode.entity.DatasetConfig;
import com.labelcode.mapper.DatasetConfigMapper;
import com.labelcode.service.DatasetConfigService;
import org.springframework.stereotype.Service;

@Service
public class DatasetConfigServiceImpl extends ServiceImpl<DatasetConfigMapper, DatasetConfig> implements DatasetConfigService {
}
