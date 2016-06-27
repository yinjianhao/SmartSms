package com.me.smartsms.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.me.smartsms.R;
import com.me.smartsms.adapter.MainViewPagerAdapter;
import com.me.smartsms.base.BaseActivity;
import com.me.smartsms.ui.fragment.ConversationFragment;
import com.me.smartsms.ui.fragment.GroupFragment;
import com.me.smartsms.ui.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private MainViewPagerAdapter adapter;
    private TextView tvConversation, tvGrouping, tvSearch;
    private LinearLayout llConversation;
    private LinearLayout llGrouping;
    private LinearLayout llSearch;
    private TextView redLine;
    private int backCount = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.vp);
        tvConversation = (TextView) findViewById(R.id.tv_conversation);
        tvGrouping = (TextView) findViewById(R.id.tv_grouping);
        tvSearch = (TextView) findViewById(R.id.tv_search);

        llConversation = (LinearLayout) findViewById(R.id.ll_conversation);
        llGrouping = (LinearLayout) findViewById(R.id.ll_grouping);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);

        redLine = (TextView) findViewById(R.id.red_line);

        List<Fragment> fragments = new ArrayList<>();
        Fragment conversationFragment = new ConversationFragment();
        Fragment groupFragment = new GroupFragment();
        Fragment searchFragment = new SearchFragment();
        fragments.add(conversationFragment);
        fragments.add(groupFragment);
        fragments.add(searchFragment);

        adapter = new MainViewPagerAdapter(getSupportFragmentManager(), fragments);
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void initListener() {
        llConversation.setOnClickListener(this);
        llGrouping.setOnClickListener(this);
        llSearch.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                redLine.animate().translationX(positionOffsetPixels / 3 + position * redLine.getWidth()).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                setTextLightAndScale(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, state + "");
            }
        });
    }

    @Override
    public void initData() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS
        }, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ((ConversationFragment) adapter.getItem(0)).operationToSmsRead();
            } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ((ConversationFragment) adapter.getItem(0)).operationToContactRead();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.ll_conversation:
                viewPager.setCurrentItem(0);
                break;
            case R.id.ll_grouping:
                viewPager.setCurrentItem(1);
                break;
            case R.id.ll_search:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    private void setTextLightAndScale(int position) {
        tvConversation.setTextColor(position == 0 ? Color.BLUE : Color.WHITE);
        tvGrouping.setTextColor(position == 1 ? Color.BLUE : Color.WHITE);
        tvSearch.setTextColor(position == 2 ? Color.BLUE : Color.WHITE);

        tvConversation.animate().scaleX(position == 0 ? 1.2f : 1).scaleY(position == 0 ? 1.2f : 1).setDuration(200);
        tvGrouping.animate().scaleX(position == 1 ? 1.2f : 1).scaleY(position == 1 ? 1.2f : 1).setDuration(200);
        tvSearch.animate().scaleX(position == 2 ? 1.2f : 1).scaleY(position == 2 ? 1.2f : 1).setDuration(200);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (backCount > 0) {
            finish();
        } else {
            backCount += 1;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    backCount = 0;
                }
            }, 2000);
            Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
        }
    }
}
