package com.elastic.gateway.domain;

import io.searchbox.annotations.JestId;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * Jun 6, 2016 4:31:12,391 PM job/SampleProject/ #39 Started by user Mahesh
 * Rajannan
 * 
 * 
 * Jun 6, 2016 4:31:12,457 PM SampleProject #39 Started by user Mahesh Rajannan
 * on node Jenkins started at 2016-06-06T21:31:12Z completed in 59ms completed:
 * SUCCESS <--INFO: this is the only one being targeted TODO: for later, try
 * some generic way of positing to elastic with minimal object conversion.
 * 
 * Jun 6, 2016 4:30:55,794 PM /configSubmit by maheshrajannan Jun 6, 2016
 * 4:28:58,751 PM /restart by maheshrajannan
 * 
 * @author maheshrajannan
 *
 */
public class CompletedBuild implements Indexable {
	private static final long serialVersionUID = -3971912226293959387L;

	@JestId
	private String id;

	private String projectName;

	private int buildNumber;

	private Date startedAt;

	private String startedBy;

	private String startedNode;

	private String status;

	private long timeTaken;

	/**
	 * @param projectName
	 * @param buildNumber
	 * @param startedAt
	 * @param startedBy
	 * @param startedNode
	 * @param status
	 * @param timeTaken
	 */
	private CompletedBuild(String projectName, int buildNumber, Date startedAt, String startedBy, String startedNode,
			String status, long timeTaken) {
		super();
		this.projectName = projectName;
		this.buildNumber = buildNumber;
		this.startedAt = startedAt;
		this.startedBy = startedBy;
		this.startedNode = startedNode;
		this.status = status;
		this.timeTaken = timeTaken;
	}

	/**
	 * @param projectName
	 * @param startedAt
	 * @param startedBy
	 */
	private CompletedBuild(String projectName, Date startedAt, String startedBy) {
		this(projectName, 1,  startedAt,  startedBy, "Jenkins Server",
				"completed", (new Date()).getTime()-startedAt.getTime());
	}
	
	public CompletedBuild(String projectName, int buildNumber, Date startedAt, String startedBy) {
		this(projectName, buildNumber,  startedAt,  startedBy, "Jenkins Server",
				"completed", (new Date()).getTime()-startedAt.getTime());	}

	/**
	 * @param projectName
	 * @param startedAt
	 * @param startedBy
	 * @return
	 */
	public static CompletedBuild getInstance(String projectName, Date startedAt, String startedBy) {
		return new CompletedBuild(projectName, startedAt, startedBy);
	}
	/**
	 * @param projectName
	 * @param buildNumber
	 * @param startedAt
	 * @param startedBy
	 * @return
	 */
	public static CompletedBuild getInstance(String projectName,int buildNumber, Date startedAt, String startedBy) {
		return new CompletedBuild(projectName,buildNumber, startedAt, startedBy);
	}	/**
	 * @param projectName
	 * @param buildNumber
	 * @param startedAt
	 * @param startedBy
	 * @param startedNode
	 * @param status
	 * @param timeTaken
	 * @return
	 */
	public static CompletedBuild getInstance(String projectName, int buildNumber, Date startedAt, String startedBy, String startedNode,
			String status, long timeTaken) {;
		return new CompletedBuild(projectName, buildNumber, startedAt, startedBy, startedNode, status, timeTaken);
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public String getStartedNode() {
		return startedNode;
	}

	public void setStartedNode(String startedNode) {
		this.startedNode = startedNode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
