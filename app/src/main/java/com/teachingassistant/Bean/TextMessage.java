package com.teachingassistant.Bean;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.widget.TextView;

import com.teachingassistant.MyApplication;
import com.teachingassistant.Presentation.Adapter.ChatAdapter;
import com.teachingassistant.R;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMTextElem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文本消息
 */

public class TextMessage extends Message {

    /**
     * 一系列构造函数
     */
    //由TIM消息构造
    public TextMessage(TIMMessage message){
        this.message = message;
    }

    //由字符串构造
    public TextMessage(String s){
        message = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(s);
        message.addElement(elem);
    }


    public TextMessage(TIMMessageDraft draft){
        message = new TIMMessage();
        for (TIMElem elem : draft.getElems()){
            message.addElement(elem);
        }
    }

    /**
     * 根据图片在输入文本中的位置来排序图片
     * @param editInput
     * @param array
     * @return
     */
    private List<ImageSpan> sortByIndex(final Editable editInput, ImageSpan []array){
        ArrayList<ImageSpan> sortList = new ArrayList<>();
        for (ImageSpan span : array){
            sortList.add(span);
        }
        Collections.sort(sortList, new Comparator<ImageSpan>() {
            @Override
            public int compare(ImageSpan lhs, ImageSpan rhs) {
                return editInput.getSpanStart(lhs) - editInput.getSpanStart(rhs);
            }
        });

        return sortList;
    }

    //用输入控件的输入内容来创建
    public TextMessage(Editable s){
        message = new TIMMessage();
        //从文本中抓入所有的图片
        ImageSpan[] spans = s.getSpans(0, s.length(), ImageSpan.class);
        //排序
        List<ImageSpan> listSpans = sortByIndex(s, spans);
        //开始构造message
        int currentIndex = 0;
        for (ImageSpan span : listSpans){
            //逐个获取每个图片的开始坐标和结束坐标
            int startIndex = s.getSpanStart(span);
            int endIndex = s.getSpanEnd(span);
            //比较当前坐标和开始坐标
            if (currentIndex < startIndex){
                //抓取图片间隙中的文字，构造textelem
                TIMTextElem textElem = new TIMTextElem();
                textElem.setText(s.subSequence(currentIndex, startIndex).toString());
                message.addElement(textElem);
            }
            //添加当前表情，构造表情项目
            TIMFaceElem faceElem = new TIMFaceElem();
            int index = Integer.parseInt(s.subSequence(startIndex, endIndex).toString());
            faceElem.setIndex(index);
            //设置图片的概要例如  [撇嘴]
            if (index < EmoticonUtil.emoticonData.length){
                faceElem.setData(EmoticonUtil.emoticonData[index].getBytes(Charset.forName("UTF-8")));
            }
            message.addElement(faceElem);
            currentIndex = endIndex;
        }
        //处理最后一张表情之后的文本部分
        if (currentIndex < s.length()){
            TIMTextElem textElem = new TIMTextElem();
            textElem.setText(s.subSequence(currentIndex, s.length()).toString());
            message.addElement(textElem);
        }

    }
    /**
     * 消息填充，将消息填充到界面中。在ChatAdapter中的getview调用
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        boolean hasText = false;
        //先将填充消息部分清空
        clearView(viewHolder);
        //构建消息显示视图
        TextView tv = new TextView(MyApplication.getContext());
        //设置属性
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        tv.setTextColor(MyApplication.getContext().getResources().getColor(isSelf()
                ? R.color.white : R.color.black));
        //解析消息内容
        List<TIMElem> elems = new ArrayList<>();
        for (int i = 0; i < message.getElementCount(); ++i){
            elems.add(message.getElement(i));
            if (message.getElement(i).getType() == TIMElemType.Text){
                hasText = true;
            }
        }
        //处理消息为字符串
        SpannableStringBuilder stringBuilder = getString(elems, context);
        if (!hasText){
            stringBuilder.insert(0," ");
        }
        //设置显示
        tv.setText(stringBuilder);
        getBubbleView(viewHolder).addView(tv);
        //显示状态
        showStatus(viewHolder);
    }

    /**
     * 获取概要
     * @return 概要字符串
     */
    @Override
    public String getSummary() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i<message.getElementCount(); ++i){
            switch (message.getElement(i).getType()){
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) message.getElement(i);
                    //表情转化为字符串
                    byte[] data = faceElem.getData();
                    if (data != null){
                        result.append(new String(data, Charset.forName("UTF-8")));
                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) message.getElement(i);
                    result.append(textElem.getText());
                    break;
            }

        }
        return result.toString();
    }

    @Override
    public void save() {
        //暂不保存
    }

    /**
     * 解析文字函数，包括提取消息中的表情和文本，并显示
     * @param elems 消息列表
     * @param context 上下文
     * @return 处理后的可以显示带格式的String
     */
    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i<elems.size(); ++i){
            switch (elems.get(i).getType()){
                //表情
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    try{
                        //获取资源管理
                        AssetManager am = context.getAssets();
                        //获取图片
                        InputStream is = am.open(String.format("emoticon/%d.gif", faceElem.getIndex()));
                        if (is == null) continue;
                        //处理图片
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        //缩放图片到2*2
                        matrix.postScale(2, 2);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                        //设置ImageSpan，放入表情。
                        ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
                        //在字符串末尾放入对应的图片ID
                        stringBuilder.append(String.valueOf(faceElem.getIndex()));
                        //用图片替换字符
                        stringBuilder.setSpan(span, startIndex,
                                startIndex + String.valueOf(faceElem.getIndex()).length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        is.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
                //文本
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
            }

        }
        return stringBuilder;
    }
}
