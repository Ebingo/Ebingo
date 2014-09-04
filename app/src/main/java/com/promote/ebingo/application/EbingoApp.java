package com.promote.ebingo.application;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.promote.ebingo.util.ContextUtil;

import java.io.File;

/**
 * Created by Administrator on 2014/8/21.
 */
public class EbingoApp extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        initImageLoaderConfiguration();
        ContextUtil.init(getApplicationContext());
    }

    /**
     * 配置imgloader.
     */
    public void initImageLoaderConfiguration() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(1 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(Thread.NORM_PRIORITY -2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(
                        new UnlimitedDiscCache(new File(Constant.IMG_DIR
                        ))
                )
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);

    }

}
