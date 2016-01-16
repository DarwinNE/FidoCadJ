package net.sourceforge.fidocadj.macropicker;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import net.sourceforge.fidocadj.R;

/** Expandable tree for browsing macros in the library.
    From
    http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/

    <pre>
    This file is part of FidoCadJ.

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014-2015 Kohta Ozaki, Davide Bucci
    </pre>

    @author Davide Bucci

*/
public class ExpandableMacroListView extends BaseExpandableListAdapter
{

    private final Context _context;
    private final List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    /** The constructor.
        @param context the context.
        @param listDataHeader a list of header titles.
        @param listChildData child data in format of header title, child title.
    */
    public ExpandableMacroListView(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData)
    {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /** Get a particular child node.
        @param groupPosition position in the group.
        @param childPosition position among the children.
        @return the child in the list data collection.
    */
    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    /** Get a particular child ID.
        @param groupPosition position in the group.
        @param childPosition position among the children.
        @return the child's ID.
    */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    /** Get a particular child View.
        @param groupPosition position in the group.
        @param childPosition position among the children.
        @param isLastChild true if it is the last child.
        @param t_ConvertView TODO: describe it.
        @param parent the parent.
        @return the child's View.
    */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
        boolean isLastChild, View t_convertView, ViewGroup parent)
    {
        View convertView=t_convertView;
        final String childText = (String) getChild(groupPosition,
            childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    /** Get a the number of children of a group.
        @param groupPosition position in the group.
        @return the number of children.
    */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
            .size();
    }

    /** Get a group.
        @param groupPosition position of the group.
        @return the group.
    */
    @Override
    public Object getGroup(int groupPosition)
    {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View t_convertView, ViewGroup parent)
    {
        View convertView=t_convertView;

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}