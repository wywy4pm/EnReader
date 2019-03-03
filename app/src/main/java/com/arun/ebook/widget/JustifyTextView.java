package com.arun.ebook.widget;

/**
 * Created by Administrator on 2018/4/8.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import com.arun.ebook.bean.ConfigData;
import com.arun.ebook.listener.LongPressListener;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.listener.TranslateListener;
import com.arun.ebook.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by du on 17/9/27.
 */
public class JustifyTextView extends AppCompatTextView {

    private int mLineY = 0;//总行高
    private int mViewWidth;//TextView的总宽度
    private TextPaint paint;
    private TranslateListener translateListener;
    private Handler mHandler;
    //private PageViewListener pageViewListener;

    public JustifyTextView(Context context) {
        super(context);
        init();
    }

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JustifyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mHandler = new Handler();
    }

    public void setTranslateListener(TranslateListener translateListener) {
        this.translateListener = translateListener;
    }

    public void setPageViewListener(PageViewListener pageViewListener) {
        //this.pageViewListener = pageViewListener;
    }

    private List<String> trans_words;

    public void setTrans_words(List<String> trans_words) {
        this.trans_words = trans_words;
    }

    /*private List<NewBookWordBean> paraText;

    public void setParaText(List<NewBookWordBean> paraText) {
        this.paraText = paraText;
    }*/

    private String paraSeq;

    public void setParaSeq(String paraSeq) {
        this.paraSeq = paraSeq;
    }

    private int book_id;

    public void setBookId(int book_id) {
        this.book_id = book_id;
    }

    private int page_id;

    public void setPageId(int page_id) {
        this.page_id = page_id;
    }

    /*@Override
    public void setTextColor(int color) {
        paint.setColor(color);
        super.setTextColor(color);
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //resize();
    }

    /**
     * 去除当前页无法显示的字
     *
     * @return 去掉的字数
     */
    public void resize() {
        CharSequence oldContent = getText();
        int start = 0;
        int end = 0;
        if (isForward) {
            start = 0;
            end = getCharNum();
        } else {
            start = oldContent.length() - getCharNum();
            end = oldContent.length();
        }
        CharSequence newContent = oldContent.subSequence(start, end);
        if (oldContent.length() - newContent.length() > 0) {
            setText(newContent);
        }
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        if (getLayout() != null) {
            return getLayout().getLineEnd(getLineNum());
        }
        return 0;
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        int lineNum = 0;
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        if (layout != null) {
            lineNum = layout.getLineForVertical(topOfLastLine);
        }
        return lineNum;
    }

    private Paint.FontMetricsInt fontMetricsInt;
    private boolean isForward = true;

    public void setMovePage(boolean isForward) {//true从上往下读,false从下往上读
        //setGravity(isForward ? Gravity.TOP : Gravity.BOTTOM);
        this.isForward = isForward;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("TAG", "Drawing TextView Address= " + this.toString());
        if (isShowPop) {
            super.onDraw(canvas);
        } else {
            //设置remove间距
            if (fontMetricsInt == null) {
                fontMetricsInt = new Paint.FontMetricsInt();
                getPaint().getFontMetricsInt(fontMetricsInt);
            }
            //canvas.translate(0, fontMetricsInt.top - fontMetricsInt.ascent);
            mLineY = getPaddingTop();
            //mViewWidth = getMeasuredWidth();
            if (isForward) {
                drawNextPage(canvas);
            } else {
                drawPrePage(canvas);
            }
        }
    }

    private int startIndex = 0;

    private void drawNextPage(Canvas canvas) {
        mLineY += (int) getTextSize();
        String text = getText().toString();
        Layout layout = getLayout();
        if (layout != null) {
            int lineCount = layout.getLineCount();
            startIndex = 0;
            for (int i = 0; i < lineCount; i++) {//每行循环
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                String lineText = text.substring(lineStart, lineEnd);//获取TextView每行中的内容
                Log.e("TAG", "lineText =" + lineText);
                drawMultiText(canvas, lineText);
                Log.e("TAG", "mLineY = " + mLineY + "  getLineHeight = " + getLineHeight() + "  getMeasuredHeight = " + getMeasuredHeight());
                mLineY += getLineHeight();//写完一行以后，高度增加一行的高度//1770
            }
        }
    }

    private void drawPrePage(Canvas canvas) {
        mLineY = getMeasuredHeight();
        String text = getText().toString();
        Layout layout = getLayout();
        if (layout != null) {
            int lineCount = layout.getLineCount();
            //Log.d("TAG", "lineCount = " + lineCount + " isForward = " + isForward);
            for (int i = lineCount - 1; i >= 0; i--) {//每行循环
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                String lineText = text.substring(lineStart, lineEnd);//获取TextView每行中的内容
                /*if (i == lineCount - 1) {
                    Log.e("TAG", "lineText =--------------------------------------------start--------------------------------------------");
                }
                Log.e("TAG", "lineText =" + lineText);
                if (i == 0) {
                    Log.e("TAG", "lineText =--------------------------------------------end---------------------------------------------");
                }*/
                drawMultiText(canvas, lineText);
                Log.e("TAG", "mLineY = " + mLineY + "  getLineHeight = " + getLineHeight() + "  getMeasuredHeight = " + getMeasuredHeight());
                mLineY -= getLineHeight();//写完一行以后，高度增加一行的高度//1770
            }

            /*for (int i = lineCount - 1; i >= 0; i--) {//每行循环
                int lineStart = layout.getLineStart(lineCount - 1 - i);
                int lineEnd = layout.getLineEnd(lineCount - 1 - i);
                String lineText = text.substring(lineStart, lineEnd);//获取TextView每行中的内容
                Log.d("TAG", " lineText =" + lineText);
                if (lineText.length() > 0) {
                    char charEnd = lineText.charAt(lineText.length() - 1);
                    if (charEnd == '\n') {
                        drawLineText(i, canvas, lineText);
                        Log.e("TAG", "mLineY = " + mLineY + "  getLineHeight = " + getLineHeight() + "  getMeasuredHeight = " + getMeasuredHeight());
                        mLineY -= getLineHeight();
                        if (i > 0) {
                            for (int j = i + 1; j < lineCount; j++) {
                                int preLineStart = layout.getLineStart(lineCount - 1 - j);
                                int preLineEnd = layout.getLineEnd(lineCount - 1 - j);
                                String preLineText = text.substring(preLineStart, preLineEnd);
                                if (preLineText.length() > 0) {
                                    char preCharEnd = preLineText.charAt(preLineText.length() - 1);
                                    if (preCharEnd != '\n') {
                                        drawLineText(j, canvas, preLineText);
                                        Log.e("TAG", "mLineY = " + mLineY + "  getLineHeight = " + getLineHeight() + "  getMeasuredHeight = " + getMeasuredHeight());
                                        mLineY -= getLineHeight();
                                    } else {
                                        break;
                                    }
                                } else {
                                    drawLineText(i, canvas, lineText);
                                    Log.e("TAG", "mLineY = " + mLineY + "  getLineHeight = " + getLineHeight() + "  getMeasuredHeight = " + getMeasuredHeight());
                                    mLineY -= getLineHeight();
                                }
                            }
                        }
                    }
                } else {
                    drawLineText(i, canvas, lineText);
                    Log.e("TAG", "mLineY = " + mLineY + "  getLineHeight = " + getLineHeight() + "  getMeasuredHeight = " + getMeasuredHeight());
                    mLineY -= getLineHeight();
                }
            }*/
        }
    }

    //private int paraSpace;

    public void setParaSpace(int paraSpace) {
        /*this.paraSpace = paraSpace;
        invalidate();*/
    }

    /*private void drawLineText(int i, Canvas canvas, String lineText) {
     *//*if (i == touchLine) {
            drawMultiText(canvas, lineText);
        } else {
            canvas.drawText(lineText, 0, mLineY, paint);
        }*//*
        //canvas.drawText(lineText, 0, mLineY, paint);
        drawMultiText(canvas, lineText);
    }*/

    private void drawMultiText(Canvas canvas, String lineText) {
        Log.d("TAG", "drawTextTestTest lineText =" + lineText);
        if (trans_words == null || trans_words.size() == 0) {
            paint.setColor(getCurrentTextColor());
            canvas.drawText(lineText, 0, mLineY, paint);
            return;
        }
        Layout layout = getLayout();
        List<String> newWords = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        long start = System.currentTimeMillis();
        for (int i = 0; i < lineText.length(); i++) {
            char one = lineText.charAt(i);
            if (StringUtils.isPunctuation(String.valueOf(one)) || StringUtils.isSpace(one)) {
                if (builder.length() > 0) {
                    newWords.add(builder.toString());
                    newWords.add(String.valueOf(one));
                } else {
                    newWords.add(String.valueOf(one));
                    //newWords.add(builder.toString());
                }
                builder.delete(0, builder.length());
            } else {
                builder.append(one);
            }
        }

        if (builder.length() > 0) {
            newWords.add(builder.toString());
        }
        if (newWords.size() > 0) {
            for (int i = 0; i < newWords.size(); i++) {
                String newText = newWords.get(i);
                boolean isTranslated = false;
                for (int j = 0; j < trans_words.size(); j++) {
                    String translatedWord = trans_words.get(j);
                    if (!TextUtils.isEmpty(translatedWord) && translatedWord.equals(newText)) {
                        isTranslated = true;
                        float pointX = layout.getSecondaryHorizontal(startIndex);
                        paint.setColor(ConfigData.lightColor);
                        canvas.drawText(newText, pointX, mLineY, paint);
                        startIndex += newText.length();
                        break;
                    }
                }
                if (!isTranslated) {
                    float pointX = layout.getSecondaryHorizontal(startIndex);
                    paint.setColor(getCurrentTextColor());
                    canvas.drawText(newText, pointX, mLineY, paint);
                    startIndex += newText.length();
                }
            }
        }

       /* if (paraText != null) {
            for (int i = 0; i < newWords.size(); i++) {
                for (int j = 0; j < paraText.size(); j++) {
                    if (!TextUtils.isEmpty(newWords.get(i)) && newWords.get(i).equals(paraText.get(j).content)) {
                        if (paraText.get(j).translated == 1) {
                            float pointX = layout.getSecondaryHorizontal(startIndex);
                            String newText = newWords.get(i);
                            paint.setColor(ConfigData.lightColor);
                            canvas.drawText(newText, pointX, mLineY, paint);
                            //Log.d("TAG", "drawTextTestTest word =" + newText + " startIndex = " + startIndex + " pointX = " + pointX);
                            startIndex += newText.length();
                            break;
                        } else {
                            float pointX = layout.getSecondaryHorizontal(startIndex);
                            String newText = newWords.get(i);
                            paint.setColor(getCurrentTextColor());
                            canvas.drawText(newText, pointX, mLineY, paint);
                            //Log.d("TAG", "drawTextTestTest word =" + newText + " startIndex = " + startIndex + " pointX = " + pointX);
                            startIndex += newText.length();
                            break;
                        }
                    }
                }
                if (newWords.get(i).equals(" ")) {
                    float pointX = layout.getSecondaryHorizontal(startIndex);
                    String newText = newWords.get(i);
                    paint.setColor(getCurrentTextColor());
                    canvas.drawText(newText, pointX, mLineY, paint);
                    //Log.d("TAG", "drawTextTestTest word =" + newText + " startIndex = " + startIndex + " pointX = " + pointX);
                    startIndex += newText.length();
                }
            }
        }*/
    }

    /**
     * 重绘此行.
     *
     * @param currentLine
     * @param canvas      画布
     * @param lineText    该行所有的文字
     * @param lineWidth   该行每个文字的宽度的总和
     */
    /*private void drawScaleText(int currentLine, Canvas canvas, String lineText, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineText)) {
            String blanks = "  ";
            if (currentLine == touchLine) {
                drawMultiText(canvas, lineText, isParaStartLine);
            } else {
                canvas.drawText(blanks, x, mLineY, paint);
            }
            float width = StaticLayout.getDesiredWidth(blanks, paint);
            x += width;
            lineText = lineText.substring(3);
        }
        //比如说一共有5个字，中间有4个间隔，
        //那就用整个TextView的宽度 - 5个字的宽度，
        //然后除以4，填补到这4个空隙中
        float interval = (mViewWidth - lineWidth) / (lineText.length() - 1);
        for (int i = 0; i < lineText.length(); i++) {
            String character = String.valueOf(lineText.charAt(i));
            float cw = StaticLayout.getDesiredWidth(character, paint);
            if (currentLine == touchLine) {
                drawMultiText(canvas, lineText, isParaStartLine);
            } else {
                canvas.drawText(character, x, mLineY, paint);
            }
            x += (cw + interval);
        }
    }*/


    /**
     * 判断是不是段落的第一行.
     * 一个汉字相当于一个字符，此处判断是否为第一行的依据是：
     * 字符长度大于3且前两个字符为空格
     *
     * @param lineText 该行所有的文字
     */
    private boolean isFirstLineOfParagraph(String lineText) {
        return lineText.length() > 3 && lineText.charAt(0) == ' ' && lineText.charAt(1) == ' ';
    }

    /**
     * 判断需不需要缩放.
     *
     * @param lineText 该行所有的文字
     * @return true 该行最后一个字符不是换行符  false 该行最后一个字符是换行符
     */
    private boolean needScale(String lineText) {
        if (lineText.length() == 0) {
            return false;
        } else {
            return lineText.charAt(lineText.length() - 1) != '\n';
        }
    }

    private LongPressListener longPressListener;

    public void setOnLongPressListener(LongPressListener longPressListener) {
        this.longPressListener = longPressListener;
    }

    private float downX = 0;
    private float downY = 0;
    private boolean isLongPress;
    private boolean isShowPop;

    public void setShowPop(boolean showPop) {
        isShowPop = showPop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;
                /* 长按操作 */
                isLongPress = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLongPress = true;
                        Log.d("TAG", "---------------onTouchEvent ACTION_LONG_PRESS------------------");
                        if (longPressListener != null) {
                            longPressListener.onLongPress();
                        }
                    }
                }, 500);
                return isTouchWord(this, event);
            case MotionEvent.ACTION_MOVE:
                int lastX = (int) event.getX();
                int lastY = (int) event.getY();
                Log.d("TAG", "---------------onTouchEvent ACTION_MOVE------------------");
                if (Math.abs(lastX - downX) > 20 || Math.abs(lastY - downY) > 20) {
                    this.mHandler.removeCallbacksAndMessages(null);
                }
                break;
            case MotionEvent.ACTION_UP:
                this.mHandler.removeCallbacksAndMessages(null);
                Log.d("TAG", "---------------onTouchEvent ACTION_UP------------------");
                if (!isLongPress) {
                    isLongPress = false;
                    if (!isShowPop) {
                        if (translateListener != null && !TextUtils.isEmpty(touchWord)) {
                            setTouchWord(touchWord, translateListener);
                            return true;
                        }
                    } else {
                        if (longPressListener != null) {
                            longPressListener.onHidePop();
                            return true;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void removeHandlerCallBack() {
        this.mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 *
     *
     * @param thisX         移动时X坐标 *
     * @param thisY         移动时Y坐标 *
     * @param lastDownTime  按下时间 *
     * @param thisEventTime 移动时间 *
     * @param longPressTime 判断长按时间的阀值
     */
    public static boolean isLongPressed(float lastX, float lastY, float thisX,
                                        float thisY, long lastDownTime, long thisEventTime,
                                        long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

   /* private int touchLine = -1;
    private int touchWordStartIndex = 0;
    private int touchWordEndIndex = 0;
    private float touchWordStartX = 0;
    private float touchWordEndX = 0;*/

    /*public void reset() {
        touchLine = -1;
        touchWordStartIndex = 0;
        touchWordEndIndex = 0;
        touchWordStartX = 0;
        touchWordEndX = 0;
    }*/
    private String touchWord;

    private boolean isTouchWord(TextView textView, MotionEvent event) {
        touchWord = "";
        boolean isTouch = false;
        float x = event.getX();
        float y = event.getY();
        //因为行设置行间距后，文本在顶部，空白在底部，加上一半的行间距，可以实现与文本垂直居中一样的效果
        y = y + textView.getLineSpacingExtra() / 2 - textView.getPaddingTop();
        //在顶部padding空白处点击
        /*if (y < 0) {
            return false;
        }*/
        int singleLineHeight = textView.getLineHeight();
        Layout layout = textView.getLayout();
        //获取点击当前所在行
        int lineNumber = (int) (y / singleLineHeight);
        if (lineNumber < 0) {
            lineNumber = 0;
        }
//        touchLine = lineNumber;
        if (lineNumber <= textView.getLineCount() - 1) {
            int lineStart = layout.getLineStart(lineNumber);
            int lineEnd = layout.getLineEnd(lineNumber);
            String lineText = textView.getText().toString().substring(lineStart, lineEnd);
            if (!TextUtils.isEmpty(lineText) && lineText.length() > 0) {
                float minCha = 0;
                int minOneIndex = 0;
                for (int i = 0; i < lineText.length(); i++) {
                    float oneX = layout.getSecondaryHorizontal(lineStart + i);
                    float currentCha = Math.abs(x - oneX);
                    if (i == 0) {
                        minCha = currentCha;
                        minOneIndex = i;
                    } else {
                        if (currentCha < minCha) {
                            minCha = currentCha;
                            minOneIndex = i;
                        }
                    }
                }
                if (minCha > getTextSize()) {
                    isTouch = false;
                } else {
                    int startIndex = 0;
                    int endIndex = 0;
                    char ch = lineText.charAt(minOneIndex);
                    if (StringUtils.isEnChar(ch)) {//点击单词
                        for (int i = minOneIndex; i < lineText.length(); i++) {
                            char c = lineText.charAt(i);
                            if (StringUtils.isSpace(c) || StringUtils.isPunctuation(String.valueOf(c))) {
                                endIndex = i;
                                break;
                            } else {
                                if (i == lineText.length() - 1) {
                                    endIndex = i + 1;
                                }
                            }
                        }
                        for (int i = minOneIndex - 1; i >= 0; i--) {
                            char c = lineText.charAt(i);
                            if (!StringUtils.isEnChar(c)) {
                                startIndex = i + 1;
                                break;
                            }
                        }
                        String word = lineText.substring(startIndex, endIndex);
                        Log.d("TAG", "getTouchWord =" + word);
                        touchWord = word;
                        isTouch = true;
                    } else {
                        isTouch = false;
                    }
                }
            } else {
                isTouch = false;
            }
        } else {
            isTouch = false;
        }
        return isTouch;
    }

    private void setTouchWord(String word, TranslateListener translateListener) {
        addTranslated(word);
        invalidate();
        if (translateListener != null) {
            translateListener.showTransDialog(book_id, word, page_id);
        }
    }

    private void addTranslated(String word) {
        if (trans_words == null) {
            trans_words = new ArrayList<>();
        }
        trans_words.add(word);
    }
}
