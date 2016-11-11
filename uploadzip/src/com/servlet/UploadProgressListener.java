package com.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.ProgressListener;

public class UploadProgressListener implements ProgressListener {

	private HttpSession session;
	// private long megaBytes = -1;

//	private long maxSize = 100 * 1024 * 1024;// ����ļ���С100M

	public UploadProgressListener(HttpServletRequest request) {

		session = request.getSession();
		UploadState us=(UploadState)session.getAttribute("upstate");
		if (us == null) {
			us = new UploadState();
			session.setAttribute("upstate", us);
		}
		us.setState(0);// Ĭ������״̬
		us.setTotalsize(0);
		us.setId(request.getParameter("upid"));
	}

	/**
	 * 
	 * Ϊ�˽��������������������������� �������,�Ǽ��ٽ������Ļ�� ���磬ֻ�е��ϴ���1���ֽڵ�ʱ��ŷ������û�
	 * 
	 */
	public void update(long pBytesRead, long pContentLength, int pItems) {
		/*
		 * long mBytes = pBytesRead / 1048576; if (megaBytes == mBytes) {
		 * return; } megaBytes = mBytes;
		 */
		UploadState state = (UploadState) session.getAttribute("upstate");
		state.setTotalsize(pContentLength);
		if (pContentLength == -1) {
			state.setErrormsg("�����" + pItems + "���ļ����ϴ�");
			state.setState(1);
			state.setUploadsize(pBytesRead);
		} else {
			state.setErrormsg("�����ϴ���" + pItems + "���ļ�");
			state.setState(0);
			state.setUploadsize(pBytesRead);
		}
	}
}