package com.uugty.uu.common.myview;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.R.bool;
import android.widget.EditText;  
import android.widget.Toast;  
import android.content.Context;  
import android.text.Editable;  
import android.text.GetChars;
import android.text.Selection;  
import android.text.Spannable;  
import android.text.TextWatcher;  
import android.util.AttributeSet;  
import android.util.Log;
  
public class TextViewContent extends EditText {  
    private int cursorPos;
    private String inputAfterText;  
    
    private boolean resetText;  
  
    private Context mContext;  
  
    public TextViewContent(Context context) {  
        super(context);  
        this.mContext = context;  
        initEditText();  
    }  
  
    public TextViewContent(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        this.mContext = context;  
        initEditText();  
    }  
  
    public TextViewContent(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
        this.mContext = context;  
        initEditText();  
    }  
    private void initEditText() {  
        addTextChangedListener(new TextWatcher() {  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {  
                if (!resetText) {  
                    cursorPos = getSelectionEnd();
                    inputAfterText= s.toString();  
                }  
  
            }  
  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
                if (!resetText) {
                    if(before==0)
                    {
                        CharSequence input = s.subSequence(cursorPos, cursorPos + count);  
                        String str = stringFilter(s.toString());
                        if (containsEmoji(input.toString())||!str.equals(s.toString())||containsChinese(input.toString())) {  
                            resetText = true;  
                            if(containsEmoji(input.toString())||containsChinese(input.toString())){
                            	setText(inputAfterText);
                            }
                            if(!str.equals(s.toString())){
                            	setText(str); 
                            }
                            CharSequence text = getText();  
                            if (text instanceof Spannable) {  
                                Spannable spanText = (Spannable) text;  
                                Selection.setSelection(spanText,spanText.length());  
                            }
                        }
                    }
                } else {  
                    resetText = false;  
                }  
            }  
  
            @Override  
            public void afterTextChanged(Editable editable) {  
  
            }  
        });  
    }  
    public static boolean containsEmoji(String source) {  
        int len = source.length();  
        for (int i = 0; i < len; i++) {  
            char codePoint = source.charAt(i);  
            if (!isEmojiCharacter(codePoint)) {
                return true;  
            }  
        }  
        return false;  
    }  
    private static boolean isEmojiCharacter(char codePoint) {
       int b=codePoint;
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||  
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)&&(codePoint!=0x263a)) ||  
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)  
                && (codePoint <= 0x10FFFF));  
    }  
	public static String stringFilter(String str)throws PatternSyntaxException{

        String regEx = "[/\\:*?<>|\"\n\t]";

        Pattern p = Pattern.compile(regEx);

        Matcher m = p.matcher(str);

        return m.replaceAll("");

    }
	  /**
     * 判定输入汉字
     * @param string
     * @return
     */
    public static boolean isChinese(char string) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(string);
    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
         || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
        return true;
    }
    return false;
}
    public static boolean containsChinese(String source) {  
        int len = source.length();  
        for (int i = 0; i < len; i++) {  
            char codePoint = source.charAt(i);  
            if (isChinese(codePoint)) {
                return true;  
            }  
        }  
        return false;  
    }  
}  