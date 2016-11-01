package com.example.user.todo.flow.todo.main;


import com.example.user.todo.database.DummyData;
import com.example.user.todo.database.MyDbHelper;
import com.example.user.todo.flow.todo.list.model.TodoViewModel;
import com.example.user.todo.service.TodoGson;
import com.example.user.todo.service.TodoService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by User on 1/11/2559.
 */
@RunWith(RobolectricTestRunner.class)
public class TodoActivityPresenterTest {

    @Mock
    TodoActivityContract.View mockView;

    @Mock
    TodoService mockService;

    @Mock
    Call<TodoGson> mockTodoCall;
    @Mock
    MyDbHelper dbHelper;
    @Captor
    private ArgumentCaptor<Callback<TodoGson>> callbackArgumentCaptor;

    private TodoGson todoGson = new TodoGson();
    private TodoActivityPresenter mPresenter;
    private String userId = DummyData.USER_ID;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Robolectric.buildActivity(TodoActivity.class);
        mPresenter = new TodoActivityPresenter(mockView, mockService, DummyData.USER_ID, dbHelper);

        Mockito.when(mockView.isActive()).thenReturn(true);
    }

    @Test
    public void loadAllDatasAndLoadIntoView_Success() {
        Mockito.when(mockService.getTodoData(userId)).thenReturn(mockTodoCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<TodoGson> callback = (Callback) invocation.getArguments()[0];
                callback.onResponse(null, Response.success(todoGson));
                return null;
            }
        }).when(mockTodoCall).enqueue(any(Callback.class));

        mPresenter.loadData(true);
        verify(mockView).setPresenter(any(TodoActivityContract.Presenter.class));
        verify(mockView).showLoadingIndicator(true);
        verify(mockTodoCall).enqueue(callbackArgumentCaptor.capture());
        verify(mockView).isActive();
    }

    @Test
    public void setData() {
        Mockito.when(mockService.getTodoData(userId)).thenReturn(mockTodoCall);

        mPresenter.setData(new ArrayList<TodoViewModel>());
        verify(mockView).setPresenter(any(TodoActivityContract.Presenter.class));
        verify(mockView).isActive();
        verify(mockView).showLoadingIndicator(false);
        verify(mockView).showTodoListData(new ArrayList<TodoViewModel>());
    }

    @Test
    public void deleteDataFromDb() throws Exception {

    }

    @Test
    public void addDataToDb() throws Exception {

    }

    @Test
    public void markDbAsDone() throws Exception {

    }

    @Test
    public void start() throws Exception {

    }

}