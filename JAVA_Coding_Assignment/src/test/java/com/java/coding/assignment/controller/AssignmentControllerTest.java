package com.java.coding.assignment.controller;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AssignmentControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
	public void getListOFParentDetails() throws Exception {
		
		Integer pageno = 2;
		this.mockMvc.perform(get("/parent/detail/"+pageno)).andExpect(status().isOk());
	
	}
	
	@Test
	public void getListOFParentDetailsExceptionTest() throws Exception {
		
		Integer pageno = 555555;
		this.mockMvc.perform(get("/parent/detail/"+pageno)).andExpect(status().isInternalServerError());
	
	}
	
	
	@Test
	public void getListOfChildDetails() throws Exception {
		
		Integer parentId = 2;
		this.mockMvc.perform(get("/child/detail/"+parentId)).andExpect(status().isOk());
	
	}
	
	@Test
	public void getListOfChildDetailsExcceptionTest() throws Exception {
		
		Integer parentId = 555555;
		this.mockMvc.perform(get("/child/detail/"+parentId)).andExpect(status().isInternalServerError());
	
	}
	
	
}
