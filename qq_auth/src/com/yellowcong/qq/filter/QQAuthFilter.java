package com.yellowcong.qq.filter;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.javabeans.weibo.Company;
import com.qq.connect.oauth.Oauth;

public class QQAuthFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filter) throws IOException, ServletException {
		// TODO Auto-generated method stub
		 try {
			 //http://yellowcong.wicp.net/qq_auth/login.jsp?code=440B9C1FFBB9FE2B08D4535229B2F3C1&state=b1224390349e4d840138d8f67df8c368
			//AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(req);
			String code = req.getParameter("code");
			String state = req.getParameter("state");
			//当有数据的情况下 我们发送token
			if(code != null && state != null){
				//获取用户的token
				AccessToken accessToken = (new Oauth()).getAccessTokenByRequest(req);
				String token = accessToken.getAccessToken();
				//获取openid
				OpenID openID=  new OpenID(token);
				//将id存储在数据库中
				String userId = openID.getUserOpenID();
				
				//获取到用户的唯一的id,通过这个id可以用来判断用户 是否注册过,
				//
				System.out.println("用户唯一的id"+userId);
				
				//获取用户QQ　空间中的用户数据
				UserInfo qzoneUserInfo = new UserInfo(token, userId);
                UserInfoBean userInfo = qzoneUserInfo.getUserInfo();
                if(userInfo.getRet() ==0 ){
                	//用户头像  尺寸有 30  50 100 
                	String  head = userInfo.getAvatar().getAvatarURL50();
                	//用户名
                	String username = userInfo.getNickname();
                	//性别
                	String gender = userInfo.getGender();
                	System.out.println("头像"+head);
                	System.out.println("别名"+username);
                	System.out.println("性别"+gender);
                }else{
                	System.out.println("为获取的原因"+userInfo.getMsg());
                }
                
                
                //先从QQ空间获取数据，如果获取不到，就从 微博中获取
                //获取腾讯微博的数据
                System.out.println("-----------------------------腾讯微博中的数据------------------------------------");
                com.qq.connect.api.weibo.UserInfo weiboUserInfo = new com.qq.connect.api.weibo.UserInfo(token,userId);
                com.qq.connect.javabeans.weibo.UserInfoBean weiboUserInfoBean = weiboUserInfo.getUserInfo();
                if (weiboUserInfoBean.getRet() == 0) {
                    //获取用户的微博头像----------------------start
                	System.out.println("头像 "+weiboUserInfoBean.getAvatar().getAvatarURL100());
                    //获取用户的微博头像 ---------------------end

                    //获取用户的生日信息 --------------------start
                	System.out.println("<p>尊敬的用户，你的生日是： " + weiboUserInfoBean.getBirthday().getYear()
                                +  "年" + weiboUserInfoBean.getBirthday().getMonth() + "月" +
                                weiboUserInfoBean.getBirthday().getDay() + "日");
                    //获取用户的生日信息 --------------------end
                    StringBuffer sb = new StringBuffer();
                    sb.append("<p>所在地:" + weiboUserInfoBean.getCountryCode() + "-" + weiboUserInfoBean.getProvinceCode() + "-" + weiboUserInfoBean.getCityCode()
                             + weiboUserInfoBean.getLocation());

                    //获取用户的公司信息---------------------------start
                    ArrayList<Company> companies = weiboUserInfoBean.getCompanies();
                    if (companies.size() > 0) {
                        //有公司信息
                        for (int i=0, j=companies.size(); i<j; i++) {
                            sb.append("<p>曾服役过的公司：公司ID-" + companies.get(i).getID() + " 名称-" +
                            companies.get(i).getCompanyName() + " 部门名称-" + companies.get(i).getDepartmentName() + " 开始工作年-" +
                            companies.get(i).getBeginYear() + " 结束工作年-" + companies.get(i).getEndYear());
                        }
                    } else {
                        //没有公司信息
                    }
                    //获取用户的公司信息---------------------------end

                    System.out.println(sb.toString());
                    
                    

                } else {
                	System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + weiboUserInfoBean.getMsg());
                }
                
                
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			filter.doFilter(req, resp);
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
