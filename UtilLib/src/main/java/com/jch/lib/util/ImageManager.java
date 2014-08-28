package com.jch.lib.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageManager {

	public static void load(String imgUrl, ImageView imgView) {
		ImageLoader.getInstance().displayImage(imgUrl, imgView);
	}

	public static void load(String imgUrl, ImageView imgView, DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(imgUrl, imgView, options);
	}

}
