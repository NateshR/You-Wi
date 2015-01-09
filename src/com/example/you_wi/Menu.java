package com.example.you_wi;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import edu.sfsu.cs.orange.ocr.CaptureActivity;
import edu.sfsu.cs.orange.ocr.R;

public class Menu extends Activity implements OnClickListener {

	private Button input, scan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		setContentView(R.layout.menu);
		
		input = (Button) findViewById(R.id.bInput);
		scan = (Button) findViewById(R.id.bScan);
		input.setOnClickListener(this);
		scan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i;
		switch (v.getId()) {
		case R.id.bInput:
			getWindow().setEnterTransition(new Explode());
			i = new Intent(Menu.this, Main.class);
			startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this)
					.toBundle());
			break;
		case R.id.bScan:
			i = new Intent(Menu.this, CaptureActivity.class);
			startActivity(i);
			break;
		}
	}

}
