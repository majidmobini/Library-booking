package com.ict.classes;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Set_font {

	private static Typeface robotoTypeFace;


	public static void setKodakFont(Context context, View view)
	{
		if (robotoTypeFace == null)
		{
				robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "yekan.ttf");
		}
		setFont(view, robotoTypeFace);
	}
	private static void setFont (View view, Typeface robotoTypeFace)
	{
		if (view instanceof ViewGroup)
		{
			for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
			{
				setFont(((ViewGroup)view).getChildAt(i), robotoTypeFace);
			}
		}
		else if (view instanceof TextView)
		{
			((TextView) view).setTypeface(robotoTypeFace);
		}
	}
}