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

public class XmlSVG extends View {
    private static final String TAG = XmlSVG.class.getName();
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

    public XmlSVG(Context context) {
        super(context);
    }

    public XmlSVG(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "onDown x:" + e.getX() + ";y:" + e.getY());
                handleTouch(e.getX(), e.getY());
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
        canvas.scale(scale, scale);
        if (normalItems != null) {
            for (NormalItem item : normalItems) {
                if (item != selectItem) {
                    item.draw(canvas, mPaint, false);
                }
            }
            if (selectItem != null) {
                selectItem.draw(canvas, mPaint, true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        NormalItem tempItem = null;
        if (normalItems != null) {
            for (NormalItem item : normalItems) {
                if (item.isTouch((int) (x / scale), (int) (y / scale))) {
                    tempItem = item;
                    break;
                }
            }
            if (tempItem != null) {
                selectItem = tempItem;
                postInvalidate();
            }
        }
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.lion);
            //采用Dom解析器解析xml
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);
                Element rootelement = doc.getDocumentElement();
                miniWidth = Integer.parseInt(rootelement.getAttribute("width").replace("px", ""));
                miniHeight = Integer.parseInt(rootelement.getAttribute("height").replace("px", ""));
                Log.d(TAG, "miniWidth: " + miniWidth);
                Log.d(TAG, "miniHeight: " + miniHeight);
                NodeList items = rootelement.getElementsByTagName("polyline");
                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("points");

                    String tempPathData = pathData.replaceAll(",", " ");
                    String[] arrays = tempPathData.split("\\s+");
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0, lenS = arrays.length; j < lenS; j++) {
                        if (j > 1 && j % 2 == 0 && j < lenS) {
                            sb.append(" L " + String.valueOf(arrays[j]));
                        } else {
                            sb.append(" " + String.valueOf(arrays[j]));
                        }
                    }
                    sb.toString();

                    String strokeColor = element.getAttribute("stroke");
                    int strokeWidth = 1;
                    int strokeMiterLimit = Integer.parseInt(element.getAttribute("stroke-miterlimit"));
                    Path path = PathParser.createPathFromPathData("M " + sb);

                    NormalItem item = new NormalItem(path, strokeColor, strokeWidth, strokeMiterLimit);
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
