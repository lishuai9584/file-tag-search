package com.labelcode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 标签管理系统API测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LabelCodeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long testLabelId1;
    private static Long testLabelId2;
    private static Long testFileId = 1L;

    @Test
    @Order(1)
    void testCreateLabel() throws Exception {
        Map<String, String> label = new HashMap<>();
        label.put("labelName", "测试标签1");
        label.put("color", "#FF0000");
        label.put("sortOrder", "10");
        label.put("description", "这是一个测试标签");

        mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labelName").value("测试标签1"))
                .andExpect(jsonPath("$.color").value("#FF0000"));

        System.out.println("✓ 创建标签成功");
    }

    @Test
    @Order(2)
    void testCreateLabelDuplicate() throws Exception {
        Map<String, String> label = new HashMap<>();
        label.put("labelName", "测试标签1"); // 重复标签名

        mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isBadRequest()); // 预期失败

        System.out.println("✓ 验证标签重复正确");
    }

    @Test
    @Order(3)
    void testGetLabels() throws Exception {
        mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(1)));

        System.out.println("✓ 查询标签列表成功");
    }

    @Test
    @Order(4)
    void testGetLabelStatistics() throws Exception {
        mockMvc.perform(get("/labels/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].labelName").exists());

        System.out.println("✓ 标签统计成功");
    }

    @Test
    @Order(5)
    void testAddTagToFile() throws Exception {
        mockMvc.perform(post("/file-tags")
                        .param("fileId", testFileId.toString())
                        .param("labelId", "1"))
                .andExpect(status().isOk());

        System.out.println("✓ 为文件添加标签成功");
    }

    @Test
    @Order(6)
    void testAddMultipleTagsToFile() throws Exception {
        mockMvc.perform(post("/file-tags/batch")
                        .param("fileId", testFileId.toString())
                        .param("labelIds", "1,2"))
                .andExpect(status().isOk());

        System.out.println("✓ 为文件批量添加标签成功");
    }

    @Test
    @Order(7)
    void testGetTagsByFile() throws Exception {
        mockMvc.perform(get("/file-tags/{fileId}", testFileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)));

        System.out.println("✓ 查询文件标签成功");
    }

    @Test
    @Order(8)
    void testGetFileWithTags() throws Exception {
        mockMvc.perform(get("/file-tags/file/{fileId}", testFileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFileId))
                .andExpect(jsonPath("$.tagRelations").isArray());

        System.out.println("✓ 获取文件标签详情成功");
    }

    @Test
    @Order(9)
    void testFindFilesByTagIntersection() throws Exception {
        Map<String, List<Long>> request = new HashMap<>();
        request.put("labelIds", Arrays.asList(1L, 2L));

        mockMvc.perform(post("/file-tags/search/intersection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("✓ 多标签交集查询成功");
    }

    @Test
    @Order(10)
    void testCountByLabelId() throws Exception {
        mockMvc.perform(get("/file-tags/count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labelId").value(1L))
                .andExpect(jsonPath("$.count").value(greaterThan(0L)));

        System.out.println("✓ 标签计数成功");
    }

    @Test
    @Order(11)
    void testUpdateLabel() throws Exception {
        Map<String, String> label = new HashMap<>();
        label.put("labelName", "测试标签1更新");
        label.put("color", "#00FF00");
        label.put("sortOrder", "20");

        mockMvc.perform(put("/labels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labelName").value("测试标签1更新"));

        System.out.println("✓ 更新标签成功");
    }

    @Test
    @Order(12)
    void testRemoveTagFromFile() throws Exception {
        mockMvc.perform(delete("/file-tags/{fileId}/{labelId}", testFileId, "1"))
                .andExpect(status().isNoContent());

        System.out.println("✓ 移除文件标签成功");
    }

    @Test
    @Order(13)
    void testGetFilesByLabel() throws Exception {
        mockMvc.perform(get("/file-tags/files-by-label/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        System.out.println("✓ 查询具有标签的文件成功");
    }

    @Test
    @Order(14)
    void testMergeTags() throws Exception {
        // 先创建测试标签
        Map<String, String> label1 = new HashMap<>();
        label1.put("labelName", "待合并标签A");
        label1.put("color", "#FF0000");
        String response1 = mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, String> label2 = new HashMap<>();
        label2.put("labelName", "待合并标签B");
        label2.put("color", "#00FF00");
        String response2 = mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label2)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 合并标签A到标签B
        Map<String, Long> mergeRequest = new HashMap<>();
        mergeRequest.put("oldLabelId", 1L);
        mergeRequest.put("newLabelId", 2L);

        mockMvc.perform(post("/labels/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk());

        System.out.println("✓ 标签合并成功");
    }

    @Test
    @Order(15)
    void testDeleteLabel() throws Exception {
        mockMvc.perform(delete("/labels/2"))
                .andExpect(status().isNoContent());

        System.out.println("✓ 删除标签成功");
    }

    @Test
    @Order(16)
    void testGetAllLabels() throws Exception {
        mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(2)));

        System.out.println("✓ 查询所有标签成功");
    }
}
