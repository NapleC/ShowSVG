package com.hl.batik.NormalSVG;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by Administrator on 2018/4/23.
 */

public class NormalItem {

    private Path path;
    private int drawColor;
    private int strokeColor;
    private int strokeWidth;
    private int strokeMiterLimit;

    public NormalItem(Path path) {
        this.path = path;
    }

    public NormalItem(Path path, String strokeColor, int strokeWidth ,int strokeMiterLimit) {
        this.path = path;
        this.strokeColor = Color.parseColor(strokeColor);
        this.strokeWidth = strokeWidth;
        this.strokeMiterLimit = strokeMiterLimit;
    }

    /**
     * 绘制地图path
     * @param canvas
     * @param paint
     * @param isSelect
     */
    public void draw(Canvas canvas, Paint paint, boolean isSelect){
//        if(isSelect){
//            //画阴影图层
//            paint.setStrokeWidth(strokeWidth);
//            paint.setShadowLayer(8,0,0,0xffffff);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.BLACK);
//            canvas.drawPath(path,paint);
//            //画区域path
//            paint.clearShadowLayer();
//            paint.setStrokeWidth(strokeWidth);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(drawColor);
//            canvas.drawPath(path,paint);
//        }else{
            //画线条
            paint.clearShadowLayer();
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawPath(path,paint);
        paint.setStrokeMiter(strokeMiterLimit);
//            //画区域
//            paint.setStrokeWidth(strokeWidth);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(drawColor);
//            canvas.drawPath(path,paint);
//        }
    }

    /**
     * 判断当前点击坐标是否在path范围内
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(int x,int y){
        RectF rectF = new RectF();
        path.computeBounds(rectF,true);
        Region region = new Region();
        region.setPath(path,new Region((int)rectF.left,(int)rectF.top,(int)rectF.right,(int)rectF.bottom));
//判断X,Y是否在region区域范围内
        if(region.contains(x,y)) return true;
        return false;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }
}
