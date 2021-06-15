package com.java.coding.assignment.model;

public class ErrorModel {
 private String errorMessage;

public ErrorModel(String message) {
	super();
	this.errorMessage = message;
}

public String getErrorMessage() {
	return errorMessage;
}

public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
}
 
}
