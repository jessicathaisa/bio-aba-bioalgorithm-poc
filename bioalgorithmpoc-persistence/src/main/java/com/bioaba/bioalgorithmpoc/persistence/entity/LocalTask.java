package com.bioaba.bioalgorithmpoc.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="localtask")
public class LocalTask {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="algorithmName")
	private String algorithmName;
	
	@Column(name="query")
	private String query;

	@Column(name="parameters")
	@ElementCollection(fetch=FetchType.EAGER)
	private List<LocalTaskParameter> parameters;

	@Column(name="databaseName")
	private String databaseName;
	
	@Column(name="databaseURL")
	private String databaseURL;
	
	@Column(name="callbackURL")
	private String callbackURL;
	
	@Column(name="status")
	private String status;
	
	@Column(name="taskKeyBioABA")
	private String taskKeyBioABA;
	
	@Column(name="result")
	private byte[] result;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	
	public List<LocalTaskParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<LocalTaskParameter> parameters) {
		this.parameters = parameters;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	public String getCallbackURL() {
		return callbackURL;
	}

	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskKeyBioABA() {
		return taskKeyBioABA;
	}

	public void setTaskKeyBioABA(String taskKeyBioABA) {
		this.taskKeyBioABA = taskKeyBioABA;
	}

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	
}
