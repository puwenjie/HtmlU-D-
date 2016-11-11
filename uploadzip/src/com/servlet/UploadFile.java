package com.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.zip.DeCompressUtil;

public class UploadFile extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String uploadpath = "upload";// �ļ��ϴ���ַ
	private String unpackpath = "";
	private long maxSize = 100 * 1024 * 1024;// ����ļ���С100M
	private String limitfile = "zip,rar";// �����ϴ�����doc,docx,xls,xlsx,ppt,htm,html,txt,
	private Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			req.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html; charset=UTF-8");
			String upid = req.getParameter("upid");
			UploadState state = (UploadState) req.getSession().getAttribute("upstate");
			String s="";
			if (upid!=null&&state != null && upid.equals(state.getId())) {
				s = gson.toJson(state);
			}
			System.out.println(state+" GET��"+upid+"_"+s);
			PrintWriter out = resp.getWriter();
			out.write(s);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			req.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html; charset=UTF-8");
			
			String s="";
			PrintWriter out = resp.getWriter();
			boolean ismulti=ServletFileUpload.isMultipartContent(req);
			if(ismulti){
				HttpSession session=req.getSession();
				UploadState us=(UploadState)session.getAttribute("upstate");
				if (us == null) {
					us = new UploadState();
					session.setAttribute("upstate", us);
				}
				us.setTargetfile("");
				us.setErrormsg("");
				us.setTotalsize(0);
				us.setUploadsize(0);
				us.setState(0);// Ĭ������״̬
				long length = req.getContentLength();
				us.setTotalsize(length);
				if (length > maxSize) {
					setStatusMsg(req, -1, "�ļ����ݳ����������!");
					s = gson.toJson(us);
				} else {
					processFileUpload(req, out);
					s = gson.toJson(us);
				}
			}
			
			out.write(s);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	@SuppressWarnings("unchecked")
	public void processFileUpload(HttpServletRequest request, PrintWriter out)
			throws ServletException, IOException {
		String savePath=this.getServletConfig().getServletContext().getRealPath("/")+uploadpath;
		// ��ʱ�ļ�Ŀ¼
		String tempPath = savePath + "/temp";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String ymd = sdf.format(new Date());
		String path = savePath + "/" + ymd + "/";
		// �����ļ���
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		// ������ʱ�ļ���
		File dirTempFile = new File(tempPath);
		if (!dirTempFile.exists()) {
			dirTempFile.mkdirs();
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(5 * 1024 * 1024); // �趨ʹ���ڴ泬��5Mʱ����������ʱ�ļ����洢����ʱĿ¼�С�
		factory.setRepository(new File(tempPath)); // �趨�洢��ʱ�ļ���Ŀ¼��

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");

		// ����һ�����ȼ�����
		UploadProgressListener progressListener = new UploadProgressListener(
				request);
		upload.setProgressListener(progressListener);
		upload.setFileSizeMax(maxSize);
		try {
			List items = upload.parseRequest(request);
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {  
					 //�������ͨ���ֶ�  
					  String name = item.getFieldName();
					  if(name!=null&&name.equals("unpkpath")){
						  unpackpath = item.getString().trim();
					  }
				}else{
					String fileName = item.getName();
					long fileSize = item.getSize();
						// ����ļ���С
						if (fileSize > maxSize) {
							setStatusMsg(request, -1, "�ļ����ݳ����������!");
							break;
						}
						// �����չ��
						String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
						if (limitfile.indexOf(fileExt) < 0) {
							setStatusMsg(request, -1, "�ϴ��ļ���չ���ǲ��������չ����ֻ����"
									+ limitfile + "��ʽ��!");
							return;
						}
						String newFileName = System.currentTimeMillis() + "_"
								+ new Random().nextInt(1000) + "." + fileExt;
						File uploadedFile = new File(path, newFileName);
						try {
							HttpSession session = request.getSession();
							UploadState state = (UploadState) session
									.getAttribute("upstate");
							state.setTargetfile(uploadedFile.getPath());
							OutputStream os = new FileOutputStream(uploadedFile);
							InputStream is = item.getInputStream();
							byte buf[] = new byte[1024];// �����޸� 1024 ����߶�ȡ�ٶ�
							int length = 0;
							while ((length = is.read(buf)) > 0) {
								os.write(buf, 0, length);
							}
							// �ر���
							os.flush();
							os.close();
							is.close();
						} catch (Exception e) {
							setStatusMsg(request, -1, "�ϴ�ʧ��!");
							return;
						}
						setStatusMsg(request, 1, "�ϴ��ɹ�");
						String unpath=this.getServletConfig().getServletContext().getRealPath("/")+unpackpath;
						File f=new File(unpath);
						if(!f.exists()){
							f.mkdirs();
						}
						try {
							DeCompressUtil.deCompress(uploadedFile.getPath(), unpath);
						} catch (Exception e) {
							e.printStackTrace();
						}
//						AntZip.unzip(uploadedFile.getPath(), unpath);//��ѹ
					}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	private void setStatusMsg(HttpServletRequest request, int error,
			String message) {
		HttpSession session = request.getSession();
		UploadState state = (UploadState) session.getAttribute("upstate");
		state.setState(error);
		state.setErrormsg(message);
	}
}
