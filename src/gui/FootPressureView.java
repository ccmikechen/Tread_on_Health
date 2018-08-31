package gui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import data.NikeDataGetter;
import interpolation.BicubicInterpolation;
import sensor.ShoePoint;

public class FootPressureView extends View {

    private static final long serialVersionUID = 1159558007515420653L;

    private final static double SCALE = 0.2;

    private NikeDataGetter getter = null;
    private int shoeWidth;
    private int shoeHeight;

    private int gridWidth;
    private int gridHeight;
    private float a, b, c, d;
    private ShoePoint A, B, C, D;

    private double[] xPoints;
    private double[] yPoints;
    private double[][] xyPressures;

    private Paint paint = new Paint();
    
    public FootPressureView(Context context, NikeDataGetter getter) {

        super(context);
        this.setBackgroundColor(Color.WHITE);
        this.getter = getter;
        this.A = getter.getPointA();
        this.B = getter.getPointB();
        this.C = getter.getPointC();
        this.D = getter.getPointD();
        this.shoeWidth = getter.getWidth();
        this.shoeHeight = getter.getHeight();

        xPoints = new double[(int) (shoeWidth * SCALE)];
        yPoints = new double[(int) (shoeHeight * SCALE)];
        xyPressures = new double[xPoints.length][yPoints.length];

        for (int x = 0; x < xPoints.length; x++)
            xPoints[x] = x;
        for (int y = 0; y < yPoints.length; y++)
            yPoints[y] = y;
    }

    @Override
    public void onDraw(Canvas canvas) {
        gridWidth = this.getWidth()/shoeWidth + 1;
        gridHeight = this.getHeight()/shoeHeight + 1;

        super.onDraw(canvas);
        getPressureData();
        drawGridLine(canvas);
        drawSensorPoint(canvas);
        drawGridColor(canvas);
        drawPressureCenterPoint(canvas);
        invalidate();
    }

    private void drawSensorPoint(Canvas canvas) {
        drawGrid(canvas, A, Color.BLACK);
        drawGrid(canvas, B, Color.BLACK);
        drawGrid(canvas, C, Color.BLACK);
        drawGrid(canvas, D, Color.BLACK);
    }

    private void drawGridLine(Canvas canvas) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i <= shoeWidth; i++)
            canvas.drawLine(toRealX(i), 0, toRealX(i), this.getHeight(), paint);
        for (int i = 0; i <= shoeHeight; i++)
            canvas.drawLine(0, toRealY(i), this.getWidth(), toRealY(i), paint);
    }

    private void drawGridColor(Canvas canvas) {
        setPressures();
        BicubicInterpolation bicubic = new BicubicInterpolation(xPoints, yPoints, xyPressures);
        for (int x = 0; x < shoeWidth; x++)
            for (int y = 0; y < shoeHeight; y++)
                drawGrid(canvas, x, y, pressureToColor(bicubic.interpolate(x * SCALE, y * SCALE)));
    }

    private void setPressures() {
        xyPressures[(int) (A.x * SCALE)][(int) (A.y * SCALE)] = a;
        xyPressures[(int) (B.x * SCALE)][(int) (B.y * SCALE)] = b;
        xyPressures[(int) (C.x * SCALE)][(int) (C.y * SCALE)] = c;
        xyPressures[(int) (D.x * SCALE)][(int) (D.y * SCALE)] = d;
    }

    private void drawPressureCenterPoint(Canvas canvas) {
        int pointSize = (this.getWidth() + this.getHeight()) / 40;
        ShoePoint c = getter.getCenterOfPressurePoint();
        paint.setColor(Color.BLUE);
        if (!Float.isNaN(c.x))
            canvas.drawCircle(toRealX(c.x), toRealY(c.y), pointSize, paint);

    }

    private void getPressureData() {
        a = getter.getA()+1;
        d = getter.getD()+1;
        b = getter.getB()+1;
        c = getter.getC()+1;
    }

    private int pressureToColor(double pressure) {
        if (pressure < 10)
            return Color.WHITE;
        pressure = Math.min(pressure, 70);
        int r = Math.max(0, (int) (pressure * 510 / 70));
        return Color.rgb(Math.min(r, 255), Math.min(510 - r, 255), 0);
    }

    private void drawGrid(Canvas canvas, ShoePoint point, int color) {
        drawGrid(canvas, point.x, point.y, color);
    }

    private void drawGrid(Canvas canvas, float x, float y, int color) {
        paint.setColor(color);
        canvas.drawRect(toRealX(x), toRealY(y), toRealX(x) + gridWidth, toRealY(y) + gridHeight, paint);
    }

    private int toRealX(float x) {
        return (int) (x * this.getWidth() / shoeWidth);
    }

    private int toRealY(float y) {
        return (int) ((shoeHeight - (y+1)) * this.getHeight() / shoeHeight);
    }

}