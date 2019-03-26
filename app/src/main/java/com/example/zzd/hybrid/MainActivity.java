package com.example.zzd.hybrid;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    Button mButtom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        mButtom = findViewById(R.id.btn);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.loadUrl("file:///android_asset/hybrid.html");
//         mWebView.loadUrl("http://www.baidu.com");
        mButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
//                        mWebView.loadUrl("javascript:sayHello()");
                        mWebView.loadUrl("javascript:write('hehehehe')");
                    }
                });
            }
        });



        mWebView.setWebChromeClient(new WebChromeClient() {
            // 拦截输入框(原理同方式2)
            // 参数message:代表promt（）的内容（不是url）
            // 参数result:代表输入框的返回值
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {


                Uri uri = Uri.parse(message);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("bridge")) {
                    System.out.println("onJsPrompt:hello");

                    System.out.println("onJsPrompt:hello");
                    //参数result:代表消息框的返回值(输入值)
                    result.confirm("js调用了Android的方法成功啦");


                    return true;
                }

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }


        });

        //  通过addJavascriptInterface()将Java对象映射到JS对象
        //        参数1：Javascript对象名
        //        参数2：Java对象名
        mWebView.addJavascriptInterface(new AndoridJs(), "bridge");

        mWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //此处为 js 返回的结果
            }
        });


    }
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    public class AndoridJs{
        @JavascriptInterface
        public void sayAndroid() {
            System.out.println("hello");

        }

    }

}
