package com.java.coding.assignment.model;

public class AssignmentModelResponse {
	private Integer ID;
	private String Sender;
	private String Receiver;
	private Integer TotalAmount;
	private Long TotalPaidAmount;
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getSender() {
		return Sender;
	}
	public void setSender(String sender) {
		Sender = sender;
	}
	public String getReceiver() {
		return Receiver;
	}
	public void setReceiver(String receiver) {
		Receiver = receiver;
	}
	public Integer getTotalAmount() {
		return TotalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		TotalAmount = totalAmount;
	}
	public Long getTotalPaidAmount() {
		return TotalPaidAmount;
	}
	public void setTotalPaidAmount(Long totalPaidAmount) {
		TotalPaidAmount = totalPaidAmount;
	}
	
	
	
}
