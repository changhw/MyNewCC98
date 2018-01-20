package org.cc98.mycc98.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.orhanobut.logger.Logger;

import org.cc98.mycc98.R;
import org.cc98.mycc98.activity.base.BaseActivity;
import org.cc98.mycc98.activity.base.BaseSwipeBackActivity;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PhotoViewActivity extends BaseActivity {
    private static final String TAG = "PhotoViewActivity";

    public static final String URL_KEY = "URL_KEY";

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    String url;

    @BindView(R.id.activity_photoview)
    PhotoView activityPhotoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        url = bundle.getString(URL_KEY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("PhotoView");

        try {
            Glide.with(this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.image_load_error)// 占位图片
                    .crossFade(500)//淡入淡出动画500ms
                    .fitCenter()
                    .priority(Priority.LOW)// 当前线程的优先级
                    .into(activityPhotoview);
        } catch (Exception e) {
            loge(e, "Glide load custom failed");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_photo_view_save:

                item.setEnabled(false);
                Observable.create(new FileSaver(getViewBitMap(activityPhotoview)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new FilesaveObserver(item));
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    class FilesaveObserver implements Observer<String> {
        public FilesaveObserver(MenuItem menuItem) {
            this.menuItem = menuItem;
        }

        private MenuItem menuItem;

        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable e) {
            Logger.t(TAG).e(e, "Filesaver error");
            menuItem.setEnabled(true);
            mkToast("保存失败");
        }

        @Override
        public void onNext(String s) {
            menuItem.setEnabled(true);
            mkToast(s);
        }
    }


    private Bitmap getViewBitMap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    protected class FileSaver implements Observable.OnSubscribe<String> {
        private static final String TAG = "FileSaver";
        private static final String SUF_FIX = ".jpg";

        public FileSaver(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
        }

        private Bitmap mBitmap;

        @Override
        public void call(Subscriber<? super String> subscriber) {
            String filename = String.valueOf(System.currentTimeMillis()).concat(SUF_FIX);
            File fileToSave = new File(Environment.getExternalStorageDirectory().getPath(), filename);
            if (fileToSave.exists()) {
                fileToSave.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(fileToSave);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                out.flush();
                out.close();
                Log.i(TAG, "photo saved");
                subscriber.onNext(fileToSave.getPath());
                subscriber.onCompleted();
            } catch (Exception e) {

                subscriber.onError(e);
            }


        }
    }


}