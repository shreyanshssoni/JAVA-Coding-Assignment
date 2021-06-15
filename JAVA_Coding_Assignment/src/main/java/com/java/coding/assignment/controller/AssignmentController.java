package com.java.coding.assignment.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.coding.assignment.model.AssignmentChildModel;
import com.java.coding.assignment.model.AssignmentModelResponse;
import com.java.coding.assignment.model.AssignmentParentModel;
import com.java.coding.assignment.model.ErrorModel;
/**
 * @author admin
 *
 */
@RestController
public class AssignmentController {

	
	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping("/parent/detail/{pageNo}")
	public ResponseEntity<?> getListOfParentDetails(@PathVariable Integer pageNo) {
		List<AssignmentModelResponse> finalList = new ArrayList<>();
		try {
			//methods for get the data from jsonFiles
			List<AssignmentParentModel> parentList = getParentData();
			List<AssignmentChildModel> childList = getChildData();
			List<AssignmentModelResponse> responseList = getModelResponse(parentList, childList);

			Collections.sort(responseList, (o1, o2) -> o1.getID() - o2.getID());
			//pagination basis on by default 2 pageSize
			finalList = getPages(responseList, pageNo);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorModel(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(finalList, HttpStatus.OK);
	}

	@GetMapping("/child/detail/{parentId}")
	public ResponseEntity<?> getListOfChildDetails(@PathVariable Integer parentId) {
		List<AssignmentModelResponse> responseList = null;
		try {
			//methods for get the data from jsonFiles
			List<AssignmentParentModel> parentList = getParentData();
			List<AssignmentChildModel> childList = getChildData();
			//Filtering data from parentId
			List<AssignmentChildModel> result = childList.stream().filter(p -> p.getParentId().equals(parentId))
					.collect(Collectors.toList());
			if (null == result || result.isEmpty()) {
				throw new RuntimeException("Given parentId is not present");
			}
			responseList = getModelChildResponse(parentList, result);
			Collections.sort(responseList, (o1, o2) -> o1.getID() - o2.getID());
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorModel(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	/**
	 * This method is used for read .json file
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private List<AssignmentParentModel> getParentData()
			throws FileNotFoundException, IOException, ParseException, JsonProcessingException, JsonMappingException {
		JSONParser jsonParser = new JSONParser();
		//reading File from resourcePath
		FileReader reader = new FileReader(new ClassPathResource("jsonfiles/Parent.json").getFile());
		JSONObject obj = (JSONObject) jsonParser.parse(reader);
		JSONArray dataList = (JSONArray) obj.get("data");
		
		List<AssignmentParentModel> parentList = mapper.readValue(dataList.toJSONString(),
				new TypeReference<List<AssignmentParentModel>>() {
				});
		return parentList;
	}

	/**
	 * This method is used for read .json file
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private List<AssignmentChildModel> getChildData()
			throws FileNotFoundException, IOException, ParseException, JsonProcessingException, JsonMappingException {
		JSONParser jsonParser = new JSONParser();
		//reading File from resourcePath
		FileReader reader = new FileReader(new ClassPathResource("jsonfiles/Child.json").getFile());
		JSONObject obj = (JSONObject) jsonParser.parse(reader);
		JSONArray dataList = (JSONArray) obj.get("data");
		
		List<AssignmentChildModel> childList = mapper.readValue(dataList.toJSONString(),
				new TypeReference<List<AssignmentChildModel>>() {
				});
		return childList;
	}

	/**
	 * This method is used for pagination
	 * 
	 * @param responseList
	 * @param pageNo
	 * @return
	 */
	private List<AssignmentModelResponse> getPages(List<AssignmentModelResponse> responseList, Integer pageNo) {
		if (pageNo > Math.ceil((double) responseList.size() / 2)) {
			throw new RuntimeException("Please provide proper page size");
		}
		int fromIndex = 2 * (pageNo - 1);
		int toIndex = 2 * pageNo;
		if (toIndex > responseList.size()) {
			toIndex = responseList.size();
		}
		if (fromIndex > toIndex) {
			fromIndex = toIndex;
		}
		return responseList.subList(fromIndex, toIndex);
	}

	
	/**
	 * This method is used for preparing APIResponse
	 * 
	 * @param parentList
	 * @param childList
	 * @return
	 */
	private List<AssignmentModelResponse> getModelResponse(List<AssignmentParentModel> parentList,
			List<AssignmentChildModel> childList) {
		List<AssignmentModelResponse> response = new ArrayList<>();

		parentList.forEach(action -> {
			AssignmentModelResponse assignmentModelResponse = new AssignmentModelResponse();
			assignmentModelResponse.setID(action.getID());
			assignmentModelResponse.setReceiver(action.getReceiver());
			assignmentModelResponse.setSender(action.getSender());
			assignmentModelResponse.setTotalAmount(action.getTotalAmount());

			List<AssignmentChildModel> result = childList.stream().filter(p -> p.getParentId().equals(action.getID()))
					.collect(Collectors.toList());
			if (null != result && !result.isEmpty()) {
				assignmentModelResponse.setTotalPaidAmount(result.stream().mapToLong(AssignmentChildModel::getPaidAmount).sum());
			} else {
				assignmentModelResponse.setTotalPaidAmount(0L);
			}
			response.add(assignmentModelResponse);
		});

		return response;
	}

	
	/**
	 * This method is used for childataResponse
	 * 
	 * @param parentList
	 * @param childList
	 * @return
	 */
	private List<AssignmentModelResponse> getModelChildResponse(List<AssignmentParentModel> parentList,
			List<AssignmentChildModel> childList) {
		List<AssignmentModelResponse> response = new ArrayList<>();

		childList.forEach(action -> {
			AssignmentModelResponse assignmentModelResponse = new AssignmentModelResponse();
			assignmentModelResponse.setID(action.getID());
			assignmentModelResponse.setTotalPaidAmount(action.getPaidAmount());

			List<AssignmentParentModel> result = parentList.stream().filter(p -> p.getID().equals(action.getParentId()))
					.collect(Collectors.toList());
			result.forEach(action1 -> {
				assignmentModelResponse.setReceiver(action1.getReceiver());
				assignmentModelResponse.setSender(action1.getSender());
				assignmentModelResponse.setTotalAmount(action1.getTotalAmount());
			});
			response.add(assignmentModelResponse);
		});
		return response;
	}
}
