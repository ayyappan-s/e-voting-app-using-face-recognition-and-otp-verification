package com.example.evotingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.goebl.david.Webb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterClass extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> candidateImagelist = new ArrayList<String>();
    private ArrayList<String> candidateSymbollist = new ArrayList<String>();
    Context basecontext;
    Context context;

    TextView candidateName;
    TextView textView;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private String state,district,legislative,phoneNumber;
    ProgressDialog progressDialog;
    int positionname;
    Context parentcontext;
    public AdapterClass(ArrayList<String> list, ArrayList<String> candidateImagelist, ArrayList<String> candidateSymbollist, Context baseContext, Context context, String state, String district, String legislative,String phoneNumber){
        this.list = list;
        this.candidateImagelist=candidateImagelist;
        this.candidateSymbollist=candidateSymbollist;
        this.basecontext = baseContext;
        this.state=state;
        this.district=district;
        this.legislative=legislative;
        this.context=context;
        this.phoneNumber=phoneNumber;
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
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) basecontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item,null);
        }
        parentcontext=parent.getContext();
        candidateName=view.findViewById(R.id.personName);
        textView=unwrap(context).findViewById(R.id.textView6);
        candidateName.setText(list.get(position));
        Button votebtn = view.findViewById(R.id.vote);
        mRequestQueue = Volley.newRequestQueue(basecontext);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
       NetworkImageView candidateSymbol = (NetworkImageView) view.findViewById(R.id.candidateSymbol);
       NetworkImageView candidateimage = (NetworkImageView) view.findViewById(R.id.candidateImage);
        candidateimage.setImageUrl(candidateSymbollist.get(position),mImageLoader);
        candidateimage.setDefaultImageResId(R.drawable.candidate);
        candidateimage.setErrorImageResId(R.drawable.candidate);
        candidateSymbol.setImageUrl(candidateImagelist.get(position),mImageLoader);
        candidateSymbol.setDefaultImageResId(R.drawable.candidate);
        candidateSymbol.setErrorImageResId(R.drawable.candidate);
        votebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent().getParent().getParent();
                ListView listView = (ListView) parentRow.getParent();
                positionname = listView.getPositionForView(parentRow);
                 progressDialog =new ProgressDialog((Activity)context);
                progressDialog.setMessage("Connecting To Server");
                progressDialog.setTitle("Adding Vote");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                Runnable runnable = new Addvoter(v);
                new Thread(runnable).start();

            }
        });

        return view;
    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
public class Addvoter implements Runnable {
        View v;
    public Addvoter(View v) {
        this.v=v;
    }
    @Override
    public void run() {
        Webb webb= Webb.create();
        JSONObject jsonObject =webb.post("http://192.168.1.5:8000/addvote")
                .useCaches(false)
                .param("state",state)
                .param("district",district)
                .param("legislative",legislative)
                .param("candidate_name",list.get(positionname))
                .param("phoneNumber",phoneNumber)
                .ensureSuccess()
                .asJsonObject()
                .getBody();
        progressDialog.setMessage("Connecting To Server");
        progressDialog.setTitle("Adding Vote");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        try {
            if(jsonObject.getString("res").contains("success")){

                progressDialog.dismiss();
                Intent intent = new Intent(basecontext,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                basecontext.startActivity(intent);
               unwrap(context).finish();
            }
            else{
                progressDialog.dismiss();

                textView.setText("Cannot Add Vote! Please Try Again");
            }
        } catch (JSONException e)
        {   progressDialog.dismiss();
            textView.setText("Cannot Add Vote! Please Try Again");
            e.printStackTrace();
        }
    }
}
}
