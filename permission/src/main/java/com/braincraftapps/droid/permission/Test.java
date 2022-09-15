package com.braincraftapps.droid.permission;

import android.app.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class Test {
    void doc(Fragment o) {
        ActivityResultLauncher<Float> l = o.registerForActivityResult(null, null, null);
    }
}
