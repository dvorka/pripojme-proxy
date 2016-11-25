package com.mindforger.pripojme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class PripojmeProxyServlet extends HttpServlet {

	private static final String PRIPOJME_BASE_URI = "https://api.pripoj.me";
	
	private static final String CONTENT_TYPE_JSON = "application/javascript; charset=utf8";

	private Mashup mashup;
	
	public PripojmeProxyServlet() {
		mashup = new Mashup(50);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url=request.getRequestURI();
		debug("\n---\nRequest URL: "+url);
		String query=request.getQueryString();		
		debug("\n  "+query);
		url+="?"+query;
		
		if(url!=null && !url.isEmpty()) {
			// projects list
			if(url.startsWith("/api/project/get")) {
				String proxyResponse=doPripojmeGET(PRIPOJME_BASE_URI+url.substring(4));
				String mashedResponse=mashup.getProjectListMashup(proxyResponse);
				doPripojmeResponse(response, mashedResponse);
				return;				
			} else {
				// project details
				if(url.startsWith("/api/device/get")) {
					String proxyResponse=doPripojmeGET(PRIPOJME_BASE_URI+url.substring(4));
					String mashedResponse=mashup.getProjectsMashup(proxyResponse);
					doPripojmeResponse(response, mashedResponse);
					return;					
				} else {
					// sensor data
					if(url.startsWith("/api/message/get")) {
						String proxyResponse=doPripojmeGET(PRIPOJME_BASE_URI+url.substring(4));
						String mashedResponse=mashup.getSensorMashup(url,proxyResponse);
						doPripojmeResponse(response, mashedResponse);
						return;
					}					
				}			
			}
		}		
		doFaultResponse(response,url);
	}
	
	private String doPripojmeGET(String url) throws IOException {
		URL proxyUrl = new URL(url);
		HttpURLConnection proxyConn = (HttpURLConnection) proxyUrl.openConnection();
		proxyConn.setRequestMethod("GET");
		proxyConn.setRequestProperty("Accept", CONTENT_TYPE_JSON);
		int responseCode = proxyConn.getResponseCode();
		debug("\nSending 'GET' request to URL : " + url);
		debug("\nResponse Code : " + responseCode);

		BufferedReader proxyIn 
			= new BufferedReader(new InputStreamReader(proxyConn.getInputStream()));
		String inputLine;
		StringBuffer proxyResponse = new StringBuffer();
		while((inputLine = proxyIn.readLine()) != null) {
			proxyResponse.append(inputLine);
		}
		proxyIn.close();
		
		return proxyResponse.toString();
	}

	private void doPripojmeResponse(HttpServletResponse response, String mashup) throws IOException {
		debug("\nResponse:\n"+mashup);
		response.setContentType(CONTENT_TYPE_JSON);
		response.getWriter().print(mashup); // IMPROVE println
	}
	
	private void doFaultResponse(HttpServletResponse response, String url) throws IOException {
		debug("\nFAULT response:");
		response.setContentType(CONTENT_TYPE_JSON);
		response.getWriter().print("'"+url+"' not found"); // IMPROVE println
		response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);										
	}
	
	private void debug(String s) {
		System.out.print(s);		
	}
}
