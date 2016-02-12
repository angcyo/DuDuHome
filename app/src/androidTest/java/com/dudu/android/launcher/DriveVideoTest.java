package com.dudu.android.launcher;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.dudu.aios.ui.activity.MainRecordActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by dengjun on 2016/2/14.
 * Description :
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DriveVideoTest {
   /* @Test
    public void  test()throws Exception{
        final  int i = 1;
        final  int j = 3;
        assertEquals(i, j);
    }*/

    @Rule
    public ActivityTestRule<MainRecordActivity> mActivityRule = new ActivityTestRule<>(
            MainRecordActivity.class);

    @Test
    public void test_click() {
    }
}
