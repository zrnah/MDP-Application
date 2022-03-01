package com.example.mdpapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ObstacleView extends View {
    private static final String TAG = "ObstacleGrid";
    private final int numColumns;
    private final int numRows;
    private int cellWidth, cellHeight;

    private final Paint blackPaint = new Paint();
    private final Paint whitePaint = new Paint();
    private final Paint yellowPaint = new Paint();
    private final Paint targetScannedColor = new Paint();

    private boolean showTargetID;

    @NonNull
    private final GestureDetector gestureDetector;

    private PixelGridView.Obstacle obstacle;
    private PopupWindow popupWindow;

    private static final List<String> ValidTargetStrings = Arrays.asList("Alphabet_A", "Alphabet_B", "Alphabet_C",
            "Alphabet_D", "Alphabet_E", "Alphabet_F",
            "Alphabet_G", "Alphabet_H", "Alphabet_S",
            "Alphabet_T", "Alphabet_U", "Alphabet_V",
            "Alphabet_W", "Alphabet_X", "Alphabet_Y",
            "Alphabet_Z", "down_arrow", "bullseye",
            "eight", "five", "four", "left_arrow",
            "nine", "one", "right_arrow", "seven",
            "six", "stop", "three", "two", "up_arrow");

    JSONObject targetIdJSON;

    {
        try {
            targetIdJSON = new JSONObject("{\n" +
                    "    \"Alphabet_A\": 20,\n" +
                    "    \"Alphabet_B\": 21,\n" +
                    "    \"Alphabet_C\": 22,\n" +
                    "    \"Alphabet_D\": 23,\n" +
                    "    \"Alphabet_E\": 24,\n" +
                    "    \"Alphabet_F\": 25,\n" +
                    "    \"Alphabet_G\": 26,\n" +
                    "    \"Alphabet_H\": 27,\n" +
                    "    \"Alphabet_S\": 28,\n" +
                    "    \"Alphabet_T\": 29,\n" +
                    "    \"Alphabet_U\": 30,\n" +
                    "    \"Alphabet_V\": 31,\n" +
                    "    \"Alphabet_W\": 32,\n" +
                    "    \"Alphabet_X\": 33,\n" +
                    "    \"Alphabet_Y\": 34,\n" +
                    "    \"Alphabet_Z\": 35,\n" +
                    "    \"down_arrow\": 37,\n" +
                    "    \"eight\": 18,\n" +
                    "    \"five\": 15,\n" +
                    "    \"four\": 14,\n" +
                    "    \"left_arrow\": 39,\n" +
                    "    \"nine\": 19,\n" +
                    "    \"one\": 11,\n" +
                    "    \"right_arrow\": 38,\n" +
                    "    \"seven\": 17,\n" +
                    "    \"six\": 16,\n" +
                    "    \"stop\": 40,\n" +
                    "    \"three\": 13,\n" +
                    "    \"two\": 12,\n" +
                    "    \"up_arrow\": 36\n" +
                    "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private final Context cachedContext;

    public ObstacleView(@NonNull Context context) {
        this(context, null);
    }

    public ObstacleView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PixelGridView,
                0, 0);
        this.numColumns = typedArray.getInt(R.styleable.PixelGridView_columns, 0);
        this.numRows = typedArray.getInt(R.styleable.PixelGridView_rows, 0);

        typedArray.recycle();

        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(80);
        whitePaint.setTextAlign(Paint.Align.CENTER);
        targetScannedColor.setColor(Color.WHITE);
        targetScannedColor.setTextSize(90);
        targetScannedColor.setTextAlign(Paint.Align.CENTER);
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStrokeWidth(60);

        gestureDetector = new GestureDetector(context, new GestureListener());

        cachedContext = context;
    }

    public void setShowTargetID(boolean showTargetID) {
        this.showTargetID = showTargetID;
    }

    public void setObstacle(PixelGridView.Obstacle obstacle) {
        this.obstacle = obstacle;

        invalidate();
    }

    public PixelGridView.Obstacle getObstacle() {
        return obstacle;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / numColumns;
        cellHeight = getHeight() / numRows;

        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (numColumns == 0 || numRows == 0) {
            return;
        }
        if(obstacle != null){
            Log.d(TAG, "onDraw: direction: " + obstacle.direction);

            if (obstacle.targetID == null || obstacle.targetID.equals("bullseye")) {
                canvas.drawRect(0, 0, cellWidth, cellHeight, blackPaint);
                canvas.drawText(String.valueOf(obstacle.id), 0.5f * cellWidth, 0.65f * cellHeight, whitePaint);
            } else if (ValidTargetStrings.contains(obstacle.targetID)) {
                if(showTargetID){
                    try {
                        canvas.drawRect(0, 0, cellWidth, cellHeight, blackPaint);
                        canvas.drawText(String.valueOf(targetIdJSON.getInt(obstacle.targetID)), 0.5f * cellWidth, 0.65f * cellHeight, targetScannedColor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    RectF rect = new RectF(0, 0, cellWidth, cellHeight);
                    int resID = getResources().getIdentifier(obstacle.targetID.toLowerCase(Locale.ROOT), "drawable", cachedContext.getPackageName());
                    Bitmap obstacleBitmap = BitmapFactory.decodeResource(getResources(), resID);
                    canvas.drawBitmap(obstacleBitmap, null, rect, null);
                }
            }

            if (obstacle.direction.equals("N")) {
                canvas.drawLine(0, 0, cellWidth, 0, yellowPaint);
            } else if (obstacle.direction.equals("E")) {
                canvas.drawLine(cellWidth, 0, cellWidth, cellHeight, yellowPaint);
            } else if (obstacle.direction.equals("S")) {
                canvas.drawLine(0, cellHeight, cellWidth, cellHeight, yellowPaint);
            } else if (obstacle.direction.equals("W")) {
                canvas.drawLine(0, 0, 0, cellHeight, yellowPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            obstacle.direction = "E";
                        } else {
                            obstacle.direction = "W";
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        obstacle.direction = "S";
                    } else {
                        obstacle.direction = "N";
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            Log.d(TAG, "onFling: direction: " + obstacle.direction);
            popupWindow.dismiss();

            return result;
        }
    }
}