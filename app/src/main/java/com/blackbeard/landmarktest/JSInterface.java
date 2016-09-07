package com.blackbeard.landmarktest;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by sudendra.kamble on 07/09/16.
 */

public class JSInterface {
  private Context mContext;
  private ICallback mListener;
  private String content;

  public JSInterface(Context c, ICallback callback) {
    mListener = callback;
    mContext = c;
  }

  @JavascriptInterface public void pageItems(String message) {
    Log.i("JSInterface", ".pageItems() " + "_html = " + message);
  }

  @JavascriptInterface public void productItems(String message) {
    Document document = Jsoup.parseBodyFragment(message);

    Log.i("JSInterface", ".productItems() " + "_html = " + message);
  }

  @JavascriptInterface public void showSrc(String _html) {
    content = _html;
    Log.i("JSInterface", ".showSrc() " + "_html = " + (_html != null));

    EventBus.getDefault().post(new MessageEvent(content));
  }
}
