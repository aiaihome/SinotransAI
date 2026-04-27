package com.sinotrans.blankbill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinotrans.blankbill.dto.BlankBillSegmentRequest;
import com.sinotrans.blankbill.repository.BlankBillSegmentRepository;
import com.sinotrans.blankbill.repository.SerialNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BlankBillSegmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlankBillSegmentRepository blankBillSegmentRepository;

    @Autowired
    private SerialNumberRepository serialNumberRepository;

    @BeforeEach
    void setUp() {
        serialNumberRepository.deleteAll();
        blankBillSegmentRepository.deleteAll();
    }

    
    @Test
    void testCreateSegment_success() throws Exception {
        BlankBillSegmentRequest request = new BlankBillSegmentRequest();
        request.setCarrierId(1L);
        request.setStartNumber("00000001");
        request.setEndNumber("00000005");
        request.setQuantity(5);
        request.setRemark("测试入库");
        request.setEntryDate(LocalDate.now().toString());

        ResultActions result = mockMvc.perform(post("/api/v1/blank-bill-segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.serialNumbers").isArray());

        // 校验数据库
        assertThat(blankBillSegmentRepository.findAll()).hasSize(1);
        assertThat(serialNumberRepository.findAll()).hasSize(5);
    }

    @Test
    void testCreateSegment_paramInvalid() throws Exception {
        BlankBillSegmentRequest request = new BlankBillSegmentRequest();
        request.setCarrierId(null);
        request.setStartNumber("");
        request.setEndNumber("");
        request.setQuantity(0);
        request.setRemark("");
        request.setEntryDate("");

        ResultActions result = mockMvc.perform(post("/api/v1/blank-bill-segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateSegment_crossConflict() throws Exception {
        // 先插入一个号段
        BlankBillSegmentRequest first = new BlankBillSegmentRequest();
        first.setCarrierId(1L);
        first.setStartNumber("00000010");
        first.setEndNumber("00000020");
        first.setQuantity(11);
        first.setRemark("已存在号段");
        first.setEntryDate(LocalDate.now().toString());
        mockMvc.perform(post("/api/v1/blank-bill-segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)));

        // 再插入交叉号段
        BlankBillSegmentRequest conflict = new BlankBillSegmentRequest();
        conflict.setCarrierId(1L);
        conflict.setStartNumber("00000015");
        conflict.setEndNumber("00000025");
        conflict.setQuantity(11);
        conflict.setRemark("交叉号段");
        conflict.setEntryDate(LocalDate.now().toString());
        ResultActions result = mockMvc.perform(post("/api/v1/blank-bill-segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conflict)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("号码段与已有号段存在交叉"));
    }
}
