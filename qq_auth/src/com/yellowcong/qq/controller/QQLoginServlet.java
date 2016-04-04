package com.yellowcong.qq.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qq.connect.oauth.Oauth;

public class QQLoginServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		try {
			String  str = new Oauth().getAuthorizeURL(req);
			System.out.println(str);
			//https://graph.qq.com/oauth2.0/authorize?client_id=101300949&redirect_uri=http://yellowcong.wicp.net/qq_auth/login.jsp&response_type=code&state=29f36c4f9021acfddf420d5071c18441&scope=get_user_info,add_topic,add_one_blog,add_album,upload_pic,list_album,add_share,check_page_fans,add_t,add_pic_t,del_t,get_repost_list,get_info,get_other_info,get_fanslist,get_idollist,add_idol,del_ido,get_tenpay_addr
			resp.sendRedirect(new Oauth().getAuthorizeURL(req));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		  doGet(req,  resp);
	}

}
