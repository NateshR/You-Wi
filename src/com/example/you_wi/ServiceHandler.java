package com.example.you_wi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;

public class ServiceHandler {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;

	public ServiceHandler() {

	}

	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	public String makeServiceCall(String url, int method,
			List<NameValuePair> params) {

		int responseCode;
		StringBuilder sb = null;
		try {

			if (method == GET) {
				if (params != null) {
					url = url + "?" + getQuery(params);
				}
				URL urlopen = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) urlopen
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(15000);
				conn.setConnectTimeout(15000);
				conn.setDoInput(true);
				
				conn.connect();
				responseCode = conn.getResponseCode();
				Log.d("DEBUG_TAG", "The response is: " + responseCode);

				BufferedReader bf = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String inputLine;
				sb = new StringBuilder();

				while ((inputLine = bf.readLine()) != null) {
					sb.append(inputLine);
				}
				bf.close();

			} else if (method == POST) {
				URL urlopen = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) urlopen
						.openConnection();
				conn.setRequestMethod("POST");
				conn.setReadTimeout(1500);
				conn.setConnectTimeout(1000);
				conn.connect();
				responseCode = conn.getResponseCode();

				BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(
						conn.getOutputStream(), "UTF-8"));
				bf.write(getQuery(params));
				bf.flush();
				bf.close();

				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String inputLine;
				sb = new StringBuilder();

				while ((inputLine = bfr.readLine()) != null) {
					sb.append(inputLine);
				}
				bfr.close();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	private String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder string = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else {
				string.append("&");
			}

			string.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			string.append("=");
			string.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		return string.toString();
	}
}
