package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

import com.example.demo.model.document.ProductDocument;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.service.ProductService;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

@SpringBootTest
public class ProductControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;  // 서비스 레이어를 모의로 주입

    @Autowired
    private ObjectMapper objectMapper;  // JSON 객체 변환을 위한 ObjectMapper

    @Test
    void testSearchProductList() throws Exception {
        // 테스트용 데이터 및 DTO 설정
        SearchProductReqDto criteria = new SearchProductReqDto("SAVINGS", "FIXED", 12, "여행");
        Page<ProductDocument> mockPage = new PageImpl<>(Collections.emptyList());

        // ProductService의 searchProducts 메서드가 호출되었을 때 mockPage를 반환하도록 설정
        when(productService.searchProducts(any(SearchProductReqDto.class), any(Pageable.class))).thenReturn(mockPage);

        // JSON 형태의 요청 본문 생성
        String content = objectMapper.writeValueAsString(criteria);

        // POST 요청 테스트 실행
        mockMvc.perform(post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())  // 상태 코드 200 확인
                .andExpect(jsonPath("$.statusCode", is(StatusCode.OK.getCode())))
                .andExpect(jsonPath("$.responseMessage", is(ResponseMessage.READ_PRODUCT_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.content", hasSize(0)));  // 예상되는 응답 본문 검증
    }
}
