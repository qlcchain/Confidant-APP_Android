/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.view.MyImageSpan;

public class EaseSmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";
    
	public static final String ee_1 = "[微笑]";
	public static final String ee_2 = "[撇嘴]";
	public static final String ee_3 = "[色]";
	public static final String ee_4 = "[发呆]";
	public static final String ee_5 = "[得意]";
	public static final String ee_6 = "[流泪]";
	public static final String ee_7 = "[害羞]";
	public static final String ee_8 = "[闭嘴]";
	public static final String ee_9 = "[睡]";
	public static final String ee_10 = "[大哭]";
	public static final String ee_11 = "[尴尬]";
	public static final String ee_12 = "[发怒]";
	public static final String ee_13 = "[调皮]";
	public static final String ee_14 = "[呲牙]";
	public static final String ee_15 = "[惊讶]";
	public static final String ee_16 = "[难过]";
	public static final String ee_17 = "[酷]";
	public static final String ee_18 = "[冷汗]";
	public static final String ee_19 = "[抓狂]";
	public static final String ee_20 = "[吐]";
	public static final String ee_21 = "[偷笑]";
	public static final String ee_22 = "[愉快]";
	public static final String ee_23 = "[白眼]";
	public static final String ee_24 = "[傲慢]";
	public static final String ee_25 = "[饥饿]";
	public static final String ee_26 = "[困]";
	public static final String ee_27 = "[惊恐]";
	public static final String ee_28 = "[流汗]";
	public static final String ee_29 = "[憨笑]";
	public static final String ee_30 = "[悠闲]";
	public static final String ee_31 = "[奋斗]";
	public static final String ee_32 = "[咒骂]";
	public static final String ee_33 = "[疑问]";
	public static final String ee_34 = "[嘘]";
	public static final String ee_35 = "[晕]";
	public static final String ee_36 = "[疯了]";
	public static final String ee_37 = "[衰]";
	public static final String ee_38 = "[骷髅]";
	public static final String ee_39 = "[敲打]";
	public static final String ee_40 = "[再见]";
	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();
	

	static {
	    EaseEmojicon[] emojicons = EaseDefaultEmojiconDatas.getData();
		for (EaseEmojicon emojicon : emojicons) {
			addPattern(emojicon.getEmojiText(), emojicon.getIcon());
		}
	    EaseEmojiconInfoProvider emojiconInfoProvider = EaseUI.getInstance().getEmojiconInfoProvider();
	    if(emojiconInfoProvider != null && emojiconInfoProvider.getTextEmojiconMapping() != null){
	        for(Entry<String, Object> entry : emojiconInfoProvider.getTextEmojiconMapping().entrySet()){
	            addPattern(entry.getKey(), entry.getValue());
	        }
	    }
	    
	}

	/**
	 * add text and icon to the map
	 * @param emojiText-- text of emoji
	 * @param icon -- resource id or local path
	 */
	public static void addPattern(String emojiText, Object icon){
	    emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
	}
	

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Object value = entry.getValue();
	                if(value instanceof String && !((String) value).startsWith("http")){
	                    File file = new File((String) value);
	                    if(!file.exists() || file.isDirectory()){
	                        return false;
	                    }
	                    spannable.setSpan(new ImageSpan(context, Uri.fromFile(file)),
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }else{
						Drawable drawable = context.getResources().getDrawable((Integer)value);
						drawable.setBounds(0, 0, (int) context.getResources().getDimension(R.dimen.x38), (int) context.getResources().getDimension(R.dimen.x38));//这里设置图片的大小
	                    spannable.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM),
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	            }
	        }
	    }
	    
	    return hasChanges;
	}
	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmilesInput(Context context, Spannable spannable) {
		KLog.i("添加文字，。");
	    boolean hasChanges = false;
	    for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Object value = entry.getValue();
	                if(value instanceof String && !((String) value).startsWith("http")){
	                    File file = new File((String) value);
	                    if(!file.exists() || file.isDirectory()){
	                        return false;
	                    }
	                    spannable.setSpan(new ImageSpan(context, Uri.fromFile(file)),
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }else{
						Drawable drawable = context.getResources().getDrawable((Integer)value);
						drawable.setBounds(0, 0, (int) context.getResources().getDimension(R.dimen.x38), (int) context.getResources().getDimension(R.dimen.x38));//这里设置图片的大小
	                    spannable.setSpan(new MyImageSpan(drawable, 0),
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	            }
	        }
	    }

	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable);
	    return spannable;
	}

	public static Spannable getSmiledTextInput(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
		addSmilesInput(context, spannable);
	    return spannable;
	}

	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	public static int getSmilesSize(){
        return emoticons.size();
    }
    
	
}
