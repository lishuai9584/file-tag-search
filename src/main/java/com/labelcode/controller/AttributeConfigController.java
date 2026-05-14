package com.labelcode.controller;

import com.labelcode.entity.AttributeConfig;
import com.labelcode.service.AttributeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@CrossOrigin(origins = "*")
public class AttributeConfigController {



    @Autowired
    private AttributeConfigService attrService;

    @GetMapping("/dataset/{datasetId}")
    public List<AttributeConfig> getAttrsByDataset(@PathVariable Long datasetId, @RequestParam(required = false) Boolean includeGlobal) {
        return attrService.findByDatasetId(datasetId, includeGlobal);
    }

    @GetMapping("/global")
    public List<AttributeConfig> getGlobalAttrs() {
        return attrService.findGlobalAttributes();
    }

    @PostMapping
    public void saveAttribute(@RequestBody AttributeConfig attr) {
        attrService.save(attr);
    }

    @DeleteMapping("/{id}")
    public void deleteAttribute(@PathVariable Long id) {
        attrService.removeById(id);
    }
}
