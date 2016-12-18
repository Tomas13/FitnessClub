package kz.grandprixclub.grandprixclubapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import kz.grandprixclub.grandprixclubapp.Models.Shares;

public class SharesDetailActivity extends AppCompatActivity {

    private Shares shares;
    ImageView imageShare;
    TextView tvAnnounceShare, tvTitleShare;
    WebView wvContentShare;

    private static final String TAG = SharesDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                shares = null;
            } else {
                shares = (Shares) extras.getSerializable("share");
            }
        } else {
            shares = (Shares) savedInstanceState.getSerializable("share");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shares_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        imageShare = (ImageView) findViewById(R.id.image_share);
        tvAnnounceShare = (TextView) findViewById(R.id.tv_announce_share);
        tvTitleShare = (TextView) findViewById(R.id.tv_title_share);
        wvContentShare = (WebView) findViewById(R.id.wv_content_share);

        if (shares != null) {
            tvAnnounceShare.setText(shares.getAnnounce());
            tvTitleShare.setText(shares.getTitle());
            setImage(shares.getMedia(), imageShare);
            wvContentShare.getSettings().setJavaScriptEnabled(true);
            //wvContentShare.loadData(shares.getContent(), "text/html", "UTF-8");
            wvContentShare.loadDataWithBaseURL("", shares.getContent(), "text/html", "UTF-8", "");
        }
    }

    private void setImage(String url, final ImageView image) {
        final ProgressDialog progressDialog = new ProgressDialog(SharesDetailActivity.this);

        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                progressDialog.dismiss();
                image.setImageBitmap(response);

            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d(TAG, error.toString());
            }
        });
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка");
        progressDialog.show();
        Volley.newRequestQueue(SharesDetailActivity.this).add(ir);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
