package com.tangbo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tangbo on 16/9/18.
 */
public class DataVisitVo {

	@JsonProperty("user_id")
	private Integer userId;

	private String uuid;

	@JsonProperty("visit_time")
	private Long visitTime;

	@JsonProperty("leave_time")
	private Long leaveTime;

	@JsonProperty("visit_page")
	private String visitPage;

	@JsonProperty("visit_type")
	private String visitType; // page,event

	@JsonProperty("visit_param")
	private String visitParam;

	@JsonProperty("phone_version")
	private String phoneVersion;

	@JsonProperty("platform")
	private String platform;

	@JsonProperty("app_version")
	private String appVersion;

	private Long stayTime = 0l;

	private String pageId;

	private String pageTitle;

	private Boolean isVisited = false;

	private Boolean isVisitedBySource = false;

	private Boolean isNotificationClicked = false; // ios push notification, 会有两次点击事件,需要去掉一次

	private Integer showView = 0;

	private Integer day = 0;

	private Integer rank = 0;

	@JsonProperty("source_type")
	private String sourceType = "";

	@JsonProperty("source_id")
	private String sourceId = "";

	private Integer wholeMainId = 0;

	private Integer dayMainId = 0;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Long visitTime) {
		this.visitTime = visitTime;
	}

	public Long getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(Long leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getVisitPage() {
		return visitPage;
	}

	public void setVisitPage(String visitPage) {
		this.visitPage = visitPage;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public String getVisitParam() {
		return visitParam;
	}

	public void setVisitParam(String visitParam) {
		this.visitParam = visitParam;
	}

	public String getPhoneVersion() {
		return phoneVersion;
	}

	public void setPhoneVersion(String phoneVersion) {
		this.phoneVersion = phoneVersion;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public Long getStayTime() {
		return stayTime;
	}

	public void setStayTime(Long stayTime) {
		this.stayTime = stayTime;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public Boolean getVisited() {
		return isVisited;
	}

	public void setVisited(Boolean visited) {
		isVisited = visited;
	}

	public Boolean getVisitedBySource() {
		return isVisitedBySource;
	}

	public void setVisitedBySource(Boolean visitedBySource) {
		isVisitedBySource = visitedBySource;
	}

	public Boolean getNotificationClicked() {
		return isNotificationClicked;
	}

	public void setNotificationClicked(Boolean notificationClicked) {
		isNotificationClicked = notificationClicked;
	}

	public Integer getShowView() {
		return showView;
	}

	public void setShowView(Integer showView) {
		this.showView = showView;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getWholeMainId() {
		return wholeMainId;
	}

	public void setWholeMainId(Integer wholeMainId) {
		this.wholeMainId = wholeMainId;
	}

	public Integer getDayMainId() {
		return dayMainId;
	}

	public void setDayMainId(Integer dayMainId) {
		this.dayMainId = dayMainId;
	}
}
