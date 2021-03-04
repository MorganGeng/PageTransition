package com.gsd.opendemo;

import android.graphics.Bitmap;

/**
 * Created by gsd on 2021/3/2.
 * Copyright © 2021 GSD. All rights reserved.
 */

public class BusEvent {

    private Bitmap data;

    public BusEvent(Bitmap bitmap) {
        data = bitmap;
    }

    public Bitmap getData() {
        return data;
    }
}
