package com.marakhovskyi.artem.todolist;

import android.app.Activity;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.internal.CollapsingTextHelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class ItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private ToDoItem mItem;
    private DBHelper dbHelper;
    private ItemsManager itemsManager;

    private EditText titleEditText;
    private EditText detailsEditText;
    private CheckBox isCompletedCheckBox;
    private int itemId;
    private TextView creationDateLabel;

    public ItemDetailFragment() {
    }

    public void saveState() {
        itemsManager.upsert(new ToDoItem(
                mItem == null ? 0 : mItem.id,
                titleEditText.getText().toString(),
                detailsEditText.getText().toString(),
                mItem == null ? new Date() : mItem.creationDate,
                isCompletedCheckBox.isChecked()
        ));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(getActivity());
        itemsManager = new ItemsManager(dbHelper);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            itemId = getArguments().getInt(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        mItem = itemsManager.getItem(itemId);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
                appBarLayout.setTitle(mItem == null ? "New item" : mItem.title);
        }

        detailsEditText = (EditText) rootView.findViewById(R.id.details);
        titleEditText = (EditText) rootView.findViewById(R.id.title);
        isCompletedCheckBox = (CheckBox) rootView.findViewById(R.id.is_completed);
        creationDateLabel = (TextView) rootView.findViewById(R.id.creation_date);

        if (mItem != null) {
            detailsEditText.setText(mItem.details);
            titleEditText.setText(mItem.title);
            ((TextView) rootView.findViewById(R.id.creation_date)).setText(Utils.DateFormat.format(mItem.creationDate));
            isCompletedCheckBox.setChecked(mItem.isCompleted);
            creationDateLabel.setText(Utils.DateFormat.format(mItem.creationDate));
        } else {
            rootView.findViewById(R.id.is_completed_container).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.creation_date_container).setVisibility(View.INVISIBLE);
        }

        return rootView;
    }
}
