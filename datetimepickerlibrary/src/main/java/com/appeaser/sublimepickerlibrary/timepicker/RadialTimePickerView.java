/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright 2015 Vikram Kakkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appeaser.sublimepickerlibrary.timepicker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.appeaser.sublimepickerlibrary.R;
import com.appeaser.sublimepickerlibrary.utilities.LockedInterval;
import com.appeaser.sublimepickerlibrary.utilities.SUtils;
import com.appeaser.sublimepickerlibrary.utilities.TimePickerUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * View to show a clock circle picker (with one or two picking circles)
 */
public class RadialTimePickerView extends View {
    public static final int FULL_ANGLE = 360;
    public static final float FULL_ANGLE_FLOAT = FULL_ANGLE;
    private static final String TAG = RadialTimePickerView.class.getSimpleName();
    private static final int HOURS = 0;
    private static final int MINUTES = 1;
    private static final int HOURS_INNER = 2;
    private static final int SELECTOR_CIRCLE = 0;
    private static final int SELECTOR_DOT = 1;
    private static final int SELECTOR_LINE = 2;
    private static final int AM = 0;
    private static final int PM = 1;
    // Opaque alpha level
    private static final int ALPHA_OPAQUE = 255;
    // Transparent alpha level
    private static final int ALPHA_TRANSPARENT = 0;
    private static final int HOURS_IN_CIRCLE = 12;
    public static final float UNIT_WIDTH = FULL_ANGLE_FLOAT / HOURS_IN_CIRCLE / 4;
    private static final int MINUTES_IN_CIRCLE = 60;
    private static final int DEGREES_FOR_ONE_HOUR = FULL_ANGLE / HOURS_IN_CIRCLE;
    private static final int DEGREES_FOR_ONE_MINUTE = FULL_ANGLE / MINUTES_IN_CIRCLE;
    private static final int[] HOURS_NUMBERS_AM = {12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private static final int FADE_OUT_DURATION = 500;
    private static final int FADE_IN_DURATION = 500;
    private static final int[] SNAP_PREFER_30S_MAP = new int[361];
    private static final int NUM_POSITIONS = 12;
    private static final float[] COS_30 = new float[NUM_POSITIONS];
    private static final float[] SIN_30 = new float[NUM_POSITIONS];
    private static final int ANGLE_NOT_FOUND = -1;
    public static int UNITS_COUNT = (int) (FULL_ANGLE_FLOAT / UNIT_WIDTH);

    static {
        // Prepare mapping to snap touchable degrees to selectable degrees.
        preparePrefer30sMap();

        final double increment = 2.0 * Math.PI / NUM_POSITIONS;
        double angle = Math.PI / 2.0;
        for (int i = 0; i < NUM_POSITIONS; i++) {
            COS_30[i] = (float) Math.cos(angle);
            SIN_30[i] = (float) Math.sin(angle);
            angle += increment;
        }
    }

    private final InvalidateUpdateListener mInvalidateUpdateListener =
            new InvalidateUpdateListener();
    private final String[] hoursTexts = new String[12];
    private final Paint[] mPaint = new Paint[2];
    private final IntHolder[] mAlpha = new IntHolder[2];
    private final Paint mPaintCenter = new Paint();
    private final Paint[][] mPaintSelector = new Paint[2][3];
    private final Paint mPaintBackground = new Paint();
    private final ColorStateList[] mTextColor = new ColorStateList[3];
    private final int[] mTextSize = new int[3];
    private final int[] mTextInset = new int[3];
    private final float[][] mOuterTextX = new float[2][12];
    private final float[][] mOuterTextY = new float[2][12];
    private final float[] mInnerTextX = new float[12];
    private final float[] mInnerTextY = new float[12];
    private final int[] mSelectionDegrees = new int[2];
    private final ArrayList<Animator> mHoursToMinutesAnims = new ArrayList<>();
    private final ArrayList<Animator> mMinuteToHoursAnims = new ArrayList<>();
    private int mSelectorColor;
    private int mSelectorDotColor;
    private Typeface mTypeface;
    private RadialPickerTouchHelper mTouchHelper;
    private boolean mIs24HourMode;
    private boolean mShowHours;
    /**
     * When in 24-hour mode, indicates that the current hour is between
     * 1 and 12 (inclusive).
     */
    private boolean mIsOnInnerCircle;
    private int mSelectorRadius;
    private int mSelectorStroke;
    private int mSelectorDotRadius;
    private int mCenterDotRadius;
    private int mXCenter;
    private int mYCenter;
    private int mCircleRadius;
    private int mMinDistForInnerNumber;
    private int mMaxDistForOuterNumber;
    private int mHalfwayDist;
    private String[] mOuterTextHours;
    private AnimatorSet mTransition;
    private int mAmOrPm;
    private float mDisabledAlpha;
    private OnValueSelectedListener mListener;
    private boolean mInputEnabled = true;
    private boolean mChangedDuringTouch = false;
    private int activeHoursBackgroundColor;
    private int inactiveHoursBackgroundColor;
    private int inactiveDigitsColor;
    private boolean isPm;
    private ArrayList<TimerSection> timerSections;
    private float selCenterX;
    private float selCenterY;
    private HashMap<Integer, Integer> timesToBlock = new HashMap<>();
    private List<LockedInterval> lockedIntervals = new ArrayList<>();
    private boolean lockSelectorDrawing = true;
    private boolean wasSomeCorrectTouch = false;
    private Pair<Integer, Integer> selectedTime;
    private TimerSection selectedSection;
    private LinkedHashSet<Integer> hoursToOvershadow = new LinkedHashSet<>();


    @SuppressWarnings("unused")
    public RadialTimePickerView(Context context) {
        this(context, null);
        setDrawingCacheEnabled(true);
    }

    public RadialTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.spRadialTimePickerStyle);
        setDrawingCacheEnabled(true);
    }

    public RadialTimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDrawingCacheEnabled(true);
        init(attrs, defStyleAttr, R.style.RadialTimePickerViewStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadialTimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
        init(attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Split up the 360 degrees of the circle among the 60 selectable values. Assigns a larger
     * selectable area to each of the 12 visible values, such that the ratio of space apportioned
     * to a visible value : space apportioned to a non-visible value will be 14 : 4.
     * E.g. the output of 30 degrees should have a higher range of input associated with it than
     * the output of 24 degrees, because 30 degrees corresponds to a visible number on the clock
     * circle (5 on the minutes, 1 or 13 on the hours).
     */
    private static void preparePrefer30sMap() {
        // We'll split up the visible output and the non-visible output such that each visible
        // output will correspond to a range of 14 associated input degrees, and each non-visible
        // output will correspond to a range of 4 associate input degrees, so visible numbers
        // are more than 3 times easier to get than non-visible numbers:
        // {354-359,0-7}:0, {8-11}:6, {12-15}:12, {16-19}:18, {20-23}:24, {24-37}:30, etc.
        //
        // If an output of 30 degrees should correspond to a range of 14 associated degrees, then
        // we'll need any input between 24 - 37 to snap to 30. Working out from there, 20-23 should
        // snap to 24, while 38-41 should snap to 36. This is somewhat counter-intuitive, that you
        // can be touching 36 degrees but have the selection snapped to 30 degrees; however, this
        // inconsistency isn't noticeable at such fine-grained degrees, and it affords us the
        // ability to aggressively prefer the visible values by a factor of more than 3:1, which
        // greatly contributes to the selectability of these values.

        // The first output is 0, and each following output will increment by 6 {0, 6, 12, ...}.
        int snappedOutputDegrees = 0;
        // Count of how many inputs we've designated to the specified output.
        int count = 1;
        // How many input we expect for a specified output. This will be 14 for output divisible
        // by 30, and 4 for the remaining output. We'll special case the outputs of 0 and 360, so
        // the caller can decide which they need.
        int expectedCount = 8;
        // Iterate through the input.
        for (int degrees = 0; degrees < 361; degrees++) {
            // Save the input-output mapping.
            SNAP_PREFER_30S_MAP[degrees] = snappedOutputDegrees;
            // If this is the last input for the specified output, calculate the next output and
            // the next expected count.
            if (count == expectedCount) {
                snappedOutputDegrees += 6;
                if (snappedOutputDegrees == FULL_ANGLE) {
                    expectedCount = 7;
                } else if (snappedOutputDegrees % 30 == 0) {
                    expectedCount = 14;
                } else {
                    expectedCount = 4;
                }
                count = 1;
            } else {
                count++;
            }
        }
    }

    /**
     * Returns mapping of any input degrees (0 to 360) to one of 60 selectable output degrees,
     * where the degrees corresponding to visible numbers (i.e. those divisible by 30) will be
     * weighted heavier than the degrees corresponding to non-visible numbers.
     * See {@link #preparePrefer30sMap()} documentation for the rationale and generation of the
     * mapping.
     */
    private static int snapPrefer30s(int degrees) {
        if (SNAP_PREFER_30S_MAP == null) {
            return -1;
        }
        return SNAP_PREFER_30S_MAP[degrees];
    }

    /**
     * Returns mapping of any input degrees (0 to 360) to one of 12 visible output degrees (all
     * multiples of 30), where the input will be "snapped" to the closest visible degrees.
     *
     * @param degrees            The input degrees
     * @param forceHigherOrLower The output may be forced to either the higher or lower step, or may
     *                           be allowed to snap to whichever is closer. Use 1 to force strictly higher, -1 to force
     *                           strictly lower, and 0 to snap to the closer one.
     * @return output degrees, will be a multiple of 30
     */
    private static int snapOnly30s(int degrees, int forceHigherOrLower) {
        final int stepSize = DEGREES_FOR_ONE_HOUR;
        int floor = (degrees / stepSize) * stepSize;
        final int ceiling = floor + stepSize;
        if (forceHigherOrLower == 1) {
            degrees = ceiling;
        } else if (forceHigherOrLower == -1) {
            if (degrees == floor) {
                floor -= stepSize;
            }
            degrees = floor;
        } else {
            if ((degrees - floor) < (ceiling - degrees)) {
                degrees = floor;
            } else {
                degrees = ceiling;
            }
        }
        return degrees;
    }

    private static int snapOnly(int degrees, final int stepSize, int forceHigherOrLower) {
        int floor = (degrees / stepSize) * stepSize;
        final int ceiling = floor + stepSize;
        if (forceHigherOrLower == 1) {
            degrees = ceiling;
        } else if (forceHigherOrLower == -1) {
            if (degrees == floor) {
                floor -= stepSize;
            }
            degrees = floor;
        } else {
            if ((degrees - floor) < (ceiling - degrees)) {
                degrees = floor;
            } else {
                degrees = ceiling;
            }
        }
        return degrees;
    }

    /**
     * Using the trigonometric Unit Circle, calculate the positions that the text will need to be
     * drawn at based on the specified circle radius. Place the values in the textGridHeights and
     * textGridWidths parameters.
     */
    private static void calculatePositions(Paint paint, float radius, float xCenter, float yCenter,
                                           float textSize, float[] x, float[] y) {
        // Adjust yCenter to account for the text's baseline.
        paint.setTextSize(textSize);
        yCenter -= (paint.descent() + paint.ascent()) / 2;

        for (int i = 0; i < NUM_POSITIONS; i++) {
            x[i] = xCenter - radius * COS_30[i];
            y[i] = yCenter - radius * SIN_30[i];
        }
    }

    private static ObjectAnimator getFadeOutAnimator(IntHolder target, int startAlpha, int endAlpha,
                                                     InvalidateUpdateListener updateListener) {
        final ObjectAnimator animator = ObjectAnimator.ofInt(target, "value", startAlpha, endAlpha);
        animator.setDuration(FADE_OUT_DURATION);
        animator.addUpdateListener(updateListener);
        return animator;
    }

    private static ObjectAnimator getFadeInAnimator(IntHolder target, int startAlpha, int endAlpha,
                                                    InvalidateUpdateListener updateListener) {
        final float delayMultiplier = 0.25f;
        final float transitionDurationMultiplier = 1f;
        final float totalDurationMultiplier = transitionDurationMultiplier + delayMultiplier;
        final int totalDuration = (int) (FADE_IN_DURATION * totalDurationMultiplier);
        final float delayPoint = (delayMultiplier * FADE_IN_DURATION) / totalDuration;

        final Keyframe kf0, kf1, kf2;
        kf0 = Keyframe.ofInt(0f, startAlpha);
        kf1 = Keyframe.ofInt(delayPoint, startAlpha);
        kf2 = Keyframe.ofInt(1f, endAlpha);
        final PropertyValuesHolder fadeIn = PropertyValuesHolder.ofKeyframe("value", kf0, kf1, kf2);

        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, fadeIn);
        animator.setDuration(totalDuration);
        animator.addUpdateListener(updateListener);
        return animator;
    }

    public List<LockedInterval> getLockedIntervals() {
        return lockedIntervals;
    }

    public void setLockedIntervals(List<LockedInterval> lockedIntervals) {
        this.lockedIntervals = lockedIntervals;
        if (hoursToOvershadow != null) {
            hoursToOvershadow.clear();
        }
        if (timesToBlock != null) {
            timesToBlock.clear();
        }
        invalidate();
        mTouchHelper.invalidateRoot();
    }

    public void toggleAmPm() {
        isPm = !isPm;
        initHoursAndMinutesText();
        mOuterTextHours = hoursTexts;
        if (wasSomeCorrectTouch) {
            lockSelectorDrawing = !lockSelectorDrawing;
        }
        invalidate();
        mTouchHelper.invalidateRoot();
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Context context = getContext();

        // Pull disabled alpha from theme.
        final TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.disabledAlpha, outValue, true);
        mDisabledAlpha = outValue.getFloat();

        // process style attributes
        final Resources res = getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadialTimePickerView,
                defStyleAttr, defStyleRes);

        mTypeface = Typeface.create("sans-serif", Typeface.NORMAL);

        // Initialize all alpha values to opaque.
        for (int i = 0; i < mAlpha.length; i++) {
            mAlpha[i] = new IntHolder(ALPHA_OPAQUE);
        }

        activeHoursBackgroundColor = a.getColor(R.styleable.RadialTimePickerView_activeHoursBackgroundColor, ContextCompat.getColor(context, R.color.timer_background));
        inactiveHoursBackgroundColor = a.getColor(R.styleable.RadialTimePickerView_inactiveHoursBackgroundColor, ContextCompat.getColor(context, R.color.timer_background_blocked));
        inactiveDigitsColor = a.getColor(R.styleable.RadialTimePickerView_inactiveDigitsColor, ContextCompat.getColor(context, R.color.inactive_digits_color));

        mTextColor[HOURS] = a.getColorStateList(R.styleable.RadialTimePickerView_spNumbersTextColor);
        mTextColor[MINUTES] = mTextColor[HOURS];

        mPaint[HOURS] = new Paint();
        mPaint[HOURS].setAntiAlias(true);
        mPaint[HOURS].setTextAlign(Paint.Align.CENTER);

        mPaint[MINUTES] = new Paint();
        mPaint[MINUTES].setAntiAlias(true);
        mPaint[MINUTES].setTextAlign(Paint.Align.CENTER);

        final ColorStateList selectorColors = a.getColorStateList(
                R.styleable.RadialTimePickerView_spNumbersSelectorColor);

        int selectorActivatedColor = Color.BLACK;
        if (selectorColors != null) {
            selectorActivatedColor = selectorColors.getColorForState(
                    SUtils.resolveStateSet(SUtils.STATE_ENABLED | SUtils.STATE_ACTIVATED), 0);
        }

        mPaintCenter.setColor(selectorActivatedColor);
        mPaintCenter.setAntiAlias(true);

        final int[] activatedStateSet = SUtils.resolveStateSet(SUtils.STATE_ENABLED | SUtils.STATE_ACTIVATED);

        mSelectorColor = selectorActivatedColor;
        mSelectorDotColor = mTextColor[HOURS].getColorForState(activatedStateSet, 0);

        mPaintSelector[HOURS][SELECTOR_CIRCLE] = new Paint();
        mPaintSelector[HOURS][SELECTOR_CIRCLE].setAntiAlias(true);

        mPaintSelector[HOURS][SELECTOR_DOT] = new Paint();
        mPaintSelector[HOURS][SELECTOR_DOT].setAntiAlias(true);

        mPaintSelector[HOURS][SELECTOR_LINE] = new Paint();
        mPaintSelector[HOURS][SELECTOR_LINE].setAntiAlias(true);
        mPaintSelector[HOURS][SELECTOR_LINE].setStrokeWidth(2);

        mPaintSelector[MINUTES][SELECTOR_CIRCLE] = new Paint();
        mPaintSelector[MINUTES][SELECTOR_CIRCLE].setAntiAlias(true);

        mPaintSelector[MINUTES][SELECTOR_DOT] = new Paint();
        mPaintSelector[MINUTES][SELECTOR_DOT].setAntiAlias(true);

        mPaintSelector[MINUTES][SELECTOR_LINE] = new Paint();
        mPaintSelector[MINUTES][SELECTOR_LINE].setAntiAlias(true);
        mPaintSelector[MINUTES][SELECTOR_LINE].setStrokeWidth(2);

        mPaintBackground.setColor(a.getColor(R.styleable.RadialTimePickerView_spNumbersBackgroundColor,
                ContextCompat.getColor(context, R.color.timepicker_default_numbers_background_color_material)));
        mPaintBackground.setAntiAlias(true);

        mSelectorRadius = res.getDimensionPixelSize(R.dimen.sp_timepicker_selector_radius);
        mSelectorStroke = res.getDimensionPixelSize(R.dimen.sp_timepicker_selector_stroke);
        mSelectorDotRadius = res.getDimensionPixelSize(R.dimen.sp_timepicker_selector_dot_radius);
        mCenterDotRadius = res.getDimensionPixelSize(R.dimen.sp_timepicker_center_dot_radius);

        mTextSize[HOURS] = res.getDimensionPixelSize(R.dimen.sp_timepicker_text_size_normal);
        mTextSize[MINUTES] = res.getDimensionPixelSize(R.dimen.sp_timepicker_text_size_normal);
        mTextSize[HOURS_INNER] = res.getDimensionPixelSize(R.dimen.sp_timepicker_text_size_inner);

        mTextInset[HOURS] = res.getDimensionPixelSize(R.dimen.sp_timepicker_text_inset_normal);
        mTextInset[MINUTES] = res.getDimensionPixelSize(R.dimen.sp_timepicker_text_inset_normal);
        mTextInset[HOURS_INNER] = res.getDimensionPixelSize(R.dimen.sp_timepicker_text_inset_inner);

        mShowHours = true;
        mIs24HourMode = false;
        mAmOrPm = AM;

        // Set up accessibility components.
        mTouchHelper = new RadialPickerTouchHelper();
        ViewCompat.setAccessibilityDelegate(this, mTouchHelper);

        if (ViewCompat.getImportantForAccessibility(this) == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }

        initHoursAndMinutesText();
        mOuterTextHours = hoursTexts;

        a.recycle();

        // Initial values
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        setHapticFeedbackEnabled(true);
    }

    public void initialize(int hour, int minute, boolean is24HourMode) {
        if (mIs24HourMode != is24HourMode) {
            mIs24HourMode = is24HourMode;
            mOuterTextHours = hoursTexts;
        }

    }

    public void setCurrentItemShowing(int item, boolean animate) {
        switch (item) {
            case HOURS:
                showHours(animate);
                break;
            case MINUTES:
                showMinutes(animate);
                break;
            default:
                Log.e(TAG, "ClockView does not support showing item " + item);
        }
    }

    public int getCurrentItemShowing() {
        return mShowHours ? HOURS : MINUTES;
    }

    public void setOnValueSelectedListener(OnValueSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Returns the current hour in 24-hour time.
     *
     * @return the current hour between 0 and 23 (inclusive)
     */
    public int getCurrentHour() {
        return getHourForDegrees(mSelectionDegrees[HOURS], mIsOnInnerCircle);
    }


    private int getHourForDegrees(int degrees, boolean innerCircle) {
        int hour = (degrees / DEGREES_FOR_ONE_HOUR) % 12;
        if (mIs24HourMode) {
            // Convert the 12-hour value into 24-hour time based on where the
            // selector is positioned.
            if (!innerCircle && hour == 0) {
                // Outer circle is 1 through 12.
                hour = 12;
            } else if (innerCircle && hour != 0) {
                // Inner circle is 13 through 23 and 0.
                hour += 12;
            }
        } else if (mAmOrPm == PM) {
            hour += 12;
        }
        return hour;
    }

    /**
     * @param hour the hour in 24-hour time or 12-hour time
     */
    private int getDegreesForHour(int hour) {
        // Convert to be 0-11.
        if (mIs24HourMode) {
            if (hour >= 12) {
                hour -= 12;
            }
        } else if (hour == 12) {
            hour = 0;
        }
        return hour * DEGREES_FOR_ONE_HOUR;
    }

    /**
     * @param hour the hour in 24-hour time or 12-hour time
     */
    private boolean getInnerCircleForHour(int hour) {
        return mIs24HourMode && (hour == 0 || hour > 12);
    }

    // Returns minutes in 0-59 range
    public int getCurrentMinute() {
        return getMinuteForDegrees(mSelectionDegrees[MINUTES]);
    }

    private int getMinuteForDegrees(int degrees) {
        return degrees / DEGREES_FOR_ONE_MINUTE;
    }

    private int getDegreesForMinute(int minute) {
        return minute * DEGREES_FOR_ONE_MINUTE;
    }

    public int getAmOrPm() {
        return mAmOrPm;
    }

    public void setAmOrPm(int val) {
        mAmOrPm = (val % 2);
        invalidate();
        mTouchHelper.invalidateRoot();
    }

    private void showHours(boolean animate) {
        if (mShowHours) {
            return;
        }

        mShowHours = true;

        if (animate) {
            startMinutesToHoursAnimation();
        }

        mOuterTextHours = hoursTexts;
        invalidate();
        mTouchHelper.invalidateRoot();
    }

    private void showMinutes(boolean animate) {
        if (!mShowHours) {
            return;
        }

        mShowHours = false;

        if (animate) {
            startHoursToMinutesAnimation();
        }

        mOuterTextHours = hoursTexts;
        invalidate();
        mTouchHelper.invalidateRoot();
    }

    private void initHoursAndMinutesText() {
        // Initialize the hours and minutes numbers.
        for (int i = 0; i < 12; i++) {
            if (isPm) {
                hoursTexts[i] = String.format("%d", HOURS_NUMBERS_AM[i] + 12);
            } else {
                hoursTexts[i] = String.format("%d", HOURS_NUMBERS_AM[i]);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
        mCircleRadius = Math.min(mXCenter, mYCenter);

        mMinDistForInnerNumber = mCircleRadius - mTextInset[HOURS_INNER] - mSelectorRadius;
        mMaxDistForOuterNumber = mCircleRadius - mTextInset[HOURS] + mSelectorRadius;
        mHalfwayDist = mCircleRadius - (mTextInset[HOURS] + mTextInset[HOURS_INNER]) / 2;

        calculatePositionsHours();
        calculatePositionsMinutes();

        mTouchHelper.invalidateRoot();
    }

    @Override
    public void onDraw(Canvas canvas) {
        final float alphaMod = mInputEnabled ? 1 : mDisabledAlpha;
        Log.i("hourTest:", "onDraw");

        ArrayList<Float> startArcAngles = TimePickerUtils.generateTimerStartArcAngles();
        timerSections = TimePickerUtils.generateTimerSections(startArcAngles, isPm);

        Paint paint = new Paint();
        paint.setColor(activeHoursBackgroundColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        RectF rectF = new RectF(mXCenter - mCircleRadius, mYCenter - mCircleRadius,
                mXCenter + mCircleRadius, mYCenter + mCircleRadius);
        canvas.drawOval(rectF, paint);

        for (LockedInterval lockedInterval : lockedIntervals) {
            addLockedInterval(canvas, paint, rectF, lockedInterval);
        }

        drawHours(canvas, alphaMod);
    }

    private void addLockedInterval(Canvas canvas, Paint paint, RectF rectF, LockedInterval lockedInterval) {
        timesToBlock.put(TimePickerUtils.getTimeAsMinutes(lockedInterval.getStartHour(), lockedInterval.getStartMinute()),
                TimePickerUtils.getTimeAsMinutes(lockedInterval.getEndHour(), lockedInterval.getEndMinute()));
        hoursToOvershadow.addAll(TimePickerUtils.extractHoursToOvershadow(lockedInterval));
        drawBlockedHours(canvas, paint, rectF, lockedInterval);
    }

    private void drawBlockedHours(Canvas canvas, Paint paint, RectF rectF,
                                  LockedInterval lockedInterval) {
        TimerSection startSection = TimePickerUtils.findSectionForHour(lockedInterval.getStartHour(), timerSections);
        float startAngle = TimePickerUtils.findAngleForGivenMinutesAndHours(lockedInterval.getStartMinute(), startSection);
        if (startAngle == ANGLE_NOT_FOUND) {
            startAngle = 0;
        }
        TimerSection endSection = TimePickerUtils.findSectionForHour(lockedInterval.getEndHour(), timerSections);
        float endAngle = TimePickerUtils.findAngleForGivenMinutesAndHours(lockedInterval.getEndMinute(), endSection);
        if (endAngle == ANGLE_NOT_FOUND) {
            endAngle = 0;
        }

        Pair<Float, Float> sweepAngles = TimePickerUtils.findSweepAngles(startAngle, endAngle, lockedInterval);

        paint.setColor(inactiveHoursBackgroundColor);

        float drawArcAngle = TimePickerUtils.mapStartAngleToDrawArcAngle(startAngle);
        Pair<Float, Float> initialSweepAngles = TimePickerUtils.getInitialSweepAngles(isPm, sweepAngles, drawArcAngle);
        Pair<Float, Float> correctedAngles = TimePickerUtils.correctedAngle(drawArcAngle,
                initialSweepAngles, lockedInterval, startAngle, endAngle, isPm);

        if (canDrawOncurrentScreen(lockedInterval)) {
            canvas.drawArc(rectF, correctedAngles.first, correctedAngles.second, true, paint);
        }
    }

    private boolean canDrawOncurrentScreen(LockedInterval lockedInterval) {
        boolean isStartTimePm = TimePickerUtils.isTimePm(lockedInterval.getStartHour(), lockedInterval.getStartMinute());
        boolean isEndTimePm = TimePickerUtils.isTimePm(lockedInterval.getEndHour(), lockedInterval.getEndMinute());
        return isPmAndCanDrawPm(isStartTimePm, isEndTimePm) || isAmAndCanDrawAm(isStartTimePm, isEndTimePm);
    }

    private boolean isAmAndCanDrawAm(boolean isStartTimePm, boolean isEndTimePm) {
        return !isPm && (!isEndTimePm || !isStartTimePm);
    }

    private boolean isPmAndCanDrawPm(boolean isStartTimePm, boolean isEndTimePm) {
        return isPm && (isEndTimePm || isStartTimePm);
    }

    private void drawHours(Canvas canvas, float alphaMod) {
        final int hoursAlpha = (int) (mAlpha[HOURS].getValue() * alphaMod + 0.5f);
        if (hoursAlpha > 0) {
            // Draw the hour selector under the elements.
            if (!lockSelectorDrawing) {
                drawSelector(canvas, mIsOnInnerCircle ? HOURS_INNER : HOURS, null, alphaMod);
            }

            // Draw outer hours.
            drawTextElements(canvas, mTextSize[HOURS], mTypeface, mTextColor[HOURS],
                    mOuterTextHours, mOuterTextX[HOURS], mOuterTextY[HOURS], mPaint[HOURS],
                    hoursAlpha, !mIsOnInnerCircle, mSelectionDegrees[HOURS], false);
        }
    }

    private int applyAlpha(int argb, int alpha) {
        final int srcAlpha = (argb >> 24) & 0xFF;
        final int dstAlpha = (int) (srcAlpha * (alpha / 255.0) + 0.5f);
        return (0xFFFFFF & argb) | (dstAlpha << 24);
    }

    private int getMultipliedAlpha(int argb, int alpha) {
        return (int) (Color.alpha(argb) * (alpha / 255.0) + 0.5);
    }

    private void drawSelector(Canvas canvas, int index, Path selectorPath, float alphaMod) {
        final int alpha = (int) (mAlpha[index % 2].getValue() * alphaMod + 0.5f);
        final int color = applyAlpha(mSelectorColor, alpha);

        // Calculate the current radius at which to place the selection circle.
        final int selRadius = mSelectorRadius;
        final int selLength = mCircleRadius - mTextInset[index];
        final double selAngleRad = Math.toRadians(mSelectionDegrees[index % 2]);
        selCenterX = mXCenter + selLength * (float) Math.sin(selAngleRad);
        selCenterY = mYCenter - selLength * (float) Math.cos(selAngleRad);

        // Draw the selection circle.
        final Paint paint = mPaintSelector[index % 2][SELECTOR_CIRCLE];
        paint.setColor(color);
        canvas.drawCircle(selCenterX, selCenterY, selRadius, paint);

        // If needed, set up the clip path for later.
        if (selectorPath != null) {
            selectorPath.reset();
            selectorPath.addCircle(selCenterX, selCenterY, selRadius, Path.Direction.CCW);
        }

        // Draw the dot if we're between two items.
        final boolean shouldDrawDot = mSelectionDegrees[index % 2] % 30 != 0;
        if (shouldDrawDot) {
            final Paint dotPaint = mPaintSelector[index % 2][SELECTOR_DOT];
            dotPaint.setColor(mSelectorDotColor);
            canvas.drawCircle(selCenterX, selCenterY, mSelectorDotRadius, dotPaint);
        }

        // Shorten the line to only go from the edge of the center dot to the
        // edge of the selection circle.
        final double sin = Math.sin(selAngleRad);
        final double cos = Math.cos(selAngleRad);
        final int lineLength = selLength - selRadius;
        final int centerX = mXCenter + (int) (mCenterDotRadius * sin);
        final int centerY = mYCenter - (int) (mCenterDotRadius * cos);
        final float linePointX = centerX + (int) (lineLength * sin);
        final float linePointY = centerY - (int) (lineLength * cos);

        // Draw the line.
        final Paint linePaint = mPaintSelector[index % 2][SELECTOR_LINE];
        linePaint.setColor(color);
        linePaint.setStrokeWidth(mSelectorStroke);
        canvas.drawLine(mXCenter, mYCenter, linePointX, linePointY, linePaint);
    }

    private void calculatePositionsHours() {
        // Calculate the text positions
        final float numbersRadius = mCircleRadius - mTextInset[HOURS];

        // Calculate the positions for the 12 numbers in the main circle.
        calculatePositions(mPaint[HOURS], numbersRadius, mXCenter, mYCenter,
                mTextSize[HOURS], mOuterTextX[HOURS], mOuterTextY[HOURS]);

        // If we have an inner circle, calculate those positions too.
        if (mIs24HourMode) {
            final int innerNumbersRadius = mCircleRadius - mTextInset[HOURS_INNER];
            calculatePositions(mPaint[HOURS], innerNumbersRadius, mXCenter, mYCenter,
                    mTextSize[HOURS_INNER], mInnerTextX, mInnerTextY);
        }
    }

    private void calculatePositionsMinutes() {
        // Calculate the text positions
        final float numbersRadius = mCircleRadius - mTextInset[MINUTES];

        // Calculate the positions for the 12 numbers in the main circle.
        calculatePositions(mPaint[MINUTES], numbersRadius, mXCenter, mYCenter,
                mTextSize[MINUTES], mOuterTextX[MINUTES], mOuterTextY[MINUTES]);
    }

    /**
     * Draw the 12 text values at the positions specified by the textGrid parameters.
     */
    private void drawTextElements(Canvas canvas, float textSize, Typeface typeface,
                                  ColorStateList textColor, String[] texts, float[] textX, float[] textY, Paint paint,
                                  int alpha, boolean showActivated, int activatedDegrees, boolean activatedOnly) {
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        // The activated index can touch a range of elements.
        final float activatedIndex = activatedDegrees / (360.0f / NUM_POSITIONS);
        final int activatedFloor = (int) activatedIndex;
        final int activatedCeil = ((int) Math.ceil(activatedIndex)) % NUM_POSITIONS;

        for (int i = 0; i < 12; i++) {
            final boolean activated = (activatedFloor == i || activatedCeil == i);
            if (activatedOnly && !activated) {
                continue;
            }

            final int stateMask = SUtils.STATE_ENABLED
                    | (showActivated && activated ? SUtils.STATE_ACTIVATED : 0);
            final int color = textColor.getColorForState(SUtils.resolveStateSet(stateMask), 0);
            paint.setColor(Color.BLACK);
            if (!lockSelectorDrawing && selectedTime.second != 30 && isSelectedSectionDrawing(i)) {
                paint.setColor(Color.WHITE);
            }
            int drawingHour = Integer.parseInt(texts[i]);
            if (hoursToOvershadow.contains(drawingHour)) {
                paint.setColor(inactiveDigitsColor);
            }
            paint.setAlpha(getMultipliedAlpha(color, alpha));

            canvas.drawText(texts[i], textX[i], textY[i], paint);
        }
    }

    private boolean isSelectedSectionDrawing(int index) {
        if (isPm) {
            return selectedSection.getHour() == index + HOURS_IN_CIRCLE;
        } else {
            return selectedSection.getHour() == index;
        }
    }

    private void startHoursToMinutesAnimation() {
        if (mHoursToMinutesAnims.size() == 0) {
            mHoursToMinutesAnims.add(getFadeOutAnimator(mAlpha[HOURS],
                    ALPHA_OPAQUE, ALPHA_TRANSPARENT, mInvalidateUpdateListener));
            mHoursToMinutesAnims.add(getFadeInAnimator(mAlpha[MINUTES],
                    ALPHA_TRANSPARENT, ALPHA_OPAQUE, mInvalidateUpdateListener));
        }

        if (mTransition != null && mTransition.isRunning()) {
            mTransition.end();
        }
        mTransition = new AnimatorSet();
        mTransition.playTogether(mHoursToMinutesAnims);
        mTransition.start();
    }

    private void startMinutesToHoursAnimation() {
        if (mMinuteToHoursAnims.size() == 0) {
            mMinuteToHoursAnims.add(getFadeOutAnimator(mAlpha[MINUTES],
                    ALPHA_OPAQUE, ALPHA_TRANSPARENT, mInvalidateUpdateListener));
            mMinuteToHoursAnims.add(getFadeInAnimator(mAlpha[HOURS],
                    ALPHA_TRANSPARENT, ALPHA_OPAQUE, mInvalidateUpdateListener));
        }

        if (mTransition != null && mTransition.isRunning()) {
            mTransition.end();
        }
        mTransition = new AnimatorSet();
        mTransition.playTogether(mMinuteToHoursAnims);
        mTransition.start();
    }

    private int getDegreesFromXY(float x, float y, boolean constrainOutside) {
        // Ensure the point is inside the touchable area.
        final int innerBound;
        final int outerBound;
        if (mIs24HourMode && mShowHours) {
            innerBound = mMinDistForInnerNumber;
            outerBound = mMaxDistForOuterNumber;
        } else {
            final int index = mShowHours ? HOURS : MINUTES;
            final int center = mCircleRadius - mTextInset[index];
            innerBound = center - mSelectorRadius;
            outerBound = center + mSelectorRadius;
        }

        final double dX = x - mXCenter;
        final double dY = y - mYCenter;
        final double distFromCenter = Math.sqrt(dX * dX + dY * dY);
        if (distFromCenter < innerBound || constrainOutside && distFromCenter > outerBound) {
            return -1;
        }

        // Convert to degrees.
        final int degrees = (int) (Math.toDegrees(Math.atan2(dY, dX) + Math.PI / 2) + 0.5);
        if (degrees < 0) {
            return degrees + FULL_ANGLE;
        } else {
            return degrees;
        }
    }

    private boolean getInnerCircleFromXY(float x, float y) {
        if (mIs24HourMode && mShowHours) {
            final double dX = x - mXCenter;
            final double dY = y - mYCenter;
            final double distFromCenter = Math.sqrt(dX * dX + dY * dY);
            return distFromCenter <= mHalfwayDist;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInputEnabled) {
            return true;
        }

        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_MOVE
                || action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_DOWN) {
            boolean forceSelection = false;
            boolean autoAdvance = false;

            if (action == MotionEvent.ACTION_DOWN) {
                // This is a new event stream, reset whether the value changed.
                mChangedDuringTouch = false;
            } else if (action == MotionEvent.ACTION_UP) {
                autoAdvance = true;

                // If we saw a down/up pair without the value changing, assume
                // this is a single-tap selection and force a change.
                if (!mChangedDuringTouch) {
                    forceSelection = true;
                }
            }

            mChangedDuringTouch |= handleTouchInput(
                    event.getX(), event.getY(), forceSelection, autoAdvance);
        }

        return true;
    }

    private boolean handleTouchInput(
            float x, float y, boolean forceSelection, boolean autoAdvance) {
        final boolean isOnInnerCircle = getInnerCircleFromXY(x, y);
        final int degrees = getDegreesFromXY(x, y, false);
        if (degrees == -1) {
            return false;
        }

        int type = HOURS;
        boolean valueChanged = false;

        if (mShowHours) {
            TimerSection newSelectedSection = TimePickerUtils.findSectionForDegrees(timerSections, degrees);
            int snapDegrees = 0;
            if (newSelectedSection != null) {
                float startAngle = TimePickerUtils.findStartAngleOfSectionWhichContainsDegree(degrees, newSelectedSection);
                float endAngle = startAngle + UNIT_WIDTH;
                boolean isDegreesCloserToStartDegree =
                        TimePickerUtils.isDegreeCloserToStartDegree(degrees, startAngle, endAngle);
                if (isDegreesCloserToStartDegree) {
                    snapDegrees = (int) startAngle;
                } else {
                    snapDegrees = (int) endAngle;
                }

                Log.i("timerTest", "snapDegrees:" + snapDegrees);
                int hour = newSelectedSection.getHour();
                int unassignedQuarter = TimePickerUtils.findUnasignedQuarterOfSectionWhichContainsDegree(degrees,
                        newSelectedSection);
                Pair<Integer, Integer> newTime = TimePickerUtils.mapToTimeAsPair(hour,
                        unassignedQuarter,
                        isDegreesCloserToStartDegree, isPm);

                Log.i("timerTest", "new Time hour:" + newTime.first + " minute:" + newTime.second);
                Log.i("hourTest:", "-----------------------------");
                boolean isInJoinedBlockedAreas = isInJoinedBlockedAreas(newTime);
                if (isInJoinedBlockedAreas) {
                    return false;
                }
                wasSomeCorrectTouch = true;
                selectedSection = newSelectedSection;
                selectedTime = newTime;
                valueChanged = setParamsOnAllowedTimeSelected(isOnInnerCircle, snapDegrees);

                Log.i("hourTest:", "inJoinedAreas:" + isInJoinedBlockedAreas);

                if (notifyNewSelectedTimeAndInvalidate(forceSelection, autoAdvance, valueChanged,
                        selectedTime))
                    return true;

            }
        }

        return false;
    }

    private boolean setParamsOnAllowedTimeSelected(boolean isOnInnerCircle, int snapDegrees) {
        boolean valueChanged;
        valueChanged = mIsOnInnerCircle != isOnInnerCircle
                || mSelectionDegrees[HOURS] != snapDegrees;
        mIsOnInnerCircle = isOnInnerCircle;
        mSelectionDegrees[HOURS] = snapDegrees;
        lockSelectorDrawing = false;
        return valueChanged;
    }

    private boolean notifyNewSelectedTimeAndInvalidate(boolean forceSelection, boolean autoAdvance, boolean valueChanged,
                                                       Pair<Integer, Integer> selectedTime) {
        if (valueChanged || forceSelection || autoAdvance) {
            // Fire the listener even if we just need to auto-advance.
            if (mListener != null) {
                mListener.onValueSelected(selectedTime);
            }

            // Only provide feedback if the value actually changed.
            if (valueChanged || forceSelection) {
                SUtils.vibrateForTimePicker(this);
                invalidate();
            }
            return true;
        }
        return false;
    }

    private boolean isInJoinedBlockedAreas(Pair<Integer, Integer> selectedTime) {
        boolean isInJoinedAreas = false;
        for (Map.Entry<Integer, Integer> entry : timesToBlock.entrySet()) {
            Integer startTimeInMin = entry.getKey();
            Integer endTimeInMin = entry.getValue();
            Pair<Integer, Integer> startHourAndMin = TimePickerUtils.timeInMinutesAsHourAndMin(startTimeInMin);
            Pair<Integer, Integer> endHourAndMin = TimePickerUtils.timeInMinutesAsHourAndMin(endTimeInMin);
            Log.i("hourTest:", "startHourAndMin:" + startHourAndMin);
            Log.i("hourTest:", "endHourAndMin:" + endHourAndMin);
            Log.i("hourTest:", "selectedHourAndMin:" + selectedTime);

            isInJoinedAreas |= TimePickerUtils.isSelectedInBlockedArea(selectedTime,
                    startHourAndMin, endHourAndMin);
            Log.i("hourTest:", TimePickerUtils.isSelectedInBlockedArea(selectedTime,
                    startHourAndMin, endHourAndMin) + "");
        }
        return isInJoinedAreas;
    }

    @Override
    public boolean dispatchHoverEvent(MotionEvent event) {
        // First right-of-refusal goes the touch exploration helper.
        return mTouchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    public void setInputEnabled(boolean inputEnabled) {
        mInputEnabled = inputEnabled;
        invalidate();
    }

    public void reset() {
        lockSelectorDrawing = true;
        wasSomeCorrectTouch = false;
        setLockedIntervals(new ArrayList<LockedInterval>());
        invalidate();
        mTouchHelper.invalidateRoot();
    }

    public interface OnValueSelectedListener {
        void onValueSelected(Pair<Integer, Integer> selectedTime);
    }

    private static class IntHolder {
        private int mValue;

        public IntHolder(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public void setValue(int value) {
            mValue = value;
        }
    }

    public static class TimerSection {
        private int hour;
        private ArrayList<Float> sectionStartAngles;

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public ArrayList<Float> getSectionStartAngles() {
            return sectionStartAngles;
        }

        public void setSectionStartAngles(ArrayList<Float> sectionStartAngles) {
            this.sectionStartAngles = sectionStartAngles;
        }
    }

    private class InvalidateUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            RadialTimePickerView.this.invalidate();
        }
    }

    private class RadialPickerTouchHelper extends ExploreByTouchHelper {
        private final Rect mTempRect = new Rect();

        private final int TYPE_HOUR = 1;
        private final int TYPE_MINUTE = 2;

        private final int SHIFT_TYPE = 0;
        private final int MASK_TYPE = 0xF;

        private final int SHIFT_VALUE = 8;
        private final int MASK_VALUE = 0xFF;

        /**
         * Increment in which virtual views are exposed for minutes.
         */
        private final int MINUTE_INCREMENT = 5;

        public RadialPickerTouchHelper() {
            super(RadialTimePickerView.this);
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            if (mShowHours) {
                final int min = mIs24HourMode ? 0 : 1;
                final int max = mIs24HourMode ? 23 : 12;
                for (int i = min; i <= max; i++) {
                    virtualViewIds.add(makeId(TYPE_HOUR, i));
                }
            } else {
                final int current = getCurrentMinute();
                for (int i = 0; i < MINUTES_IN_CIRCLE; i += MINUTE_INCREMENT) {
                    virtualViewIds.add(makeId(TYPE_MINUTE, i));

                    // If the current minute falls between two increments,
                    // insert an extra node for it.
                    if (current > i && current < i + MINUTE_INCREMENT) {
                        virtualViewIds.add(makeId(TYPE_MINUTE, current));
                    }
                }
            }
        }

        @Override
        protected void onPopulateEventForVirtualView(int virtualViewId, AccessibilityEvent event) {
            event.setClassName(getClass().getName());

            final int type = getTypeFromId(virtualViewId);
            final int value = getValueFromId(virtualViewId);
            final CharSequence description = getVirtualViewDescription(type, value);
            event.setContentDescription(description);
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            node.setClassName(getClass().getName());
            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);

            final int type = getTypeFromId(virtualViewId);
            final int value = getValueFromId(virtualViewId);
            final CharSequence description = getVirtualViewDescription(type, value);
            node.setContentDescription(description);

            getBoundsForVirtualView(virtualViewId, mTempRect);
            node.setBoundsInParent(mTempRect);

            final boolean selected = isVirtualViewSelected(type, value);
            node.setSelected(selected);

            final int nextId = getVirtualViewIdAfter(type, value);
            if (nextId != INVALID_ID) {
                node.setTraversalBefore(RadialTimePickerView.this, nextId);
            }
        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            if (action == AccessibilityNodeInfoCompat.ACTION_CLICK) {
                final int type = getTypeFromId(virtualViewId);
                final int value = getValueFromId(virtualViewId);
                if (type == TYPE_HOUR) {
                    final int hour = mIs24HourMode ? value : hour12To24(value, mAmOrPm);
                    return true;
                } else if (type == TYPE_MINUTE) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);

            info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD);
        }

        @Override
        public boolean performAccessibilityAction(View host, int action, Bundle arguments) {
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true;
            }

            switch (action) {
                case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD:
                    adjustPicker(1);
                    return true;
                case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD:
                    adjustPicker(-1);
                    return true;
            }

            return false;
        }

        private void adjustPicker(int step) {
            final int stepSize;
            final int initialStep;
            final int maxValue;
            final int minValue;
            if (mShowHours) {
                stepSize = 1;

                final int currentHour24 = getCurrentHour();
                if (mIs24HourMode) {
                    initialStep = currentHour24;
                    minValue = 0;
                    maxValue = 23;
                } else {
                    initialStep = hour24To12(currentHour24);
                    minValue = 1;
                    maxValue = 12;
                }
            } else {
                stepSize = 5;
                initialStep = getCurrentMinute() / stepSize;
                minValue = 0;
                maxValue = 55;
            }

            final int nextValue = (initialStep + step) * stepSize;
            final int clampedValue = SUtils.constrain(nextValue, minValue, maxValue);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            final int id;
            final int degrees = getDegreesFromXY(x, y, true);
            if (degrees != -1) {
                final int snapDegrees = snapOnly30s(degrees, 0) % FULL_ANGLE;
                if (mShowHours) {
                    final boolean isOnInnerCircle = getInnerCircleFromXY(x, y);
                    final int hour24 = getHourForDegrees(snapDegrees, isOnInnerCircle);
                    final int hour = mIs24HourMode ? hour24 : hour24To12(hour24);
                    id = makeId(TYPE_HOUR, hour);
                } else {
                    final int current = getCurrentMinute();
                    final int touched = getMinuteForDegrees(degrees);
                    final int snapped = getMinuteForDegrees(snapDegrees);

                    // If the touched minute is closer to the current minute
                    // than it is to the snapped minute, return current.
                    final int currentOffset = getCircularDiff(current, touched, MINUTES_IN_CIRCLE);
                    final int snappedOffset = getCircularDiff(snapped, touched, MINUTES_IN_CIRCLE);
                    final int minute;
                    if (currentOffset < snappedOffset) {
                        minute = current;
                    } else {
                        minute = snapped;
                    }
                    id = makeId(TYPE_MINUTE, minute);
                }
            } else {
                id = INVALID_ID;
            }

            return id;
        }

        /**
         * Returns the difference in degrees between two values along a circle.
         *
         * @param first  value in the range [0,max]
         * @param second value in the range [0,max]
         * @param max    the maximum value along the circle
         * @return the difference in between the two values
         */
        private int getCircularDiff(int first, int second, int max) {
            final int diff = Math.abs(first - second);
            final int midpoint = max / 2;
            return (diff > midpoint) ? (max - diff) : diff;
        }

        private int getVirtualViewIdAfter(int type, int value) {
            if (type == TYPE_HOUR) {
                final int nextValue = value + 1;
                final int max = mIs24HourMode ? 23 : 12;
                if (nextValue <= max) {
                    return makeId(type, nextValue);
                }
            } else if (type == TYPE_MINUTE) {
                final int current = getCurrentMinute();
                final int snapValue = value - (value % MINUTE_INCREMENT);
                final int nextValue = snapValue + MINUTE_INCREMENT;
                if (value < current && nextValue > current) {
                    // The current value is between two snap values.
                    return makeId(type, current);
                } else if (nextValue < MINUTES_IN_CIRCLE) {
                    return makeId(type, nextValue);
                }
            }
            return INVALID_ID;
        }

        private int hour12To24(int hour12, int amOrPm) {
            int hour24 = hour12;
            if (hour12 == 12) {
                if (amOrPm == AM) {
                    hour24 = 0;
                }
            } else if (amOrPm == PM) {
                hour24 += 12;
            }
            return hour24;
        }

        private int hour24To12(int hour24) {
            if (hour24 == 0) {
                return 12;
            } else if (hour24 > 12) {
                return hour24 - 12;
            } else {
                return hour24;
            }
        }

        private void getBoundsForVirtualView(int virtualViewId, Rect bounds) {
            final float radius;
            final int type = getTypeFromId(virtualViewId);
            final int value = getValueFromId(virtualViewId);
            final float centerRadius;
            final float degrees;
            if (type == TYPE_HOUR) {
                final boolean innerCircle = getInnerCircleForHour(value);
                if (innerCircle) {
                    centerRadius = mCircleRadius - mTextInset[HOURS_INNER];
                    radius = mSelectorRadius;
                } else {
                    centerRadius = mCircleRadius - mTextInset[HOURS];
                    radius = mSelectorRadius;
                }

                degrees = getDegreesForHour(value);
            } else if (type == TYPE_MINUTE) {
                centerRadius = mCircleRadius - mTextInset[MINUTES];
                degrees = getDegreesForMinute(value);
                radius = mSelectorRadius;
            } else {
                // This should never happen.
                centerRadius = 0;
                degrees = 0;
                radius = 0;
            }

            final double radians = Math.toRadians(degrees);
            final float xCenter = mXCenter + centerRadius * (float) Math.sin(radians);
            final float yCenter = mYCenter - centerRadius * (float) Math.cos(radians);

            bounds.set((int) (xCenter - radius), (int) (yCenter - radius),
                    (int) (xCenter + radius), (int) (yCenter + radius));
        }

        private CharSequence getVirtualViewDescription(int type, int value) {
            final CharSequence description;
            if (type == TYPE_HOUR || type == TYPE_MINUTE) {
                description = Integer.toString(value);
            } else {
                description = null;
            }
            return description;
        }

        private boolean isVirtualViewSelected(int type, int value) {
            return type == TYPE_HOUR ?
                    getCurrentHour() == value
                    : (type == TYPE_MINUTE && getCurrentMinute() == value);
        }

        private int makeId(int type, int value) {
            //noinspection PointlessBitwiseExpression
            return type << SHIFT_TYPE | value << SHIFT_VALUE;
        }

        private int getTypeFromId(int id) {
            //noinspection PointlessBitwiseExpression
            return id >>> SHIFT_TYPE & MASK_TYPE;
        }

        private int getValueFromId(int id) {
            return id >>> SHIFT_VALUE & MASK_VALUE;
        }
    }
}
