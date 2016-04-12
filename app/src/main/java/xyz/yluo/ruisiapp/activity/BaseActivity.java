package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-4-11.
 * 所有activity的基类
 */
public class BaseActivity extends AppCompatActivity{

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.menu_setting) {
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
            return true;
        }
        if (id==R.id.new_topic){
            startActivity(new Intent(getApplicationContext(),NewArticleActivity_2.class));
        }
        return super.onOptionsItemSelected(item);
    }
}