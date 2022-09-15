package com.appstexture.droid.permission;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

public class Test {
    void doc(Fragment o) {
        ActivityResultLauncher<Float> l = o.registerForActivityResult(null, null, null);
    }
}
