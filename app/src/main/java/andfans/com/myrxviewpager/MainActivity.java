package andfans.com.myrxviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import andfans.com.myrxviewpager.util.MyRxViewPager;

public class MainActivity extends AppCompatActivity {
    private MyRxViewPager myRxViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRxViewPager = (MyRxViewPager) findViewById(R.id.id_viewPager);
    }
}
