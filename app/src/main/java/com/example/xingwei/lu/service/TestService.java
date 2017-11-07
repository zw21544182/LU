package com.example.xingwei.lu.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * 创建时间: 2017/11/7
 * 创建人: Administrator
 * 功能描述:
 */

public class TestService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d("zw", "onAccessibilityEvent");
    }

    @Override
    public void onInterrupt() {

    }
}
