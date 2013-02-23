/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package group.pals.android.lib.ui.filechooser;

import group.pals.android.lib.ui.filechooser.utils.Converter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The adapter to be used in {@link android.widget.ListView}
 * 
 * @author Hai Bison
 * 
 */
public class FileAdapter extends ArrayAdapter<DataModel> {

    /**
     * Default short format for file time. Value = {@code "yyyy.MM.dd hh:mm a"}<br>
     * See <a href=
     * "http://developer.android.com/reference/java/text/SimpleDateFormat.html"
     * >API docs</a>.
     */
    public static final String DefFileTimeShortFormat = "yyyy.MM.dd hh:mm a";
    /**
     * You can set your own short format for file time by this variable. If the
     * value is in wrong format, {@link #DefFileTimeShortFormat} will be used.<br>
     * See <a href=
     * "http://developer.android.com/reference/java/text/SimpleDateFormat.html"
     * >API docs</a>.
     */
    public static String fileTimeShortFormat = DefFileTimeShortFormat;

    private final boolean MultiSelection;
    private final int SelectionMode;

    /**
     * Creates new {@link FileAdapter}
     * 
     * @param context
     *            {@link Context}
     * @param objects
     *            the data
     * @param selectionMode
     *            see {@link FileChooserActivity#SelectionMode}
     * @param multiSelection
     *            see {@link FileChooserActivity#MultiSelection}
     */
    public FileAdapter(Context context, List<DataModel> objects,
            int selectionMode, boolean multiSelection) {
        super(context, R.layout.file_item, objects);
        this.SelectionMode = selectionMode;
        this.MultiSelection = multiSelection;
    }

    /**
     * The "view holder"
     * 
     * @author Hai Bison
     * 
     */
    private static final class Bag {

        TextView txtFileName;
        TextView txtFileInfo;
        CheckBox checkboxSelection;
        ImageView imageIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataModel Data = getItem(position);
        Bag bag;

        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext())
                    .getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.file_item, null);

            bag = new Bag();
            bag.txtFileName = (TextView) convertView
                    .findViewById(R.id.text_view_filename);
            bag.txtFileInfo = (TextView) convertView
                    .findViewById(R.id.text_view_file_info);
            bag.checkboxSelection = (CheckBox) convertView
                    .findViewById(R.id.checkbox_selection);
            bag.imageIcon = (ImageView) convertView
                    .findViewById(R.id.image_view_icon);

            convertView.setTag(bag);
        } else {
            bag = (Bag) convertView.getTag();
        }

        // update view
        updateView(bag, Data, Data.getFile());

        return convertView;
    }

    /**
     * Updates the view.
     * 
     * @param bag
     *            the "view holder", see {@link Bag}
     * @param Dm
     *            {@link DataModel}
     * @param file
     *            {@link File}
     * @since v2.0 alpha
     */
    private void updateView(Bag bag, final DataModel Dm, File file) {
        // image icon
        if (file.isDirectory())
            bag.imageIcon.setImageResource(R.drawable.folder48);
        else
            bag.imageIcon.setImageResource(R.drawable.file48);

        // filename
        bag.txtFileName.setText(file.getName());

        // file info
        String time = null;
        try {
            time = new SimpleDateFormat(fileTimeShortFormat).format(file
                    .lastModified());
        } catch (Exception e) {
            try {
                time = new SimpleDateFormat(DefFileTimeShortFormat)
                        .format(file.lastModified());
            } catch (Exception ex) {
                time = new Date(file.lastModified()).toLocaleString();
            }
        }
        if (file.isDirectory())
            bag.txtFileInfo.setText(time);
        else {
            bag.txtFileInfo.setText(String.format("%s, %s",
                    Converter.sizeToStr(file.length()), time));
        }

        // checkbox
        if (MultiSelection) {
            if (SelectionMode == FileChooserActivity.FilesOnly
                    && file.isDirectory()) {
                bag.checkboxSelection.setVisibility(View.GONE);
            } else {
                bag.checkboxSelection.setVisibility(View.VISIBLE);
                bag.checkboxSelection.setFocusable(false);
                bag.checkboxSelection
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                Dm.setSelected(isChecked);
                            }
                        });
                bag.checkboxSelection.setChecked(Dm.isSelected());
            }
        } else
            bag.checkboxSelection.setVisibility(View.GONE);
    }//updateView
}
