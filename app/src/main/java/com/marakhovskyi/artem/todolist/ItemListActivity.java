package com.marakhovskyi.artem.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;

    private DBHelper dbHelper;
    private ItemsManager itemsManager;
    private TextView noItemsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        dbHelper = new DBHelper(this);
        itemsManager = new ItemsManager(dbHelper);

        noItemsTextView = (TextView) findViewById(R.id.no_elements_text_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane) {
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);

                    context.startActivity(intent);
                }
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.item_list);
        setupRecyclerView(recyclerView);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                this,
                itemsManager,
                mTwoPane,
                noItemsTextView));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<ToDoItem> mValues;
        private final boolean mTwoPane;
        private final ItemsManager itemsManager;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoItem item = (ToDoItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };
        private CompoundButton.OnCheckedChangeListener checkBoxListener
                = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToDoItem item = (ToDoItem) buttonView.getTag();
                item.isCompleted = isChecked;
                itemsManager.upsert(item);
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      ItemsManager itemsManager,
                                      boolean twoPane,
                                      TextView noItemsView) {
            mValues = itemsManager.getItems();
            mParentActivity = parent;
            mTwoPane = twoPane;
            this.itemsManager = itemsManager;

            noItemsView.setVisibility(mValues.size() != 0 ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.titleTextView.setText(mValues.get(position).title);
            holder.detailsTextView.setText(mValues.get(position).details);
            holder.completedCheckbox.setChecked(mValues.get(position).isCompleted);
            holder.completedCheckbox.setTag(mValues.get(position));
            holder.creationTextView.setText(Utils.DateFormat.format(mValues.get(position).creationDate));

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.completedCheckbox.setOnCheckedChangeListener(checkBoxListener);
            holder.completedCheckbox.setTag(mValues.get(position));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView titleTextView;
            final TextView detailsTextView;
            final TextView creationTextView;
            final CheckBox completedCheckbox;

            ViewHolder(View view) {
                super(view);
                titleTextView = (TextView) view.findViewById(R.id.title);
                detailsTextView = (TextView) view.findViewById(R.id.details);
                creationTextView = (TextView) view.findViewById(R.id.creation_date);
                completedCheckbox = (CheckBox) view.findViewById(R.id.completed_check);
            }
        }
    }
}
