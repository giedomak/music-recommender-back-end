package com.crawler;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class testDeezer {

	public static void main(String[] args) {
		try {
			String accessToken = "fruKWgozs95433dfa93c7dd39V7lUdZ5433dfa93c81b0YIsby9";
			
			//Init HTTP Client
			CloseableHttpClient httpclient = HttpClients.createDefault();
	
			//INIT http get client
			HttpGet httpget = new HttpGet("http://api.deezer.com/user/me?access_token="+accessToken);
	
			//httpget.set
			//httpget.setParams(params);
			//Set accesToken header, use adHeader for a second one.
			//httpget.setHeader("Authorization","Bearer "+ accessToken);
	
			//INIT Respone and execute request 
			CloseableHttpResponse response = httpclient.execute(httpget);
	
			//Get response
			HttpEntity entity = response.getEntity();
	
			//Convert IOStream to String
			String content = IOUtils.toString(entity.getContent(), "UTF-8");
		
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
