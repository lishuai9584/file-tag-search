package com.labelcode.controller;

import com.labelcode.entity.DatasetConfig;
import com.labelcode.service.DatasetConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasets")
@CrossOrigin(origins = "*") // 支持前端调试
public class DatasetConfigController {



    @Autowired
    private DatasetConfigService datasetService;

    @GetMapping
    public List<DatasetConfig> getAllDatasets() {
        return datasetService.list();
    }

    @PostMapping
    public DatasetConfig createDataset(@RequestBody DatasetConfig dataset) {
        datasetService.save(dataset);
        return dataset;
    }

    @DeleteMapping("/{id}")
    public void deleteDataset(@PathVariable Long id) {
        datasetService.removeById(id);
    }
}
