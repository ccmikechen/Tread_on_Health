package edu.kuas.mis.wmc.service.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import edu.kuas.mis.wmc.app.Util;

import android.os.AsyncTask;

public class AssistantServiceClient {
	private String result = null;
	
	public ResponseErrorCode doHttpGet(String host, int port, String file, String params) {
		int times = 0;
		result = null;
		new HttpRequestAsyncTask().execute("http://" + host + ":" + port + "/" + file + "?" + params);
		while(result == null) {
			try {
				//Util.log("Wait 25 ms.");
				Thread.sleep(25);
				times++;
				if(times > 200) {
					Util.log("Server doesn't response!");
					result = ResponseErrorCodeJsonParser.generateResponseString(new ResponseErrorCode(ResponseErrorCodeDefine.NO_RESPONSE));
					break;
				}
			} catch(Exception e) {
				Util.log(e.getMessage());
			}
		}
		ResponseErrorCodeJsonParser rejp = new ResponseErrorCodeJsonParser(result);
		return rejp.getErrorCode();
    }
	
	private class HttpRequestAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            makeGetRequest(objects[0].toString());
            return null;
        }

        private void makeGetRequest(String target) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpPost = new HttpGet(target);
			HttpResponse response;
			try {
				response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				result = responseString;
			} catch(ClientProtocolException e) {
				Util.log(e.getMessage());
				result = ResponseErrorCodeJsonParser.generateResponseString(new ResponseErrorCode(ResponseErrorCodeDefine.EXCEPTION));
			} catch(IOException e) {
				Util.log(e.getMessage());
				result = ResponseErrorCodeJsonParser.generateResponseString(new ResponseErrorCode(ResponseErrorCodeDefine.EXCEPTION));
			}
		}
	}
}
