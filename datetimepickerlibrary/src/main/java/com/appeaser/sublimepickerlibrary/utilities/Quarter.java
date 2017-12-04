package com.appeaser.sublimepickerlibrary.utilities;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Mirek on 04.12.2017.
 */
@IntDef({Quarter.Q0, Quarter.Q15, Quarter.Q30, Quarter.Q45})
@Retention(RetentionPolicy.SOURCE)
public @interface Quarter {
    int Q0 = 0;
    int Q15 = 15;
    int Q30 = 30;
    int Q45 = 45;
}