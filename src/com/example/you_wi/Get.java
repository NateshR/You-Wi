package com.example.you_wi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import edu.sfsu.cs.orange.ocr.R;

public class Get extends Activity {

	TextView tv;
	ListView lv;
	String got;
	private String TAG = "Get";

	private static YouTube youtube;
	private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
	int i = 0;
	ResourceId rID;
	Thumbnail[] thumbnail = new Thumbnail[10];
	String[] title = new String[10];
	String[] id = new String[10];
	String[] link = new String[10];
	Bundle basket = new Bundle();

	private static String url;
	private Dialog pDialog;
	private String TAG_PARSE = "parse";
	private String TAG_TEXT = "text";
	private String TAG_DEBUG = "wiki";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.tvWiki);
		tv.setMovementMethod(new ScrollingMovementMethod());
		lv = (ListView) findViewById(R.id.lvYou);

		Bundle gotBasktet = getIntent().getExtras();
		got = gotBasktet.getString("input");
		Log.d(TAG, "Got Text: " + got);
		new GetContent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, got);
		new PostTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, got);

	}

	private class GetContent extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Aut-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Get.this);
			((ProgressDialog) pDialog).setMessage("Please Wait..");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			tv.setText(result);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			ServiceHandler sh = new ServiceHandler();
			url = "http://en.wikipedia.org/w/api.php?action=parse&page="
					+ params[0] + "&prop=text&section=0&format=json";
			String jsonURL = sh.makeServiceCall(url, ServiceHandler.GET);
			String text = "";
			if (jsonURL != null) {

				try {
					JSONObject obj = new JSONObject(jsonURL);
					JSONObject objParse = obj.getJSONObject(TAG_PARSE);
					JSONObject objText = objParse.getJSONObject(TAG_TEXT);
					String text_html = objText.getString("*");

					Document text_document = Jsoup.parse(text_html);
					Elements pElements = text_document.select("p");
					for (Element e : pElements) {
						text = text + "\n" + e.text();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block6
					e.printStackTrace();
				}
			} else {
				Log.d(TAG_DEBUG, "Null response");

			}
			return text;
		}
	}

	private final class PostTask extends AsyncTask<String, Void, String[]> {

		protected String[] doInBackground(String... params) {
			try {
				youtube = new YouTube.Builder(new NetHttpTransport(),
						new JacksonFactory(), new HttpRequestInitializer() {

							@Override
							public void initialize(
									com.google.api.client.http.HttpRequest arg0)
									throws IOException {
								// TODO Auto-generated method stub

							}
						}).setApplicationName("TextTest").build();

				YouTube.Search.List search = youtube.search()
						.list("id,snippet");
				String apiKey = "AIzaSyAMYURG_meBDF81wPiUX16LW0vbgcl08-s";
				search.setKey(apiKey);
				search.setQ(params[0]);
				search.setType("video");
				search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
				search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

				SearchListResponse searchResponse = search.execute();
				List<SearchResult> searchResultList = searchResponse.getItems();
				Iterator<SearchResult> iterator = null;
				if (searchResultList != null) {
					iterator = searchResultList.iterator();
				}
				while (iterator.hasNext()) {
					SearchResult result = iterator.next();
					rID = result.getId();

					if (rID.getKind().equals("youtube#video")) {
						// thumbnail[i] = new String();
						thumbnail[i] = result.getSnippet().getThumbnails()
								.getDefault();
						title[i] = new String();
						id[i] = new String();
						link[i] = new String();

						title[i] = result.getSnippet().getTitle();
						id[i] = result.getId().getVideoId();
						link[i] = "https://www.youtube.com/watch?v=" + id[i];
						Log.d("youtube:", id[i]);

					}
					i++;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return title;
		}

		@Override
		protected void onPostExecute(String[] xyz) {
			// TODO Auto-generated method stub
			super.onPostExecute(xyz);
			final ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < xyz.length; ++i) {
				list.add(xyz[i]);
			}
			final StableArrayAdapter adapter = new StableArrayAdapter(Get.this,
					android.R.layout.simple_list_item_1, list);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					String url = link[position];
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
		}

	}
}
