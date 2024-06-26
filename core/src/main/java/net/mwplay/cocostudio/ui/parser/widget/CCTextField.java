/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.mwplay.cocostudio.ui.parser.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import com.seraphel.shooting.constant.Asset;
import net.mwplay.cocostudio.ui.BaseCocoStudioUIEditor;
import net.mwplay.cocostudio.ui.model.FileData;
import net.mwplay.cocostudio.ui.model.objectdata.widget.TextFieldObjectData;
import net.mwplay.cocostudio.ui.parser.WidgetParser;
import net.mwplay.cocostudio.ui.widget.TTFLabelStyle;

//import net.mwplay.cocostudio.ui.util.FontUtil;

public class CCTextField extends WidgetParser<TextFieldObjectData> {

	/**
	 * 默认文字,为了支持基本字符输入
	 */
	final String defaultText = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*";

	@Override
	public String getClassName () {
		return "TextFieldObjectData";
	}

	@Override
	public Actor parse (BaseCocoStudioUIEditor editor, TextFieldObjectData widget) {
		String path = "example/Isans-15.fnt";
		BitmapFont bitmapFont = Asset.fetch().loadFont(path);
		bitmapFont.getData().setScale(0.7f);
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = bitmapFont;
		textFieldStyle.fontColor = Color.WHITE;

		TextField textField = new TextField(widget.LabelText, textFieldStyle) {

			@Override
			public void setText (String text) {
				String sumText = text + getMessageText() + defaultText;

                /*getStyle().font = FontUtil.createFont(
						labelStyle.getFontFileHandle(), sumText,
                        labelStyle.getFontSize());*/

				super.setText(text);
			}

			@Override
			public void setMessageText (String messageText) {

				String sumText = messageText + getText() + defaultText;

                /*getStyle().font = FontUtil.createFont(
                    labelStyle.getFontFileHandle(), sumText,
                    labelStyle.getFontSize());*/
				super.setMessageText(messageText);
			}

		};

		textField.setMaxLength(widget.MaxLengthText);
		textField.setMessageText(widget.PlaceHolderText);
		textField.setPasswordMode(widget.PasswordEnable);
		textField.setPasswordCharacter(widget.PasswordStyleText);
		return textField;
	}
}
