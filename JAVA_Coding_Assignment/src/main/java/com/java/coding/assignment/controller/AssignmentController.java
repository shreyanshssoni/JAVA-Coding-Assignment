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
import com.java.coding.assignment.model.AssignmentParentModel;
import com.java.coding.assignment.model.ErrorModel;
import com.java.coding.assignment.model.AssignmentModelResponse;

@RestController
public class AssignmentController {

	@GetMapping("/parent/detail/{pageNo}")
	public ResponseEntity<?> getListOFParentDetails(@PathVariable Integer pageNo) {
		if (null == pageNo) {
			throw new RuntimeException("Please enter page no");
		}
		List<AssignmentModelResponse> finalList = new ArrayList<>();
		try {
			List<AssignmentParentModel> langList = getParentData();
			List<AssignmentChildModel> langList1 = getChildData();
			List<AssignmentModelResponse> responseList = getModelResponse(langList, langList1);

			Collections.sort(responseList, (o1, o2) -> o1.getID() - o2.getID());
			finalList = getPages(responseList, pageNo);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorModel(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(finalList, HttpStatus.OK);
	}

	@GetMapping("/child/detail/{parentId}")
	public ResponseEntity<?> getListOfChildDetails(@PathVariable Integer parentId) {
		if (null == parentId) {
			throw new RuntimeException("Please enter parentId");
		}
		List<AssignmentModelResponse> responseList = null;
		try {
			List<AssignmentParentModel> langList = getParentData();
			List<AssignmentChildModel> langList1 = getChildData();
			List<AssignmentChildModel> result = langList1.stream().filter(p -> p.getParentId().equals(parentId))
					.collect(Collectors.toList());
			if (null == result || result.isEmpty()) {
				throw new RuntimeException("Given parentId is not present");
			}
			responseList = getModelResponse1(langList, result);
			Collections.sort(responseList, (o1, o2) -> o1.getID() - o2.getID());
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorModel(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	public List<AssignmentChildModel> getChildData()
			throws FileNotFoundException, IOException, ParseException, JsonProcessingException, JsonMappingException {
		JSONParser jsonParser = new JSONParser();
		FileReader reader1 = new FileReader("Child.json");
		JSONObject obj1 = (JSONObject) jsonParser.parse(reader1);

		JSONArray parentList1 = (JSONArray) obj1.get("data");
		ObjectMapper mapper = new ObjectMapper();
		List<AssignmentChildModel> langList1 = mapper.readValue(parentList1.toJSONString(),
				new TypeReference<List<AssignmentChildModel>>() {
				});
		return langList1;
	}

	public List<AssignmentParentModel> getParentData()
			throws FileNotFoundException, IOException, ParseException, JsonProcessingException, JsonMappingException {
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader("Parent.json");
		JSONObject obj = (JSONObject) jsonParser.parse(reader);
		JSONArray parentList = (JSONArray) obj.get("data");
		ObjectMapper mapper = new ObjectMapper();
		List<AssignmentParentModel> langList = mapper.readValue(parentList.toJSONString(),
				new TypeReference<List<AssignmentParentModel>>() {
				});
		return langList;
	}

	public List<AssignmentModelResponse> getPages(List<AssignmentModelResponse> responseList, Integer pageNo) {
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

	private List<AssignmentModelResponse> getModelResponse(List<AssignmentParentModel> langList,
			List<AssignmentChildModel> langList1) {
		List<AssignmentModelResponse> response = new ArrayList<>();

		langList.forEach(action -> {
			AssignmentModelResponse assignmentModelResponse = new AssignmentModelResponse();
			assignmentModelResponse.setID(action.getID());
			assignmentModelResponse.setReceiver(action.getReceiver());
			assignmentModelResponse.setSender(action.getSender());
			assignmentModelResponse.setTotalAmount(action.getTotalAmount());

			List<AssignmentChildModel> result = langList1.stream().filter(p -> p.getParentId().equals(action.getID()))
					.collect(Collectors.toList());
			if (null != result && !result.isEmpty()) {
				Long sum = result.stream().mapToLong(AssignmentChildModel::getPaidAmount).sum();
				assignmentModelResponse.setTotalPaidAmount(sum);
			} else {
				assignmentModelResponse.setTotalPaidAmount(0L);
			}
			response.add(assignmentModelResponse);
		});

		return response;
	}

	private List<AssignmentModelResponse> getModelResponse1(List<AssignmentParentModel> langList,
			List<AssignmentChildModel> langList1) {
		List<AssignmentModelResponse> response = new ArrayList<>();

		langList1.forEach(action -> {
			AssignmentModelResponse assignmentModelResponse = new AssignmentModelResponse();
			assignmentModelResponse.setID(action.getID());
			assignmentModelResponse.setTotalPaidAmount(action.getPaidAmount());

			List<AssignmentParentModel> result = langList.stream().filter(p -> p.getID().equals(action.getParentId()))
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
