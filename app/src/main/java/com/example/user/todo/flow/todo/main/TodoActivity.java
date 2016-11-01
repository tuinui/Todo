package com.example.user.todo.flow.todo.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.user.todo.R;
import com.example.user.todo.database.DummyData;
import com.example.user.todo.database.MyDbHelper;
import com.example.user.todo.database.TodoDB;
import com.example.user.todo.flow.todo.list.TodoListFragment;
import com.example.user.todo.flow.todo.list.model.TodoViewModel;
import com.example.user.todo.service.TodoService;
import com.example.user.todo.util.MyAnimationUtils;
import com.example.user.todo.util.PopupMenuUtils;
import com.example.user.todo.util.RetrofitUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoActivity extends AppCompatActivity implements TodoActivityContract.View {
    @BindView(R.id.toolbar_generic)
    Toolbar mToolbar;
    @BindView(R.id.tablayout_generic)
    TabLayout mTabLayout;
    @BindView(R.id.swiperefreshlayout_content)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.viewpager_todo)
    ViewPager mViewPager;

    private TodoActivityContract.Presenter mPresenter;

    private TodoPagerAdapter mPagerAdapter;
    private final int POSITION_PENDING = 0;
    private final int POSITION_DONE = 1;

    private TodoListFragment mDoneFragment = TodoListFragment.newInstance(TodoViewModel.DONE, new TodoListFragment.FragmentInteractionListener() {
        @Override
        public void onEditStateChange(boolean isEdit) {
            TodoActivity.this.invalidateState(isEdit);
        }

        @Override
        public void markAsDone(TodoDB data) {
            mPresenter.markDbAsDone(data);
        }
    });
    private TodoListFragment mPendingFragment = TodoListFragment.newInstance(TodoViewModel.PENDING, new TodoListFragment.FragmentInteractionListener() {
        @Override
        public void onEditStateChange(boolean isEdit) {
            TodoActivity.this.invalidateState(isEdit);
        }

        @Override
        public void markAsDone(TodoDB data) {
            mPresenter.markDbAsDone(data);
        }
    });

    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.loadData(true);
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getPosition() == POSITION_DONE) {
                invalidateState(mDoneFragment.isEditState());
            } else if (tab.getPosition() == POSITION_PENDING) {
                invalidateState(mPendingFragment.isEditState());
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private Toolbar.OnMenuItemClickListener mPendingMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.menu_item_add) {
                showTodoAddingDialog();
                return true;
            }
            return false;
        }
    };

    @BindString(R.string.pending)
    String mPageTitlePending;
    @BindString(R.string.done)
    String mPageTitleDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        ButterKnife.bind(this);
        initView();

        new TodoActivityPresenter(this, RetrofitUtils.getRetrofit().create(TodoService.class), DummyData.USER_ID, MyDbHelper.with(this));
    }

    private void invalidateState(boolean isEditState) {
        invalidateToolbar(isEditState);
        TodoListFragment currentFragment = getCurrentFragment();


        if (null != currentFragment) {
            currentFragment.setEditState(isEditState);
        }

        if (!isEditState) {
            if (currentFragment == mPendingFragment) {
                mToolbar.inflateMenu(R.menu.menu_add);
                mToolbar.setOnMenuItemClickListener(mPendingMenuItemClickListener);
            } else {
                clearToolbarMenu();
            }
        }
    }

    private TodoListFragment getCurrentFragment() {
        return findFragmentByPosition(mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    private void invalidateToolbar(boolean editState) {

        clearToolbarMenu();
        if (editState) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invalidateState(false);
                }
            });

            mToolbar.inflateMenu(R.menu.menu_delete);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menu_item_delete) {
                        TodoListFragment fragment = getCurrentFragment();
                        if(null != fragment){
                            invalidateState(false);
                            mPresenter.deleteDataFromDb(getCurrentFragment().getCurrentCheckedIds());
                        }

                    }
                    return false;
                }
            });
        }
    }

    private void clearToolbarMenu() {
        mToolbar.getMenu().clear();
        mToolbar.setNavigationIcon(null);
        mToolbar.setNavigationOnClickListener(null);
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshListener);
        initToolbar();
        mPagerAdapter = new TodoPagerAdapter(getSupportFragmentManager(), getFragments());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void initToolbar() {
        mToolbar.inflateMenu(R.menu.menu_add);
        mToolbar.setOnMenuItemClickListener(mPendingMenuItemClickListener);
    }

    private LinkedHashMap<String, TodoListFragment> getFragments() {
        LinkedHashMap<String, TodoListFragment> fragments = new LinkedHashMap<>();
        fragments.put(mPageTitlePending, mPendingFragment);
        fragments.put(mPageTitleDone, mDoneFragment);
        return fragments;
    }


    @Override
    public void showLoadingIndicator(boolean active) {
        mSwipeRefreshLayout.setRefreshing(active);
    }


    @Override
    public void showTodoListData(List<TodoViewModel> todoGsons) {
        replaceData(todoGsons);

    }


    private void replaceData(List<TodoViewModel> todoGsons) {
        List<TodoViewModel> pendingModels = new ArrayList<>();
        List<TodoViewModel> doneModels = new ArrayList<>();

        for (TodoViewModel data : todoGsons) {
            if (data.getState() == TodoViewModel.DONE) {
                doneModels.add(data);
            } else if (data.getState() == TodoViewModel.PENDING) {
                pendingModels.add(data);
            }
        }


        mDoneFragment.replaceData(doneModels);
        mPendingFragment.replaceData(pendingModels);
    }


    @Override
    public void setPresenter(TodoActivityContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return !isFinishing() && !isDestroyed();
    }

    private void showTodoAddingDialog() {
        PopupMenuUtils.showPopupAlertEditTextMenu(this, "Add", "", new PopupMenuUtils.Action1<String>() {
            @Override
            public void call(String data) {
                addToDatabase(data);
            }
        });
    }

    private void addToDatabase(String todoName) {
        mPresenter.addDataToDb(todoName);
//        Toast.makeText(this, "Add to db  : " + todoName, Toast.LENGTH_LONG).show();
    }

    public class TodoPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<TodoListFragment> fragments = new ArrayList<>();
        private List<String> pageTitles = new ArrayList<>();

        TodoPagerAdapter(FragmentManager fm, LinkedHashMap<String, TodoListFragment> fragments) {
            super(fm);
            this.fragments = new ArrayList<>(fragments.values());
            this.pageTitles = new ArrayList<>(fragments.keySet());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles.get(position);
        }

        @Override
        public TodoListFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    public TodoListFragment findFragmentByPosition(int position) {
        if (position == POSITION_DONE) {
            return mDoneFragment;
        } else if (position == POSITION_PENDING) {
            return mPendingFragment;
        } else {
            return null;
        }
    }



    private void showSnackBar(){

    }
}
