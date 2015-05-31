package com.example.ui;

import android.widget.LinearLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageButton_define extends LinearLayout {
		
	private  ImageView   imageViewbutton;
	
	private  TextView   textView;

	public ImageButton_define(Context context,AttributeSet attrs) {
		
		super(context,attrs);
		// TODO Auto-generated constructor stub
		
		imageViewbutton = new ImageView(context, attrs);
		
		imageViewbutton.setPadding(0, 0, 0, 0);
		
		textView =new TextView(context, attrs);
		
		//Ë®Æ½¾ÓÖÐ
		textView.setGravity(android.view.Gravity.FILL_HORIZONTAL);
		
		textView.setPadding(0, 5, 0, 0);
		
		textView.setTextSize(20);
		
		setClickable(true);
		
		setFocusable(true);
		
		
		//setBackgroundResource(android.R.drawable.btn_default);
		
		setOrientation(LinearLayout.VERTICAL);
		
		addView(imageViewbutton);
		
		addView(textView);
		
	}

}