package com.ut.smartHome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ut.smartHome.plugin.SmartHomeContext;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class RootActivity extends AppCompatActivity {

    private static String KEY_IS_FIRST_START = "isFirstStart";
    private Button bt_run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        boolean isFirstStart = read();

        if (!isFirstStart) {
            Intent intent = new Intent(RootActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        isFirstStart = false;
        save(isFirstStart);

        Banner banner = findViewById(R.id.banner);
        bt_run = findViewById(R.id.run);
        banner.setImageLoader(imageLoader);

        List<Integer> imageSource = new ArrayList<>();
        imageSource.add(R.drawable.guide_1);
        imageSource.add(R.drawable.guide_2);
        imageSource.add(R.drawable.guide_3);
        banner.setImages(imageSource);

        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 2) {

                    bt_run.setVisibility(View.VISIBLE);

                    bt_run.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RootActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    bt_run.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        banner.isAutoPlay(false);
        banner.start();
    }

    ImageLoader imageLoader = new ImageLoader() {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource((Integer) path);
        }
    };

    private boolean read() {
        SharedPreferences sharedPreferences = getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_FIRST_START, true);
    }

    private void save(boolean isFirstStart) {
        SharedPreferences sharedPreferences = getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_FIRST_START, isFirstStart);
        editor.apply();
    }
}
