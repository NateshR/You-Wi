package com.example.you_wi;

import edu.sfsu.cs.orange.ocr.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity implements OnClickListener {

	EditText et;
	Button b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		et = (EditText) findViewById(R.id.etGet);
		b = (Button) findViewById(R.id.bSubmit);
		b.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String input = et.getText().toString();
		Bundle basket = new Bundle();
		basket.putString("input", input);
		Intent i = new Intent(Main.this, Get.class);
		i.putExtras(basket);
		startActivity(i);

	}

}
