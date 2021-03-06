package org.cc98.mycc98.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.cc98.mycc98.R;
import org.cc98.mycc98.activity.base.BaseWebViewActivity;
import org.cc98.mycc98.utility.ClipBoard;
import org.cc98.mycc98.utility.ShareContent;
import org.cc98.mycc98.utility.StringProcess;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NormalWebviewActivity extends BaseWebViewActivity {
    public static final String URL_KEY = "URLKEY";


    @BindView(R.id.activity_normal_webview_container)
    LinearLayout activityNormalWebviewContainer;


    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, NormalWebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_webview);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        urlToLoad = bundle.getString(URL_KEY, getString(R.string.application_github_website_url));

        mLinearLayout = activityNormalWebviewContainer;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        initWebView(urlToLoad);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_normal_webview, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String currentUrl=webView.getUrl();
        switch (id) {
            case R.id.menu_normalweb_opensys:

                //directly load page to another;
                try{
                    if(StringProcess.isValidUrl(urlToLoad)){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl));
                        startActivity(intent);
                    }
                }catch (Exception e){
                    ClipBoard.copyToClpBoard(this, currentUrl);
                    mkToast(getString(R.string.normal_webview_urlerror));
                }


                break;

            case R.id.menu_normalweb_copylink:
                ClipBoard.copyToClpBoard(this, currentUrl);
                mkToast(getString(R.string.normal_webview_urlcopied));
                break;
            case R.id.menu_normalweb_share:
                String sharedText = String.format(getString(R.string.normal_webview_urlshare_template), getTitle(), currentUrl);
                ShareContent.shareTextDefaultTitle(this, sharedText);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
