package com.retor.vknews.newsfragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.retor.vklib.DialogsBuilder;
import com.retor.vknews.R;
import com.retor.vknews.presenters.INewsPresenter;
import com.retor.vknews.presenters.IView;
import com.retor.vknews.presenters.VKNews;

import java.util.ArrayList;

/**
 * Created by retor on 23.06.2015.
 */
public class NewsFragment extends Fragment implements IView<VKNews> {
    private INewsPresenter presenter;
    private ProgressDialog progressDialog;
    private ArrayList<VKNews> arrayList = new ArrayList<>();
    private MAdapter adapter;
    private SwipeRefreshLayout swiper;
    private boolean loading = false;
    private LinearLayoutManager linearLayoutManager;
    private String next_from;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.presenter = new NewsPresenter(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new MAdapter(getActivity(), new ArrayList<VKNews>());
        if (savedInstanceState != null && (arrayList = (ArrayList<VKNews>) savedInstanceState.getSerializable("array")) != null) {
            adapter.setItems(arrayList);
            next_from = savedInstanceState.getString("next_from");
        } else {
            showProgress();
            presenter.getNews(null);
        }
        View out = inflater.inflate(R.layout.news_recycler, container, false);
        swiper = (SwipeRefreshLayout) out.findViewById(R.id.swipe);
        RecyclerView recyclerView = (RecyclerView) out.findViewById(R.id.recycle);
        initSwipeRefresh(swiper);
        initRecycler(recyclerView);
        return out;
    }

    private void initRecycler(final RecyclerView input) {
        this.linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        input.setLayoutManager(linearLayoutManager);
        input.setHasFixedSize(true);
        RecyclerView.ItemAnimator im = new DefaultItemAnimator();
        im.setAddDuration(1000);
        input.setItemAnimator(im);
        input.setAdapter(adapter);
        /*This listener adding scroll RecyclerView to Up if it not set RefreshLayout take first scrolling to up and do refresh*/
        input.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                getActivity().findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);
                swipeActivate(recyclerView);
                next_from();
            }
        });
    }

    private void swipeActivate(RecyclerView recyclerView) {
        int topRowVerticalPosition =
                (recyclerView == null || recyclerView.getChildCount() == 0) ?
                        0 : recyclerView.getChildAt(0).getTop();
        swiper.setEnabled(topRowVerticalPosition >= 0);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
    }

    private void next_from() {
        int visibleCount = linearLayoutManager.getChildCount();
        int totalCount = linearLayoutManager.getItemCount();
        int firstVisible = linearLayoutManager.findFirstVisibleItemPosition();
        int visibleThreshold = 3;
        if (!loading && (totalCount - visibleCount)
                <= (firstVisible + visibleThreshold)) {
            loading = true;
            if (next_from != null) {
                presenter.getNews(next_from);
            }
        }
    }

    private void initSwipeRefresh(final SwipeRefreshLayout input) {
        input.setVerticalScrollBarEnabled(true);
        input.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                input.setRefreshing(false);
                presenter.getNews(null);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("array", arrayList);
        outState.putString("next_from", next_from);
        super.onSaveInstanceState(outState);
    }

    private void showProgress() {
        loading = true;
        progressDialog = DialogsBuilder.createProgress(getActivity(), "Loading...");
        progressDialog.show();
    }

    private void closeProgress() {
        loading = false;
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onItem(VKNews item) {
//        Log.d("NewsList", item.getPost().post_type);
        arrayList.add(item);
        adapter.addItem(item);
        next_from = item.getNext_from();
        closeProgress();
    }
}
