package com.blackbeard.landmarktest;

import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrollingActivity extends AppCompatActivity {

  WebView webView;
  ProgressDialog progressDialog;
  Handler pageLoadHandler = new Handler();
  RecyclerView recyclerView;
  Runnable runnable = new Runnable() {
    @Override public void run() {
      webView.loadUrl(
          "javascript:window.Android.showSrc('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    }
  };
  int count = 0;

  List<ProductItem> productItemsList = new ArrayList<>();
  ProductRecyclerAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scrolling);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    webView = (WebView) findViewById(R.id.webview);
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    adapter = new ProductRecyclerAdapter(this, productItemsList);
    recyclerView.setAdapter(adapter);

    loadWebview("http://www.landmarkshops.com/c/babyandchild-backtoschool");
  }

  private void loadWebview(String path) {

    WebSettings settings = webView.getSettings();
    String appCachePath = getCacheDir().getAbsolutePath();

    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setGeolocationEnabled(true);
    settings.setAllowFileAccess(true);
    settings.setAppCachePath(appCachePath);
    settings.setAppCacheEnabled(true);
    //show progress bar
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Please wait Loading...");
    progressDialog.show();
    webView.setWebViewClient(new MyWebViewClient());

    webView.addJavascriptInterface(new JSInterface(this, null), "Android");
    webView.setWebChromeClient(new WebChromeClient());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
        WebView.setWebContentsDebuggingEnabled(true);
      }
    }
    webView.loadUrl(path);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onMessageEvent(MessageEvent event) {
    handleSrc(event);
    //     handleItems(event);
    //     handlePages(event);
  }

  private void handlePages(MessageEvent event) {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private void handleItems(Element element) {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }

    List<ProductItem> list = ProductItem.parse(element);
    productItemsList.clear();
    productItemsList.addAll(list);
    adapter.notifyDataSetChanged();

    Log.i("ScrollingActivity", "handleItems() list = " + list);
  }

  private void handleSrc(MessageEvent event) {
    boolean isProduct = false;
    if (!TextUtils.isEmpty(event.message)) {
      isProduct = event.message.contains("products-list");
    }
    Log.i("ScrollingActivity", ".onMessageEvent() " + "products-list = " + isProduct);
    if (!isProduct && count < 3) {
      ++count;
      Log.i("ScrollingActivity", ".onMessageEvent() " + "retry ... count=" + count);
      pageLoadHandler.postDelayed(runnable, 500);
    } else {
      if (progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
      count = 0;
      if (!isProduct) {
        Log.i("ScrollingActivity", ".onMessageEvent() " + "refresh again!!!!!");
        Toast.makeText(this, "refresh again", Toast.LENGTH_SHORT).show();
      } else {
        //successful
        Document document = Jsoup.parseBodyFragment(event.message);
        Elements elements = document.getElementsByClass("lms-pagination");
        Log.i("ScrollingActivity", ".onMessageEvent() " + "lms-pagination=" + elements.toString());

        Element element = document.getElementById("products-list");
        Log.i("ScrollingActivity", ".onMessageEvent() " + "products-list=" + element.toString());
        handleItems(element);

        //webView.loadUrl("javascript:window.Android.pageItems('<head>'+document.getElementsByClassName('lms-pagination')[0].getElementsByTagName('ul')[0].getElementsByTagName('li').innerHTML+'</head>');");
        //webView.loadUrl("javascript:window.Android.productItems('<head>'+document.getElementById('products-list').getElementsByClassName('product-item').innerHTML+'</head>');");
      }
    }
  }

  @Override public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  private class MyWebViewClient extends WebViewClient {
    @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

      if (!progressDialog.isShowing()) {
        progressDialog.show();
      }
      return super.shouldOverrideUrlLoading(view, request);
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);

      if (!progressDialog.isShowing()) {
        progressDialog.show();
      }

      return true;
    }

    @Override public void onPageFinished(WebView view, String url) {
      Log.i("ScrollingActivity", "onPageFinished()");
      pageLoadHandler.postDelayed(runnable, 500);
      webView.loadUrl(
          "javascript:window.Android.showSrc('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    }
  }
}
