package com.hl.batik.NormalSVG;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hl.batik.utils.PathParser;
import com.hl.batik.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 2018/4/23.
 * 用 http://inloop.github.io/svg2android/ 网站将 SVG 资源转换成相应的 Android 代码，
 * 利用 DOM 解析 SVG 的代码，将属性 pathData 数据封装。
 */

public class NormalSVG  extends View {
    private static final String TAG = NormalSVG.class.getName();
    private List<NormalItem> normalItems = new ArrayList<NormalItem>();
    //被点击的区域
    private NormalItem selectItem;
    private int miniWidth;
    private int miniHeight;
    //缩放0.3倍
    private float scale = 0.5f;
    private Context mContext;
    private Paint mPaint;
    GestureDetectorCompat gestureDetectorCompat;

    public NormalSVG(Context context) {
        super(context);
    }

    public NormalSVG(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        gestureDetectorCompat = new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG,"onDown x:"+e.getX()+";y:"+e.getY());
                handleTouch(e.getX(),e.getY());
                return true;
            }
        });
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //将画布缩放0.3倍
        canvas.scale(scale,scale);
        if(normalItems != null){
            for(NormalItem item:normalItems){
                if(item != selectItem){
                    item.draw(canvas,mPaint,false);
                }
            }
            if(selectItem != null){
                selectItem.draw(canvas,mPaint,true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    private void handleTouch(float x,float y){
        NormalItem tempItem = null;
        if(normalItems != null){
            for(NormalItem item : normalItems){
                if(item.isTouch((int)(x/scale),(int)(y/scale))){
                    tempItem = item;
                    break;
                }
            }
            if(tempItem != null) {
                selectItem = tempItem;
                postInvalidate();
            }
        }
    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.lion_svg);
            //采用Dom解析器解析xml
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);
                Element rootelement = doc.getDocumentElement();
                miniWidth = Integer.parseInt(rootelement.getAttribute("android:viewportWidth"));
                miniHeight = Integer.parseInt(rootelement.getAttribute("android:viewportHeight"));
                Log.d(TAG,"miniWidth: "+miniWidth);
                Log.d(TAG,"miniHeight: "+miniHeight);
                NodeList items = rootelement.getElementsByTagName("path");
                for(int i=0;i<items.getLength();i++){
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("android:pathData");
                    String strokeColor = element.getAttribute("android:strokeColor");
                    int strokeWidth = Integer.parseInt(element.getAttribute("android:strokeWidth"));
                    int strokeMiterLimit = Integer.parseInt(element.getAttribute("android:strokeMiterLimit"));
                    Path path = PathParser.createPathFromPathData(pathData);
                    NormalItem item = new NormalItem(path,strokeColor,strokeWidth,strokeMiterLimit);
                    normalItems.add(item);
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: " + miniWidth + "---" + miniHeight);
        setMeasuredDimension(miniWidth, miniHeight);
    }
}
