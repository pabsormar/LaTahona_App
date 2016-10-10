/*
 * PodListen is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License (GPL) as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * PodListen is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See included copy of GNU GPLv3.0 for more details. Alternatively, you can find its text
 * at http://www.gnu.org/licenses/gpl-3.0.txt
 */

package org.deafsapps.latahona.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

// This class is a workaround to an issue found when including a 'Spannable' object in a 'TextView'
public class PatchedTextView extends TextView
{
    public PatchedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public PatchedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PatchedTextView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (ArrayIndexOutOfBoundsException e) {
            setText(getText().toString());
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setGravity(int gravity) {
        try {
            super.setGravity(gravity);
        } catch (ArrayIndexOutOfBoundsException e) {
            setText(getText().toString());
            super.setGravity(gravity);
        }
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        try {
            super.setText(text, type);
        } catch (ArrayIndexOutOfBoundsException e){
            setText(text.toString());
        }
    }
}
