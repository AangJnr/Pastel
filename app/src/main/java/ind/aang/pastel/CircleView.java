package ind.aang.pastel;

/**
 * Created by AangJnr on 10/5/16.
 */

        import android.content.Context;
        import android.content.res.TypedArray;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.util.AttributeSet;
        import android.view.View;

public class CircleView extends View {

    private int circleRadius = 20;
    private int strokeWidth = 15;
    private int fillColor = 0X00FFFFFF;

    public CircleView(Context context) {
        super(context);

        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray aTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView, defStyleAttr, 0);


        fillColor = aTypedArray.getColor(R.styleable.CircleView_fillColor, fillColor);
        circleRadius = aTypedArray.getDimensionPixelSize(R.styleable.CircleView_circleRadius, circleRadius);


        aTypedArray.recycle();

        init();
    }

    public CircleView(Context context, int fillColor, int circleRadius) {
        super(context);

        this.fillColor = fillColor;
        this.circleRadius = circleRadius;


        init();
    }

    private void init() {
        this.setMinimumHeight(circleRadius * 2 + strokeWidth);
        this.setMinimumWidth(circleRadius * 2 + strokeWidth);
        this.setSaveEnabled(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = this.getWidth();
        int h = this.getHeight();

        int ox = w/2;
        int oy = h/2;


        canvas.drawCircle(ox, oy, circleRadius, getFill());
    }



    private Paint getFill()
    {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(fillColor);
        p.setStyle(Paint.Style.FILL);
        return p;
    }



    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }


    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }


}