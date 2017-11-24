package com.davinci.wolf.settings.mode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davinci.wolf.R;

/**
 * Created by aakash on 9/28/17.
 */
public class ModeTile extends CardView
	implements View.OnClickListener {
	private ImageView icon = null;
	private int accent = Color.TRANSPARENT;
	private OnClickListener clickListener = null;
	
	public ModeTile(Context context) {
		super(context);
		init(context, null);
	}
	
	public ModeTile(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public ModeTile(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	@Override
	public void setOnClickListener(@Nullable OnClickListener l) {
		this.clickListener = l;
	}
	
	@Override
	public void onClick(View view) {
		if (this.clickListener != null)
			this.clickListener.onClick(this);
	}
	
	@Override
	public void dispatchSetSelected(boolean selected) {
		icon.setColorFilter(selected ? accent : Color.TRANSPARENT);
	}
	
	private void init(Context context, AttributeSet attributeSet) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater == null) return;
		inflater.inflate(R.layout.view_modetile, this);
		if (attributeSet == null) return;
		TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.ModeTile);
		(icon = this.findViewById(R.id.icon)).setImageResource(array.getResourceId(R.styleable.ModeTile_src, R.mipmap.ic_launcher));
		TextView title = this.findViewById(R.id.title);
		title.setText(array.getText(R.styleable.ModeTile_title));
		title.setTextSize(array.getDimensionPixelSize(R.styleable.ModeTile_textSize, sp2px(getContext())));
		array.recycle();
		array = context.obtainStyledAttributes(new TypedValue().data, new int[]{R.attr.colorAccent});
		accent = array.getColor(0, 0);
		icon.setOnClickListener(this);
		title.setOnClickListener(this);
	}
	
	private static int sp2px(Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (float) 4, context.getResources().getDisplayMetrics());
	}

	/*public void setSelected(boolean value) {
		Log.i(TAG, "setSelected: " + this);
		icon.setSelected(value);
		title.setSelected(value);
	}*/
}
