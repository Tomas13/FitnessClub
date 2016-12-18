package kz.grandprixclub.grandprixclubapp.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;

import kz.grandprixclub.grandprixclubapp.R;

public class SharesListViewAdapter extends BaseAdapter {

    private static final String TAG = SharesListViewAdapter.class.getSimpleName();

    public static final String ANNONCE = "annonce", IMAGE_URL = "image_url";

    private ArrayList<HashMap<String, String>> list;
    private Activity context;
    TextView annonce;
    ImageView image;
    private RequestQueue mRequestQueue;

    public SharesListViewAdapter(ArrayList<HashMap<String, String>> list, Activity context, RequestQueue requestQueue) {
        this.list = list;
        this.context = context;
        this.mRequestQueue = requestQueue;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shares_table, null);
            image = (ImageView) convertView.findViewById(R.id.imageShares);
            annonce = (TextView) convertView.findViewById(R.id.imageViewText);

            HashMap<String, String> map = list.get(position);

            annonce.setText(map.get(SharesListViewAdapter.ANNONCE));

            setImage(map.get(SharesListViewAdapter.IMAGE_URL), image, map.get(SharesListViewAdapter.ANNONCE));


        }

        return convertView;
    }

    private void setImage(String url, final ImageView image, final String text) {
        final ProgressDialog progressDialog = new ProgressDialog(context);

        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                progressDialog.dismiss();
                //image.setImageBitmap(drawTextToBitmap(context, response, text));
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
        Volley.newRequestQueue(context).add(ir);
        return;
    }

    public Bitmap drawTextToBitmap(Context gContext,
                                   Bitmap bitmap,
                                   String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        /*Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);*/

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (26 * scale));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 26;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

}
