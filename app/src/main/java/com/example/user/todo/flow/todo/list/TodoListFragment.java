package com.example.user.todo.flow.todo.list;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.user.todo.R;
import com.example.user.todo.base.BaseFragment;
import com.example.user.todo.database.TodoDB;
import com.example.user.todo.flow.todo.list.model.TodoViewModel;
import com.example.user.todo.util.MyAnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Saran on 1/11/2559.
 */

public class TodoListFragment extends BaseFragment {
    private TodoRecyclerAdapter mAdapter = new TodoRecyclerAdapter() {
        public List<TodoViewModel> pendingRemovalDatas = new ArrayList<>();
        private HashMap<TodoViewModel, Runnable> pendingRunnables = new HashMap<>();

        private Handler handler = new Handler(); // hanlder for running delayed runnables

        private static final int PENDING_REMOVAL_TIMEOUT = 5000;

        @Override
        public void setOnLongClickRoot(View v) {
            v.setOnLongClickListener(onRootLongClick);
        }

        private View.OnLongClickListener onRootLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setEditState(!isEditState);
                listener.onEditStateChange(isEditState);
                return true;
            }
        };


        public void pendingMarkAsDone(final TodoViewModel data, int position) {
            if (!pendingRemovalDatas.contains(data)) {
                pendingRemovalDatas.add(data);
                notifyItemChanged(position);

                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        removeFromAdapter(data);
                        markAsDone(data);
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(data, pendingRemovalRunnable);
            }
        }

        public void markAsDone(TodoViewModel data) {
            listener.markAsDone(data.getData());
        }

        public void removeFromAdapter(TodoViewModel item) {
            if (pendingRemovalDatas.contains(item)) {
                pendingRemovalDatas.remove(item);
            }
            if (mDatas.contains(item)) {
                mDatas.remove(item);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            if (payloads.isEmpty()) {
                return;
            }
            Bundle o = (Bundle) payloads.get(0);
            for (String key : o.keySet()) {
                if (TextUtils.equals(key, KEY_IS_EDIT_STATE)) {
                    isEditState = o.getBoolean(KEY_IS_EDIT_STATE);
                    bindData(holder, position, isEditState);
                }
            }
        }

        private void bindData(final TodoRecyclerAdapter.ViewHolder holder, int position, boolean isEditState) {
            final TodoViewModel data = mDatas.get(position);
            holder.tvName.setText(data.getId() + ". " + data.getName());
            holder.buttonUndo.setOnClickListener(null);
            holder.layoutUndoOverlay.setOnClickListener(null);
            holder.layoutUndoOverlay.setVisibility(View.INVISIBLE);


            if (isEditState) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(data.isChecked());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TodoViewModel data = mDatas.get(holder.getLayoutPosition());
                        boolean newValue = !holder.checkBox.isChecked();
                        holder.checkBox.setChecked(newValue);
                        data.setChecked(newValue);
                    }
                });
            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.itemView.setOnLongClickListener(onRootLongClick);
                if (mState == TodoViewModel.PENDING) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TodoViewModel data = mDatas.get(holder.getAdapterPosition());
                            pendingMarkAsDone(data, holder.getAdapterPosition());
                        }
                    });

                    if (pendingRemovalDatas.contains(data)) {
                        // we need to show the "undo" state of the row
                        holder.layoutUndoOverlay.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Landing).duration(300).interpolate(new FastOutSlowInInterpolator()).playOn(holder.layoutUndoOverlay);
                        holder.buttonUndo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // user wants to undo the removal, let's cancel the pending task
                                Runnable pendingRemovalRunnable = pendingRunnables.get(data);
                                pendingRunnables.remove(data);
                                if (pendingRemovalRunnable != null)
                                    handler.removeCallbacks(pendingRemovalRunnable);
                                pendingRemovalDatas.remove(data);
                                // this will rebind the row in "normal" state
                                notifyItemChanged(mDatas.indexOf(data));
                            }
                        });
                    }
                }

            }

        }

        @Override
        public void onBindViewHolder(final TodoRecyclerAdapter.ViewHolder holder, int position) {
            if (getItemCount() <= position) {
                return;
            }
            bindData(holder, position, isEditState);


        }

        @Override
        public int getItemCount() {
            if (null == mDatas) {
                return 0;
            }
            return mDatas.size();
        }
    };

    private FragmentInteractionListener listener;
    private RecyclerView mRecyclerView;
    private boolean isEditState;
    private List<TodoViewModel> mDatas = new ArrayList<>();
    private static final String KEY_TODO_LIST_STATE = "TODO_LIST_STATE";
    private
    @TodoViewModel.State
    int mState = TodoViewModel.PENDING;
    private TextView mNoDataView;

    public static TodoListFragment newInstance(@TodoViewModel.State int state, FragmentInteractionListener listener) {

        Bundle args = new Bundle();
        args.putInt(KEY_TODO_LIST_STATE, state);
        TodoListFragment fragment = new TodoListFragment();
        fragment.setListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mState = getArguments().getInt(KEY_TODO_LIST_STATE, TodoViewModel.PENDING);
        }
    }

    public void setEditState(boolean isEdit) {
        isEditState = isEdit;
        changeViewEditState(isEdit);
    }

    private void changeViewEditState(boolean isEdit) {
        if (mAdapter != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(TodoRecyclerAdapter.KEY_IS_EDIT_STATE, isEdit);
            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount(), bundle);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_todo_list);
        mNoDataView = (TextView) v.findViewById(R.id.textview_todo_list_nodata);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void replaceData(List<TodoViewModel> datas) {
        if (null == datas) {
            MyAnimationUtils.animateRevealCompat(mNoDataView, MyAnimationUtils.VISIBLE);
        } else {
            if(datas.isEmpty()){
                MyAnimationUtils.animateRevealCompat(mNoDataView, MyAnimationUtils.VISIBLE);
            }else{
                MyAnimationUtils.animateRevealCompat(mNoDataView, MyAnimationUtils.GONE);
            }

            mDatas.clear();
            mAdapter.notifyDataSetChanged();
            mDatas.addAll(datas);
            mAdapter.notifyDataSetChanged();
        }


    }

    public void setListener(FragmentInteractionListener listener) {
        this.listener = listener;
    }


    public interface FragmentInteractionListener {
        void onEditStateChange(boolean isEdit);

        void markAsDone(TodoDB data);
    }

    public List<Long> getCurrentCheckedIds() {
        List<Long> checkedIds = new ArrayList<>();
        if (null == mDatas || mDatas.isEmpty()) {
            return checkedIds;
        }
        for (TodoViewModel data : mDatas) {
            if (data.isChecked()) {
                checkedIds.add(data.getId());
            }
        }
        return checkedIds;
    }

    public boolean isEditState() {
        return isEditState;
    }


}
