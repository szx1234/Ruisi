package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.checknet.CheckNet;
import xyz.yluo.ruisiapp.checknet.CheckNetResponse;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link PublicData}
 */
public class LaunchActivity extends BaseActivity{
    private long starttime = 0;
    private TextView launch_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        launch_text = (TextView) findViewById(R.id.launch_text);
        starttime = System.currentTimeMillis();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSetting();
//      AlphaAnimation anima = new AlphaAnimation(0.2f, 1.0f);
//      anima.setDuration(1000);// 设置动画显示时间
//      image.startAnimation(anima);
        TranslateAnimation animation = new TranslateAnimation(0,0,80,0);
        animation.setDuration(1000);
        launch_text.startAnimation(animation);

        new CheckNet(this).startCheck(new CheckNetResponse() {
            @Override
            public void onFinish(int type, String response) {
                canGetRs(type);
            }
        });
    }

    //从首选项读出设置
    private void getSetting(){
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String urlSetting  = shp.getString("setting_forums_url", "0");
            boolean isShowZhidin  = shp.getBoolean("setting_show_zhidin",false);
            String tail = shp.getString("setting_user_tail","");
            boolean theme = shp.getBoolean("setting_swich_theme",false);
            boolean setting_show_plain = shp.getBoolean("setting_show_plain",false);
            boolean isrecieveMessage = shp.getBoolean("setting_show_notify",false);

            PublicData.ISSHOW_ZHIDIN = isShowZhidin;
            PublicData.ISSHOW_PLAIN = setting_show_plain;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void canGetRs(int type){
        if(type==1||type==2){
            String url = UrlUtils.getLoginUrl(false);
            checklogin(url);
        }else{
            noNetWork();
        }
    }

    private void checklogin(String url){
        HttpUtil.get(this, url, new TextResponseHandler() {
            @Override
            public void onSuccess(String res) {
                PublicData.ISLOGIN = false;
                if (res.contains("loginbox")) {
                    PublicData.ISLOGIN = false;
                } else {
                    Document doc = Jsoup.parse(res);
                    int index =  res.indexOf("欢迎您回来");
                    String s = res.substring(index,index+30).split("，")[1].split(" ")[0].trim();
                    if(s.length()>0){
                        PublicData.USER_GRADE = s;
                    }
                    PublicData.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
                    PublicData.USER_UID = GetId.getUid(url);
                    PublicData.ISLOGIN = true;
                }
            }
            @Override
            public void onFinish() {
                finishthis();
            }
        });


    }
    //没网是执行
    private void noNetWork(){
        Toast.makeText(getApplicationContext(),"无法连接到服务器请检查网络设置！",Toast.LENGTH_SHORT).show();
    }
    private void finishthis(){
        long currenttime = System.currentTimeMillis();
        long delay = 1500-(currenttime-starttime);
        if(delay<0){
            delay = 0;
        }
        new Handler().postDelayed(new Runnable(){
            public void run() {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }
        }, delay);

    }
}
