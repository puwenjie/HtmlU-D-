package com.servlet;

public class UploadState {
	private String id;//�ϴ����
	private int state;//�ϴ�״̬ 0 ���� -1 ���� 1 �ϴ����
	private String errormsg;//������Ϣ
	private String targetfile;//Ŀ���ļ�
	private long totalsize;//�ļ��ܴ�С
	private long uploadsize;//���ϴ���С
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTargetfile() {
		return targetfile;
	}
	public void setTargetfile(String targetfile) {
		this.targetfile = targetfile;
	}
	/**
	 * �ϴ�״̬ 0 ���� -1 ���� 1 �ϴ����
	 * @param state
	 */
	public int getState() {
		return state;
	}
	/**
	 * �ϴ�״̬ 0 ���� -1 ���� 1 �ϴ����
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public long getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(long totalsize) {
		this.totalsize = totalsize;
	}
	public long getUploadsize() {
		return uploadsize;
	}
	public void setUploadsize(long uploadsize) {
		this.uploadsize = uploadsize;
	}
	
}
