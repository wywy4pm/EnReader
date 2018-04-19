package com.arun.ebook.widget;

/**
 * Created by Administrator on 2018/4/8.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.utils.StringUtils;

/**
 * Created by du on 17/9/27.
 */
public class JustifyTextView extends AppCompatTextView {

    private int mLineY = 0;//总行高
    private int mViewWidth;//TextView的总宽度
    private TextPaint paint;
    private PageViewListener pageViewListener;

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
    }

    public void setPageViewListener(PageViewListener pageViewListener) {
        this.pageViewListener = pageViewListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resize();
    }

    /**
     * 去除当前页无法显示的字
     *
     * @return 去掉的字数
     */
    public void resize() {
        CharSequence oldContent = getText();
        CharSequence newContent = oldContent.subSequence(0, getCharNum());
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

    @Override
    protected void onDraw(Canvas canvas) {
        mLineY = 0;
        mViewWidth = getMeasuredWidth();//获取textview的实际宽度
        mLineY += getTextSize();

        String text = getText().toString();

        Layout layout = getLayout();
        if (layout != null) {
            int lineCount = layout.getLineCount();
            for (int i = 0; i < lineCount; i++) {//每行循环
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                String lineText = text.substring(lineStart, lineEnd);//获取TextView每行中的内容
                if (needScale(lineText)) {
                    if (i == touchLine) {
                        drawMultiText(canvas, lineText);
                    } else {
                        canvas.drawText(lineText, 0, mLineY, paint);
                    }
                    /*if (i == lineCount - 1) {//最后一行不需要重绘
                        if (i == touchLine) {
                            drawMultiText(canvas, lineText);
                        } else {
                            canvas.drawText(lineText, 0, mLineY, paint);
                        }
                        //canvas.drawText(lineText, 0, mLineY, paint);
                    } else {
                        float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
                        drawScaleText(i, canvas, lineText, width);
                    }*/
                } else {
                    if (i == touchLine) {
                        drawMultiText(canvas, lineText);
                    } else {
                        canvas.drawText(lineText, 0, mLineY, paint);
                    }
                }
                mLineY += getLineHeight();//写完一行以后，高度增加一行的高度
            }
        }
    }

    private void drawMultiText(Canvas canvas, String lineText) {
        Log.d("TAG", "drawMultiText:lineText =" + lineText);
        Log.d("TAG", "drawMultiText:lineText.length() = " + lineText.length());
        Log.d("TAG", "drawMultiText:touchWordStartIndex = " + touchWordStartIndex);
        if (!TextUtils.isEmpty(lineText) && lineText.length() > 0) {
            String touchLeft = lineText.substring(0, touchWordStartIndex);
            String touchRight = lineText.substring(touchWordEndIndex, lineText.length());
            String touchWord = lineText.substring(touchWordStartIndex, touchWordEndIndex);

            paint.setColor(getResources().getColor(R.color.text_common));
            canvas.drawText(touchLeft, 0, mLineY, paint);
            /*TextPaint textPaint = getPaint();
            textPaint.setColor(Color.RED);
            textPaint.drawableState = getDrawableState();*/
            paint.setColor(Color.RED);
            canvas.drawText(touchWord, touchWordStartX, mLineY, paint);

            paint.setColor(getResources().getColor(R.color.text_common));
            canvas.drawText(touchRight, touchWordEndX, mLineY, paint);
        }
    }

    /**
     * 重绘此行.
     *
     * @param currentLine
     * @param canvas      画布
     * @param lineText    该行所有的文字
     * @param lineWidth   该行每个文字的宽度的总和
     */
    private void drawScaleText(int currentLine, Canvas canvas, String lineText, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineText)) {
            String blanks = "  ";
            if (currentLine == touchLine) {
                drawMultiText(canvas, lineText);
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
                drawMultiText(canvas, lineText);
            } else {
                canvas.drawText(character, x, mLineY, paint);
            }
            x += (cw + interval);
        }
    }


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

    private float downX = 0;
    private float downY = 0;
    //private boolean isTurnPage;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;

                //Log.d("TAG", "---------------ACTION_DOWN------------------");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                reset();
                float moveX = eventX - downX;
                //float moveY = Math.abs(eventY - downY);
                if (pageViewListener != null) {
                    if (moveX > 30) {//上一页
                        pageViewListener.showPrePage();
                        return true;
                    } else if (moveX < -30) {
                        pageViewListener.showNextPage();
                        return true;
                    }
                    getTouchWord(this, event, pageViewListener);
                }
                //Log.d("TAG", "---------------ACTION_UP------------------");
                break;
        }
        return true;
    }

    private int touchLine = -1;
    private int touchWordStartIndex = 0;
    private int touchWordEndIndex = 0;
    private float touchWordStartX = 0;
    private float touchWordEndX = 0;

    public void reset() {
        touchLine = -1;
        touchWordStartIndex = 0;
        touchWordEndIndex = 0;
        touchWordStartX = 0;
        touchWordEndX = 0;
    }

    private void getTouchWord(TextView textView, MotionEvent event, PageViewListener pageViewListener) {
        float x = event.getX();
        float y = event.getY();
        //因为行设置行间距后，文本在顶部，空白在底部，加上一半的行间距，可以实现与文本垂直居中一样的效果
        y = y + textView.getLineSpacingExtra() / 2 - textView.getPaddingTop();
        //在顶部padding空白处点击
        /*if (y < 0) {
            return false;
        }*/
        int singleLineHeight = textView.getLineHeight();
        StaticLayout layout = (StaticLayout) textView.getLayout();
        //获取点击当前所在行
        int lineNumber = (int) (y / singleLineHeight);
        if (lineNumber < 0) {
            lineNumber = 0;
        }
        touchLine = lineNumber;
        if (lineNumber <= textView.getLineCount() - 1) {
            int lineStart = layout.getLineStart(lineNumber);
            int lineEnd = layout.getLineEnd(lineNumber);
            String lineText = textView.getText().toString().substring(lineStart, lineEnd);
            //Log.d("TAG", "getTouchWord : line = " + lineNumber + "  lineStart = " + lineStart + "  lineEnd = " + lineEnd + "  text =" + lineText);
            //int lineWidth = (int) layout.getLineWidth(lineNumber);
            if (!TextUtils.isEmpty(lineText) && lineText.length() > 0) {
                float minCha = 0;
                int minOneIndex = 0;
                for (int i = 0; i < lineText.length(); i++) {
                    float oneX = layout.getSecondaryHorizontal(lineStart + i);
                    //String charSeq = String.valueOf(lineText.charAt(i));
                    //float oneX = StaticLayout.getDesiredWidth(charSeq, paint);
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
                int startIndex = 0;
                int endIndex = 0;
                char ch = lineText.charAt(minOneIndex);
                if (StringUtils.isEnChar(ch)) {//点击单词
                    for (int i = minOneIndex; i < lineText.length(); i++) {
                        char c = lineText.charAt(i);
                        if (!StringUtils.isEnChar(c)) {
                            endIndex = i;
                            break;
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
                    touchWordStartIndex = startIndex;
                    touchWordEndIndex = endIndex;
                    int start = startIndex + lineStart;
                    int end = endIndex + lineStart;
                    touchWordStartX = layout.getSecondaryHorizontal(start);
                    touchWordEndX = layout.getSecondaryHorizontal(end);
                    invalidate();

                    if (pageViewListener != null) {
                        pageViewListener.showTransDialog(word);
                    }
                    /*SpannableStringBuilder span = new SpannableStringBuilder(textView.getText());
                     int start = startIndex + lineStart;
                     int end = endIndex + lineStart;
                     span.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     setText(span);*/
                } else {
                    if (pageViewListener != null) {
                        pageViewListener.showBottom();
                    }
                }
            }
        }
    }
}
